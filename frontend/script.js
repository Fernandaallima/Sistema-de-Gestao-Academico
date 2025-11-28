
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
    return alert("Preencha todos os campos!");
  }

  // ---- DEFINIÇÃO DA ROTA CORRETA ----
  // Se está editando → PUT normal
  // Se é novo aluno → POST /completo
  const method = id ? "PUT" : "POST";
  const url = id
    ? `${API_ALUNOS}/${id}`
    : `${API_ALUNOS}/completo`;

  // ---- BODY CORRIGIDO ----
  const body = id
    ? { // PUT → backend aceita JSON completo sem problema
        id,
        nome,
        email,
        turma: { id: Number(turmaId) }
      }
    : { // POST /completo → EXIGE objeto turma
        nome,
        email,
        turma: { id: Number(turmaId) }
      };

  await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  // Atualiza tabela
  await carregarAlunos();

  // Limpa formulário
  resetAlunoForm({ salvo: true });

  if (!id) showMessage("✅ Aluno criado com sucesso!", "success");
}



/**
 * Preenche o formulário com os dados do aluno a ser editado.
 */
// Função para mostrar mensagens tipo toast
function showMessage(message, type = "success") {
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.textContent = message;

  document.body.appendChild(toast);

  setTimeout(() => {
      toast.remove();
  }, 3000);
}

// Função de edição de aluno
function editarAluno(id) {
  fetch(`${API_ALUNOS}/${id}`)
    .then(resp => resp.json())
    .then(a => {
        document.getElementById("alunoId").value = a.id;
        document.getElementById("alunoNome").value = a.nome;
        document.getElementById("alunoEmail").value = a.email;
        document.getElementById("turmaSelect").value = a.turma?.id ?? "";

        showPage("alunos");

        showMessage("🖊️ Você está editando o aluno " + a.nome, "info");

        const form = document.getElementById("formAluno");
        form.classList.add("editing");

        // Não alteramos o botão Salvar
        // O botão Cancelar já está no HTML e chama resetAlunoForm()
    })
    .catch(err => {
        console.error(err);
        showMessage("❌ Erro ao carregar o aluno", "error");
    });
}

function cancelarEdicaoAluno() {
  resetAlunoForm({ cancelado: true });
}



/**
 * Remove um aluno após confirmação.
 * O backend já trata exclusão de notas e vínculos.
 */
async function deletarAluno(id) {
  const ok = await confirmarBonito("Excluir este aluno?");
  if (!ok) return;

  await fetch(`${API_ALUNOS}/${id}`, { method: "DELETE" });
  carregarAlunos();

  showMessage("🗑️ Aluno excluído com sucesso!", "success");
}

function confirmarBonito(msg = "Tem certeza?") {
  return new Promise(resolve => {
      
      // Criar overlay
      const overlay = document.createElement("div");
      overlay.className = "confirm-overlay";

      // Criar caixa
      const box = document.createElement("div");
      box.className = "confirm-box";

      box.innerHTML = `
          <h3 class="confirm-title">${msg}</h3>
          <div class="confirm-actions">
              <button id="cYes" class="btn primary">Sim</button>
              <button id="cNo" class="btn ghost">Cancelar</button>
          </div>
      `;

      overlay.appendChild(box);
      document.body.appendChild(overlay);

      // Botões
      box.querySelector("#cYes").onclick = () => fechar(true);
      box.querySelector("#cNo").onclick = () => fechar(false);

      function fechar(resp) {
          overlay.classList.add("closing");
          setTimeout(() => {
              overlay.remove();
              resolve(resp);
          }, 200); // tempo da animação
      }
  });
}

function resetAlunoForm({ cancelado = false, salvo = false } = {}) {
  const form = document.getElementById("formAluno");

  // Detecta se estava editando
  const estavaEditando = form.classList.contains("editing");

  // Limpa os campos
  form.reset();
  document.getElementById("alunoId").value = "";

  // Remove classe de edição
  form.classList.remove("editing");

  // Mensagens
  if (cancelado && estavaEditando) {
    showMessage("✖️ Edição cancelada", "info");
  } else if (salvo && estavaEditando) {
    showMessage("✅ Edição salva com sucesso!", "success");
  }
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

// Função para editar um curso
async function editarCurso(id) {
  try {
    const resp = await fetch(`${API_CURSOS}/${id}`);
    const curso = await resp.json();

    // Preenche formulário
    document.getElementById("cursoId").value = curso.id;
    document.getElementById("cursoNome").value = curso.nome;

    // Mostra a página de cursos
    showPage("cursos");

    // Mostra toast informativo
    showMessage(`🖊️ Você está editando o curso "${curso.nome}"`, "info");

    // Destaca o formulário
    const form = document.getElementById("formCurso");
    form.classList.add("editing");

  } catch (err) {
    console.error(err);
    showMessage("❌ Erro ao carregar o curso", "error");
  }
}

// Função para cancelar edição de curso
function cancelarEdicaoCurso() {
  const form = document.getElementById("formCurso");

  // Limpa formulário
  resetCursoForm();

  // Remove destaque
  form.classList.remove("editing");

  // Mostra toast
  showMessage("✖️ Edição cancelada", "info");
}

// Função para deletar um curso
async function deletarCurso(id) {
  const ok = await confirmarBonito("Deseja realmente excluir este curso?");
  if (!ok) return;

  try {
    await fetch(`${API_CURSOS}/${id}`, { method: "DELETE" });
    carregarCursos();
    showMessage("🗑️ Curso excluído com sucesso!", "success");
  } catch (err) {
    console.error(err);
    showMessage("❌ Erro ao excluir o curso", "error");
  }
}



async function salvarCurso(e) {
  e.preventDefault();

  const id = document.getElementById("cursoId").value;
  const nome = document.getElementById("cursoNome").value;

  if (!nome) return alert("Informe o nome do curso");

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_CURSOS}/${id}` : API_CURSOS;

  await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nome }),
  });

  // Atualiza a tabela
  carregarCursos();

  // Reseta formulário e mostra mensagem de sucesso se estava editando
  resetCursoForm({ salvo: true });

  // Se for novo curso, você pode mostrar outra mensagem:
  if (!id) showMessage("✅ Curso adicionado com sucesso!", "success");
}


function resetCursoForm({ cancelado = false, salvo = false } = {}) {
  const form = document.getElementById("formCurso");

  // Detecta se estava editando
  const estavaEditando = form.classList.contains("editing");

  // Limpa campos
  form.reset();
  document.getElementById("cursoId").value = "";

  // Remove destaque de edição
  form.classList.remove("editing");

  // Mensagens
  if (cancelado && estavaEditando) {
    showMessage("✖️ Edição cancelada", "info");
  } else if (salvo && estavaEditando) {
    showMessage("✅ Edição salva com sucesso!", "success");
  }
}


function cancelarEdicaoCurso() {
  resetCursoForm({ cancelado: true });
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

  // Atualiza a tabela
  carregarProfessores();

  // Reseta o formulário e mostra mensagem de sucesso se estava editando
  resetProfessorForm({ salvo: true });
}


// Função de edição de professor
async function editarProfessor(id) {
  try {
    const resp = await fetch(`${API_PROFS}/${id}`);
    const prof = await resp.json();

    document.getElementById("professorId").value = prof.id;
    document.getElementById("professorNome").value = prof.nome;

    showPage("professores");

    // Destaca o formulário como em edição
    const form = document.getElementById("formProfessor");
    form.classList.add("editing");

    showMessage("🖊️ Você está editando o professor " + prof.nome, "info");
  } catch (err) {
    console.error(err);
    showMessage("❌ Erro ao carregar o professor", "error");
  }
}

// Função para cancelar edição do professor
function cancelarEdicaoProfessor() {
  resetProfessorForm({ cancelado: true });
}


// Função para deletar professor com confirmação bonita
async function deletarProfessor(id) {
  const ok = await confirmarBonito("Deseja realmente excluir este professor?");
  if (!ok) return;

  try {
    await fetch(`${API_PROFS}/${id}`, { method: "DELETE" });
    carregarProfessores();
    showMessage("🗑️ Professor excluído com sucesso!", "success");
  } catch (err) {
    console.error(err);
    showMessage("❌ Erro ao excluir o professor", "error");
  }
}

// Função de reset do formulário de professor (igual ao padrão)
function resetProfessorForm({ cancelado = false, salvo = false } = {}) {
  const form = document.getElementById("formProfessor");

  // Detecta se estava editando
  const estavaEditando = form.classList.contains("editing");

  // Limpa os campos
  form.reset();
  document.getElementById("professorId").value = "";

  // Remove destaque de edição
  form.classList.remove("editing");

  // Mostra mensagem conforme a ação
  if (cancelado && estavaEditando) {
    showMessage("✖️ Edição cancelada", "info");
  } else if (salvo && estavaEditando) {
    showMessage("✅ Edição salva com sucesso!", "success");
  }
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
