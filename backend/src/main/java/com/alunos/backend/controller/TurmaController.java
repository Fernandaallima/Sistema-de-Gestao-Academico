package com.alunos.backend.controller;

import com.alunos.backend.model.Turma;
import com.alunos.backend.model.Aluno;
import com.alunos.backend.model.Curso;
import com.alunos.backend.model.Professor;

import com.alunos.backend.repository.TurmaRepository;
import com.alunos.backend.repository.CursoRepository;
import com.alunos.backend.repository.ProfessorRepository;
import com.alunos.backend.repository.AlunoRepository;
import com.alunos.backend.repository.NotaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping
    public List<Turma> listar() {
        return turmaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        return turmaRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Turma criar(@RequestBody Turma turma) {
        return turmaRepository.save(turma);
    }

    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @RequestBody Turma turma) {
        turma.setId(id);
        return turmaRepository.save(turma);
    }

    // 🔥🔥🔥 DELETE DEFINITIVO QUE FUNCIONA 🔥🔥🔥
    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {

        Turma turma = turmaRepository.findById(id).orElse(null);
        if (turma == null) return;

        // 1) Buscar alunos da turma
        List<Aluno> alunos = alunoRepository.findByTurma_Id(id);

        // 2) Para cada aluno: apagar notas
        for (Aluno aluno : alunos) {
            notaRepository.findByAlunoId(aluno.getId())
                    .forEach(n -> notaRepository.deleteById(n.getId()));

            // 3) Remover vínculo com a turma
            aluno.setTurma(null);
            alunoRepository.save(aluno);
        }

        // 4) Agora pode deletar a turma sem erro
        turmaRepository.deleteById(id);
    }

    @PostMapping("/completo")
    public Turma criarTurmaCompleta(@RequestBody Turma turma) {

        // Vincular curso
        if (turma.getCurso() != null && turma.getCurso().getId() != null) {
            Curso curso = cursoRepository.findById(turma.getCurso().getId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
            turma.setCurso(curso);
        }

        // Vincular professor
        if (turma.getProfessor() != null && turma.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(turma.getProfessor().getId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            turma.setProfessor(professor);
        }

        return turmaRepository.save(turma);
    }
}
