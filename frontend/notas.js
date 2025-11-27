/* ==========================
   NOTAS — CARREGAR TURMAS NO SELECT
   ========================== */
async function carregarTurmas_NoSelectNotas() {
    try {
        const resp = await fetch(API_TURMAS);
        const turmas = await resp.json();

        const sel = document.getElementById("notasTurmaSelect");

        sel.innerHTML = `<option value="">— selecione —</option>`;

        turmas.forEach(t => {
            sel.innerHTML += `
                <option value="${t.id}">
                    ${t.nome} — ${t.curso?.nome ?? "Curso indefinido"} — Prof: ${t.professor?.nome ?? "?"}
                </option>
            `;
        });

        // Quando trocar a turma → recarrega notas
        sel.addEventListener("change", carregarNotasPorTurma);

    } catch (err) {
        console.error("Erro ao carregar turmas no select de notas:", err);
    }
}

/* ==========================
   NOTAS — CARREGAR NOTAS POR TURMA
   ========================== */
async function carregarNotasPorTurma() {
    const turmaId = document.getElementById("notasTurmaSelect").value;

    if (!turmaId) {
        document.getElementById("notasCard").style.display = "none";
        return;
    }

    try {
        // 1) Buscar alunos da turma
        const respAlunos = await fetch(`${API_ALUNOS}/turma/${turmaId}`);
        const alunos = await respAlunos.json();

        // 2) Buscar notas já lançadas
        const respNotas = await fetch(`${API_NOTAS}/turma/${turmaId}`);
        const notas = await respNotas.json();

        // 3) Montar lista final
        const listaFinal = alunos.map(a => {
            const nota = notas.find(n => n.aluno && n.aluno.id === a.id);

            return {
                aluno: a,
                id: nota?.id ?? 0,
                nota1: nota?.nota1 ?? null,
                nota2: nota?.nota2 ?? null,
                media: nota?.media ?? null,
                status: nota?.status ?? null
            };
        });

        montarTabelaNotas(listaFinal);

    } catch (err) {
        console.error("Erro ao carregar notas:", err);
    }
}

/* ==========================
   NOTAS — MONTAR TABELA
   ========================== */
function montarTabelaNotas(lista) {
    const tbody = document.getElementById("notasTabelaBody");
    tbody.innerHTML = "";

    lista.forEach(n => {
        tbody.innerHTML += `
            <tr>
                <td>${n.aluno.id}</td>
                <td>${n.aluno.nome}</td>

                <td>
                    <input type="number" min="0" max="10"
                        id="nota1-${n.id}"
                        value="${n.nota1 ?? ""}">
                </td>

                <td>
                    <input type="number" min="0" max="10"
                        id="nota2-${n.id}"
                        value="${n.nota2 ?? ""}">
                </td>

                <td>${n.media ?? "-"}</td>
                <td>${n.status ?? "-"}</td>
            </tr>
        `;
    });

    document.getElementById("notasCard").style.display = "block";
}

/* ==========================
   NOTAS — SALVAR TODAS EM LOTE
   ========================== */
async function salvarNotasEmLote() {
    const turmaId = document.getElementById("notasTurmaSelect").value;

    const linhas = document.querySelectorAll("#notasTabelaBody tr");

    for (let linha of linhas) {
        const cols = linha.children;

        const alunoId = cols[0].innerText;
        const input1 = cols[2].querySelector("input");
        const input2 = cols[3].querySelector("input");

        const notaId = Number(input1.id.split("-")[1]);

        await salvarUmaNota(
            notaId,
            alunoId,
            turmaId,
            input1.value,
            input2.value
        );
    }

    alert("Notas salvas com sucesso!");
    carregarNotasPorTurma();
}

/* ==========================
   NOTAS — SALVAR UMA NOTA
   ========================== */
async function salvarUmaNota(id, alunoId, turmaId, nota1, nota2) {

    const payload = {
        aluno: { id: Number(alunoId) },
        turma: { id: Number(turmaId) },
        nota1: nota1 === "" ? null : Number(nota1),
        nota2: nota2 === "" ? null : Number(nota2)
    };

    const isUpdate = id && id > 0;

    const url = isUpdate
        ? `${API_NOTAS}/${id}`
        : API_NOTAS;

    const method = isUpdate ? "PUT" : "POST";

    try {
        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });
    } catch (err) {
        console.error("Erro ao salvar nota:", err);
    }
}
