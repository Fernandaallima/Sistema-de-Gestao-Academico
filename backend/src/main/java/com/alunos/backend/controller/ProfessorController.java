package com.alunos.backend.controller;

import com.alunos.backend.model.Professor;
import com.alunos.backend.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorRepository repo;

    @GetMapping
    public List<Professor> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Professor criar(@RequestBody Professor p) {
        return repo.save(p);
    }

    @PutMapping("/{id}")
    public Professor editar(@PathVariable Long id, @RequestBody Professor p) {
        p.setId(id);
        return repo.save(p);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repo.deleteById(id);
    }
    @GetMapping("/{id}")
    public Professor buscarPorId(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

}
