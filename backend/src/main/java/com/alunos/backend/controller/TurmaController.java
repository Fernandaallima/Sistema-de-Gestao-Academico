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

// Indica que esta classe é um controller REST (retorna JSON)
@RestController

// Libera requisições do frontend (CORS)
@CrossOrigin(origins = "*")

// Define o caminho base de todos os endpoints: /turmas
@RequestMapping("/turmas")
public class TurmaController {

    // Repositório da Turma (acesso ao banco)
    @Autowired
    private TurmaRepository turmaRepository;

    // Repositório do Aluno (para buscar alunos da turma)
    @Autowired
    private AlunoRepository alunoRepository;

    // Repositório das Notas (necessário para exclusão completa)
    @Autowired
    private NotaRepository notaRepository;

    // Repositório de Curso (vincular curso à turma)
    @Autowired
    private CursoRepository cursoRepository;

    // Repositório de Professor (vincular professor à turma)
    @Autowired
    private ProfessorRepository professorRepository;

    // -------------------------------------------
    // LISTAR TODAS AS TURMAS
    // -------------------------------------------
    @GetMapping
    public List<Turma> listar() {
        // Retorna todas as turmas cadastradas
        return turmaRepository.findAll();
    }

    // -------------------------------------------
    // BUSCAR TURMA POR ID
    // -------------------------------------------
    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        // Busca a turma ou retorna null se não existir
        return turmaRepository.findById(id).orElse(null);
    }

    // -------------------------------------------
    // CRIAR TURMA SIMPLES
    // -------------------------------------------
    @PostMapping
    public Turma criar(@RequestBody Turma turma) {
        // Salva a turma no banco
        return turmaRepository.save(turma);
    }

    // -------------------------------------------
    // ATUALIZAR TURMA
    // -------------------------------------------
    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @RequestBody Turma turma) {
        // Define o ID enviado pela URL
        turma.setId(id);

        // Salva as alterações
        return turmaRepository.save(turma);
    }

    // ------------------------------------------------------------
    // 🔥 EXCLUIR TURMA COMPLETA — COM ALUNOS E NOTAS (SEM ERROS) 🔥
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {

        // Busca a turma no banco
        Turma turma = turmaRepository.findById(id).orElse(null);
        if (turma == null) return;

        // 1) Buscar todos os alunos vinculados à turma
        List<Aluno> alunos = alunoRepository.findByTurma_Id(id);

        // 2) Para cada aluno: deletar notas associadas
        for (Aluno aluno : alunos) {

            // Apagar todas as notas do aluno
            notaRepository.findByAlunoId(aluno.getId())
                    .forEach(n -> notaRepository.deleteById(n.getId()));

            // 3) Desvincular o aluno da turma
            aluno.setTurma(null);
            alunoRepository.save(aluno);
        }

        // 4) Agora é seguro deletar a turma,
        // pois nenhum aluno nem nota está referenciando ela
        turmaRepository.deleteById(id);
    }

    // ------------------------------------------------------------
    // CRIAR TURMA COMPLETA — COM CURSO E PROFESSOR VINCULADOS
    // ------------------------------------------------------------
    @PostMapping("/completo")
    public Turma criarTurmaCompleta(@RequestBody Turma turma) {

        // Vincular Curso
        if (turma.getCurso() != null && turma.getCurso().getId() != null) {
            Curso curso = cursoRepository.findById(turma.getCurso().getId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
            turma.setCurso(curso);
        }

        // Vincular Professor
        if (turma.getProfessor() != null && turma.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(turma.getProfessor().getId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            turma.setProfessor(professor);
        }

        // Salva a turma completa
        return turmaRepository.save(turma);
    }
}
