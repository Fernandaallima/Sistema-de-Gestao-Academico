package com.alunos.backend.controller;

import com.alunos.backend.model.Aluno;
import com.alunos.backend.model.Nota;
import com.alunos.backend.model.Turma;
import com.alunos.backend.repository.AlunoRepository;
import com.alunos.backend.repository.NotaRepository;
import com.alunos.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Indica que esta classe é um controlador REST (retorna JSON)
@RestController

// Permite acesso do frontend, liberando CORS
@CrossOrigin(origins = "*")

// Define que todas as rotas começam com /notas
@RequestMapping("/notas")
public class NotaController {

    // Injeta automaticamente o repositório de notas
    @Autowired
    private NotaRepository repo;

    // Para buscar alunos ao salvar/editar notas
    @Autowired
    private AlunoRepository alunoRepo;

    // Para buscar turma ao salvar/editar notas
    @Autowired
    private TurmaRepository turmaRepo;

    // -----------------------------------
    // LISTAR TODAS AS NOTAS
    // -----------------------------------
    @GetMapping
    public List<Nota> listar() {
        // Retorna todas as notas do banco
        return repo.findAll();
    }

    // -----------------------------------
    // CRIAR NOVA NOTA
    // -----------------------------------
    @PostMapping
    public Nota criar(@RequestBody Nota n) {

        // Garantir que o aluno informado é válido
        if (n.getAluno() != null && n.getAluno().getId() != null) {
            Aluno aluno = alunoRepo.findById(n.getAluno().getId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            n.setAluno(aluno);
        }

        // Garantir que a turma informada é válida
        if (n.getTurma() != null && n.getTurma().getId() != null) {
            Turma turma = turmaRepo.findById(n.getTurma().getId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            n.setTurma(turma);
        }

        // Calcula média e status antes de salvar
        calcularMediaStatus(n);

        // Salva a nota no banco
        return repo.save(n);
    }

    // -----------------------------------
    // EDITAR NOTA EXISTENTE
    // -----------------------------------
    @PutMapping("/{id}")
    public Nota editar(@PathVariable Long id, @RequestBody Nota n) {

        // Define o ID da nota que será editada
        n.setId(id);

        // Garantir aluno válido
        if (n.getAluno() != null && n.getAluno().getId() != null) {
            Aluno aluno = alunoRepo.findById(n.getAluno().getId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            n.setAluno(aluno);
        }

        // Garantir turma válida
        if (n.getTurma() != null && n.getTurma().getId() != null) {
            Turma turma = turmaRepo.findById(n.getTurma().getId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            n.setTurma(turma);
        }

        // Recalcula média e status antes de atualizar
        calcularMediaStatus(n);

        // Atualiza no banco
        return repo.save(n);
    }

    // -----------------------------------
    // BUSCAR TODAS AS NOTAS DE UMA TURMA
    // -----------------------------------
    @GetMapping("/turma/{turmaId}")
    public List<Nota> porTurma(@PathVariable Long turmaId) {
        // Busca todas as notas onde turma_id = turmaId
        return repo.findByTurmaId(turmaId);
    }

    // -----------------------------------
    // BUSCAR NOTA DE UM ALUNO ESPECÍFICO
    // -----------------------------------
    @GetMapping("/aluno/{alunoId}")
    public Nota buscarPorAluno(@PathVariable Long alunoId) {
        // Retorna a nota do aluno ou null se não existir
        return repo.findByAlunoId(alunoId).stream().findFirst().orElse(null);
    }

    // -----------------------------------
    // LÓGICA DE CÁLCULO DA MÉDIA E STATUS
    // -----------------------------------
    private void calcularMediaStatus(Nota n) {

        Double a = n.getNota1();
        Double b = n.getNota2();

        // Caso nenhum valor seja informado → aluno reprovado
        if (a == null && b == null) {
            n.setMedia(null);
            n.setStatus("REPROVADO");
            return;
        }

        // Apenas uma nota informada → média parcial, sem status
        if ((a != null && b == null) || (a == null && b != null)) {
            double media = (a != null ? a : b);
            n.setMedia(media);
            n.setStatus(null);
            return;
        }

        // Duas notas → média final e definição do status
        double media = (a + b) / 2.0;
        n.setMedia(media);
        n.setStatus(media >= 6.0 ? "APROVADO" : "REPROVADO");
    }
}
