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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notas")
public class NotaController {

    @Autowired
    private NotaRepository repo;

    @Autowired
    private AlunoRepository alunoRepo;

    @Autowired
    private TurmaRepository turmaRepo;

    // -----------------------------------
    // LISTAR TODAS
    // -----------------------------------
    @GetMapping
    public List<Nota> listar() {
        return repo.findAll();
    }

    // -----------------------------------
    // CRIAR NOTA
    // -----------------------------------
    @PostMapping
    public Nota criar(@RequestBody Nota n) {

        // garantir aluno correto
        if (n.getAluno() != null && n.getAluno().getId() != null) {
            Aluno aluno = alunoRepo.findById(n.getAluno().getId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            n.setAluno(aluno);
        }

        // garantir turma correta
        if (n.getTurma() != null && n.getTurma().getId() != null) {
            Turma turma = turmaRepo.findById(n.getTurma().getId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            n.setTurma(turma);
        }

        calcularMediaStatus(n);
        return repo.save(n);
    }

    // -----------------------------------
    // EDITAR NOTA
    // -----------------------------------
    @PutMapping("/{id}")
    public Nota editar(@PathVariable Long id, @RequestBody Nota n) {

        n.setId(id);

        // garantir aluno correto
        if (n.getAluno() != null && n.getAluno().getId() != null) {
            Aluno aluno = alunoRepo.findById(n.getAluno().getId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            n.setAluno(aluno);
        }

        // garantir turma correta
        if (n.getTurma() != null && n.getTurma().getId() != null) {
            Turma turma = turmaRepo.findById(n.getTurma().getId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            n.setTurma(turma);
        }

        calcularMediaStatus(n);
        return repo.save(n);
    }

    // -----------------------------------
    // BUSCAR NOTAS POR TURMA
    // -----------------------------------
    @GetMapping("/turma/{turmaId}")
    public List<Nota> porTurma(@PathVariable Long turmaId) {
        return repo.findByTurmaId(turmaId);
    }

    // -----------------------------------
    // BUSCAR NOTA POR ALUNO
    // -----------------------------------
    @GetMapping("/aluno/{alunoId}")
    public Nota buscarPorAluno(@PathVariable Long alunoId) {
        return repo.findByAlunoId(alunoId).stream().findFirst().orElse(null);
    }

    // -----------------------------------
    // CÁLCULO DE MÉDIA E STATUS
    // -----------------------------------
    private void calcularMediaStatus(Nota n) {

        Double a = n.getNota1();
        Double b = n.getNota2();

        // Nenhuma nota enviada → REPROVADO
        if (a == null && b == null) {
            n.setMedia(null);
            n.setStatus("REPROVADO");
            return;
        }

        // Só uma nota → média parcial
        if ((a != null && b == null) || (a == null && b != null)) {
            double media = (a != null ? a : b);
            n.setMedia(media);
            n.setStatus(null);
            return;
        }

        // Duas notas → média final
        double media = (a + b) / 2.0;
        n.setMedia(media);
        n.setStatus(media >= 6.0 ? "APROVADO" : "REPROVADO");
    }
}
