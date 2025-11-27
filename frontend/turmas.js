/* ---------- CARREGAR LISTA DE TURMAS ---------- */
async function carregarTurmas() {
    try {
        const resp = await fetch(API_TURMAS);
        if (!resp.ok) throw new Error("Erro ao carregar turmas");

        const lista = await resp.json();

        // 🔥 Atualizar total no dashboard
        const totalEl = document.getElementById("countTurmas");
        if (totalEl) totalEl.textContent = lista.length;

        // popular tabela
        const tbody = document.getElementById("listaTurmas");
        tbody.innerHTML = "";

        lista.forEach(t => {
            tbody.innerHTML += `
                <tr>
                    <td>${t.id}</td>
                    <td>${t.nome}</td>
                    <td>${t.curso?.nome ?? "-"}</td>
                    <td>${t.professor?.nome ?? "-"}</td>
                    <td>
                        <button class="btn-table btn-edit" onclick="editarTurma(${t.id})">✏️</button>
                        <button class="btn-table btn-delete" onclick="deletarTurma(${t.id})">🗑️</button>
                    </td>
                </tr>
            `;
        });

        carregarCursosEmSelect();
        carregarProfessoresEmSelect();

    } catch (e) {
        console.error(e);
        alert("Erro ao carregar turmas.");
    }
}


/* ---------- SELECT CURSOS ---------- */
async function carregarCursosEmSelect() {
    const resp = await fetch(API_CURSOS);
    const lista = await resp.json();

    const sel = document.getElementById("turmaCurso");
    sel.innerHTML = `<option value="">Selecione</option>`;

    lista.forEach(c => {
        sel.innerHTML += `<option value="${c.id}">${c.nome}</option>`;
    });
}

/* ---------- SELECT PROFESSORES ---------- */
async function carregarProfessoresEmSelect() {
    const resp = await fetch(API_PROFS);
    const lista = await resp.json();

    const sel = document.getElementById("turmaProfessor");
    sel.innerHTML = `<option value="">Selecione</option>`;

    lista.forEach(p => {
        sel.innerHTML += `<option value="${p.id}">${p.nome}</option>`;
    });
}

/* ---------- SALVAR TURMA ---------- */
async function salvarTurma(e) {
    e.preventDefault();

    const id = document.getElementById("turmaId").value;
    const nome = document.getElementById("turmaNome").value;
    const cursoId = document.getElementById("turmaCurso").value;
    const professorId = document.getElementById("turmaProfessor").value;

    if (!nome || !cursoId || !professorId) {
        alert("Preencha todos os campos.");
        return;
    }

    const payload = {
        nome,
        curso: { id: Number(cursoId) },
        professor: { id: Number(professorId) }
    };

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_TURMAS}/${id}` : `${API_TURMAS}/completo`;

    try {
        const resp = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!resp.ok) throw new Error("Erro ao salvar turma");

        // Reseta formulário e mostra toast de edição salva
        resetTurmaForm({ salvo: true });

        // Atualiza tabelas e selects
        carregarTurmas();
        carregarCursosEmSelect();
        carregarProfessoresEmSelect();

    } catch (e) {
        console.error(e);
        showMessage("❌ Erro ao salvar turma", "error");
    }
}


/* ---------- EDITAR ---------- */
async function editarTurma(id) {
    try {
        const resp = await fetch(`${API_TURMAS}/${id}`);
        if (!resp.ok) throw new Error("Turma não encontrada");

        const t = await resp.json();

        document.getElementById("turmaId").value = t.id;
        document.getElementById("turmaNome").value = t.nome;
        document.getElementById("turmaCurso").value = t.curso?.id ?? "";
        document.getElementById("turmaProfessor").value = t.professor?.id ?? "";

        showPage("turmasPage");

        // Destaca formulário e mostra toast
        const form = document.getElementById("formTurma");
        form.classList.add("editing");
        showMessage(`🖊️ Você está editando a turma "${t.nome}"`, "info");

    } catch (e) {
        console.error(e);
        showMessage("❌ Erro ao carregar turma", "error");
    }
}


/* ---------- EXCLUIR ---------- */
async function deletarTurma(id) {
    const ok = await confirmarBonito("Deseja realmente excluir esta turma?");
    if (!ok) return;

    try {
        const resp = await fetch(`${API_TURMAS}/${id}`, { method: "DELETE" });
        if (!resp.ok) throw new Error("Erro ao excluir turma");

        // Atualiza tabelas corretamente
        carregarTurmas();
        carregarAlunos();          // Atualiza lista de alunos
        carregarTurmasSelect();    // Atualiza select de turmas
        showMessage("🗑️ Turma excluída com sucesso!", "success");
    } catch (e) {
        console.error(e);
        showMessage("❌ Erro ao excluir turma", "error");
    }
}



function cancelarEdicaoTurma() {
    resetTurmaForm({ cancelado: true });
}


/* ---------- RESET ---------- */
function resetTurmaForm({ cancelado = false, salvo = false } = {}) {
    const form = document.getElementById("formTurma");

    const estavaEditando = form.classList.contains("editing");

    form.reset();
    document.getElementById("turmaId").value = "";

    form.classList.remove("editing");

    if (cancelado && estavaEditando) {
        showMessage("✖️ Edição cancelada", "info");
    } else if (salvo && estavaEditando) {
        showMessage("✅ Edição salva com sucesso!", "success");
    }
}




/* ---------- Atualiza dropdowns de turmas ---------- */
function carregarDropdownTurmas() {
    // só chama se existir no seu projeto
    if (typeof carregarTurmas_NoSelectNotas === "function")
        carregarTurmas_NoSelectNotas();

    if (typeof carregarTurmas_NoSelectAluno === "function")
        carregarTurmas_NoSelectAluno();

    if (typeof carregarTurmas === "function")
        carregarTurmas();
}

/* ---------- Se seu sistema não usa, não quebra ---------- */
function carregarDropdownAlunos() {
    if (typeof carregarAlunos === "function") carregarAlunos();
}

function carregarDropdownNotas() {
    if (typeof carregarTurmasNotas === "function") carregarTurmasNotas();
}
