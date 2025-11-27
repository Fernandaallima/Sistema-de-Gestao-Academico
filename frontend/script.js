
/* ============================================================
   Navegação entre Páginas (SPA Simples)
   ============================================================ */
/**
 * Controla qual seção do sistema está visível.
 * O layout funciona como um SPA simples: cada página é uma <section>.
 */
function showPage(id) {

  // Troca de página
  document.querySelectorAll(".page").forEach(p => p.classList.remove("active"));
  const page = document.getElementById(id);
  if (page) page.classList.add("active");

  // Destaque do menu lateral comum
  document.querySelectorAll(".menu-btn").forEach(b =>
    b.classList.remove("active")
  );

  document.querySelectorAll(".menu-btn").forEach(btn => {
    if (btn.textContent.toLowerCase().includes(id))
      btn.classList.add("active");
  });

  // --- TIMELINE ---
  document.querySelectorAll(".timeline-item").forEach(i =>
    i.classList.remove("active")
  );

  document.querySelectorAll(".timeline-item").forEach(item => {
    if (item.dataset.page === id) {
      item.classList.add("active");
    }
  });

  // Força reanimação da linha (agora corretamente)
  const menu = document.querySelector(".timeline-menu");
  if (menu) {
    menu.style.animation = "none";
    void menu.offsetWidth; // força reflow
    menu.style.animation = "";
  }

  // Atualizações de turmas
  if (id === "turmasPage") {
    carregarTurmas();
    carregarCursosEmSelect();
    carregarProfessoresEmSelect();
  }
}


/* ============================================================
   Evento inicial — DOM completamente carregado
   ============================================================ */
document.addEventListener("DOMContentLoaded", () => {

  // O sistema abre diretamente no dashboard (sem login nesta versão)
  showPage("dashboard");

  // Carregamentos iniciais de dados
  carregarCursos();
  carregarProfessores();
  carregarTurmas();
  carregarAlunos();
  carregarTurmasSelect();

  // Conecta formulários às funções de salvar
  document.getElementById("formAluno").addEventListener("submit", salvarAluno);
  document.getElementById("formCurso").addEventListener("submit", salvarCurso);
  document.getElementById("formProfessor").addEventListener("submit", salvarProfessor);
  document.getElementById("formTurma").addEventListener("submit", salvarTurma);

  // Aplica tema salvo no navegador (claro/escuro)
  const savedTheme = localStorage.getItem("sga-theme");
  if (savedTheme === "dark") document.documentElement.classList.add("dark");

  // Popula select de turmas na tela de notas
  carregarTurmas_NoSelectNotas();
});

/* ============================================================
   CRUD — ALUNOS
   ============================================================ */
/**
 * Busca todos os alunos na API e popula tabela e contadores.
 */
async function carregarAlunos() {
  try {
    const resp = await fetch(API_ALUNOS);
    const lista = await resp.json();

    // Mostra total no card do Dashboard
    document.getElementById("countAlunos").textContent = lista.length ?? 0;

    const tbody = document.querySelector("#listaAlunos tbody");
    tbody.innerHTML = "";

    // Monta cada linha da tabela
    lista.forEach((a) => {
      tbody.innerHTML += `
        <tr>
          <td>${a.id}</td>
          <td>${escapeHtml(a.nome)}</td>
          <td>${escapeHtml(a.email)}</td>

          <!-- Dados derivados via backend -->
          <td>${a.turma?.curso?.nome ?? "-"}</td>
          <td>${a.turma?.nome ?? "-"}</td>
          <td>${a.turma?.professor?.nome ?? "-"}</td>

          <td>
            <button class="btn-table btn-edit" onclick="editarAluno(${a.id})">✏️</button>
            <button class="btn-table btn-delete" onclick="deletarAluno(${a.id})">🗑️</button>
          </td>
        </tr>
      `;
    });

  } catch (err) {
    console.error("Erro ao carregar alunos:", err);
  }
}

/**
 * Salva um aluno novo ou editado.
 * Se existe ID → PUT
 * Se não existe → POST /completo
 */
async function salvarAluno(e) {
  e.preventDefault();

  const id = document.getElementById("alunoId").value;
  const nome = document.getElementById("alunoNome").value;
  const email = document.getElementById("alunoEmail").value;
  const turmaId = document.getElementById("turmaSelect").value;

  if (!nome || !email || !turmaId) {
    alert("Preencha todos os campos.");
    return;
  }

  // Payload enviado ao backend
  const payload = {
    nome,
    email,
    turma: { id: Number(turmaId) },
  };

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_ALUNOS}/${id}` : `${API_ALUNOS}/completo`;

  const resp = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!resp.ok) {
    alert("Erro ao salvar aluno");
    return;
  }

  resetAlunoForm();
  carregarAlunos();
}

/**
 * Preenche o formulário com os dados do aluno a ser editado.
 */
async function editarAluno(id) {
  const resp = await fetch(`${API_ALUNOS}/${id}`);
  const a = await resp.json();

  document.getElementById("alunoId").value = a.id;
  document.getElementById("alunoNome").value = a.nome;
  document.getElementById("alunoEmail").value = a.email;
  document.getElementById("turmaSelect").value = a.turma?.id ?? "";

  showPage("alunos");
}

/**
 * Remove um aluno após confirmação.
 * O backend já trata exclusão de notas e vínculos.
 */
async function deletarAluno(id) {
  if (!confirm("Excluir aluno?")) return;
  await fetch(`${API_ALUNOS}/${id}`, { method: "DELETE" });
  carregarAlunos();
}

function resetAlunoForm() {
  document.getElementById("formAluno").reset();
  document.getElementById("alunoId").value = "";
}

/* ============================================================
   CRUD — CURSOS
   ============================================================ */
async function carregarCursos() {
  const resp = await fetch(API_CURSOS);
  const lista = await resp.json();

  // Atualiza card do Dashboard
  document.getElementById("countCursos").textContent = lista.length;

  // Tabela de cursos
  const tbody = document.querySelector("#listaCursos tbody");
  tbody.innerHTML = lista
    .map(
      (c) => `
      <tr>
        <td>${c.id}</td>
        <td>${escapeHtml(c.nome)}</td>
        <td>
          <button class="btn-table btn-edit" onclick="editarCurso(${c.id})">✏️</button>
          <button class="btn-table btn-delete" onclick="deletarCurso(${c.id})">🗑️</button>
        </td>
      </tr>`
    )
    .join("");

  // Preenche select usado em ALUNOS (opcional)
  const sel = document.getElementById("alunoCurso");
  sel.innerHTML =
    '<option value="">Selecione</option>' +
    lista.map((c) => `<option value="${c.id}">${c.nome}</option>`).join("");
}

async function editarCurso(id) {
  const resp = await fetch(`${API_CURSOS}/${id}`);
  const curso = await resp.json();

  document.getElementById("cursoId").value = curso.id;
  document.getElementById("cursoNome").value = curso.nome;

  showPage("cursos");
}

async function deletarCurso(id) {
  if (!confirm("Deseja realmente excluir este curso?")) return;

  await fetch(`${API_CURSOS}/${id}`, { method: "DELETE" });

  carregarCursos();
}


async function salvarCurso(e) {
  e.preventDefault();

  const id = document.getElementById("cursoId").value;
  const nome = document.getElementById("cursoNome").value;

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_CURSOS}/${id}` : API_CURSOS;

  await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nome }),
  });

  resetCursoForm();
  carregarCursos();
}

function resetCursoForm() {
  document.getElementById("cursoId").value = "";
  document.getElementById("formCurso").reset();
}

/* ============================================================
   SELECT DE TURMAS NO FORMULÁRIO DE ALUNO
   ============================================================ */
async function carregarTurmasSelect() {
  try {
    const resp = await fetch(API_TURMAS);
    const turmas = await resp.json();

    const sel = document.getElementById("turmaSelect");
    sel.innerHTML = '<option value="">Selecione</option>';

    turmas.forEach((t) => {
      sel.innerHTML += `
        <option value="${t.id}">
          ${t.nome} — ${t.curso?.nome ?? "Sem curso"} — Prof: ${t.professor?.nome ?? "?"}
        </option>
      `;
    });

  } catch (err) {
    console.error("Erro ao carregar turmas:", err);
  }
}

/* ============================================================
   CRUD — PROFESSORES
   ============================================================ */
async function carregarProfessores() {
  const resp = await fetch(API_PROFS);
  const lista = await resp.json();

  document.getElementById("countProfs").textContent = lista.length;

  const tbody = document.querySelector("#listaProfessores tbody");
  tbody.innerHTML = lista
    .map(
      (p) => `
      <tr>
        <td>${p.id}</td>
        <td>${escapeHtml(p.nome)}</td>
        <td>
          <button onclick="editarProfessor(${p.id})" class="btn-table btn-edit">✏️</button>
          <button onclick="deletarProfessor(${p.id})" class="btn-table btn-delete">🗑️</button>
        </td>
      </tr>`
    )
    .join("");

  // Preenche select usado em Turmas
  const sel = document.getElementById("alunoProfessor");
  sel.innerHTML =
    '<option value="">Selecione</option>' +
    lista.map((p) => `<option value="${p.id}">${p.nome}</option>`).join("");
}

async function salvarProfessor(e) {
  e.preventDefault();

  const id = document.getElementById("professorId").value;
  const nome = document.getElementById("professorNome").value.trim();

  if (!nome) return alert("Informe o nome");

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_PROFS}/${id}` : API_PROFS;

  await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nome }),
  });

  resetProfessorForm();
  carregarProfessores();
}

async function editarProfessor(id) {
  const resp = await fetch(`${API_PROFS}/${id}`);
  const prof = await resp.json();

  document.getElementById("professorId").value = prof.id;
  document.getElementById("professorNome").value = prof.nome;

  showPage("professores");
}

async function deletarProfessor(id) {
  if (!confirm("Deseja realmente excluir este professor?")) return;

  await fetch(`${API_PROFS}/${id}`, { method: "DELETE" });

  carregarProfessores();
}


function resetProfessorForm() {
  document.getElementById("professorId").value = "";
  document.getElementById("formProfessor").reset();
}

/* ============================================================
   Tema — Claro / Escuro
   ============================================================ */
function toggleTheme() {
  document.documentElement.classList.toggle("dark");

  // Salva no navegador para manter entre visitas
  const mode = document.documentElement.classList.contains("dark")
    ? "dark"
    : "light";

  localStorage.setItem("sga-theme", mode);
}

/* ============================================================
   Helpers Utilitários
   ============================================================ */
/**
 * Escapa caracteres especiais para evitar falhas de layout e XSS.
 */
function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
