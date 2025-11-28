package com.alunos.backend.controller;

import com.alunos.backend.model.Professor;
import com.alunos.backend.model.Turma;          // ✔ IMPORTANTE
import com.alunos.backend.repository.ProfessorRepository;
import com.alunos.backend.repository.TurmaRepository;  // ✔ IMPORTANTE

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorRepository repo;

    @Autowired
    private TurmaRepository turmaRepository;   //  NECESSÁRIO PARA DESVINCULAR AS TURMAS

    // -----------------------------------
    // LISTAR TODOS OS PROFESSORES
    // -----------------------------------
    @GetMapping
    public List<Professor> listar() {
        return repo.findAll();
    }

    // -----------------------------------
    // CRIAR UM NOVO PROFESSOR
    // -----------------------------------
    @PostMapping
    public Professor criar(@RequestBody Professor p) {
        return repo.save(p);
    }

    // -----------------------------------
    // EDITAR UM PROFESSOR EXISTENTE
    // -----------------------------------
    @PutMapping("/{id}")
    public Professor editar(@PathVariable Long id, @RequestBody Professor p) {
        p.setId(id);
        return repo.save(p);
    }

    // -----------------------------------
    // EXCLUIR UM PROFESSOR POR ID
    // -----------------------------------
    @DeleteMapping("/{id}")
    @Transactional
    public void deletar(@PathVariable Long id) {

        // 1) Buscar professor
        Professor professor = repo.findById(id).orElse(null);
        if (professor == null) return;

        // 2) Buscar turmas vinculadas ao professor
        List<Turma> turmas = turmaRepository.findByProfessor_Id(id);

        // 3) Para cada turma: desvincular professor
        for (Turma t : turmas) {
            t.setProfessor(null);
            turmaRepository.save(t);
        }

        // 4) Agora o professor pode ser excluído sem erro
        repo.deleteById(id);
    }

    // -----------------------------------
    // BUSCAR PROFESSOR POR ID
    // -----------------------------------
    @GetMapping("/{id}")
    public Professor buscarPorId(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

}
