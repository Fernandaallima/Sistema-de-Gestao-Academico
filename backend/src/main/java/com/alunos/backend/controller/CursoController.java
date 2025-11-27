package com.alunos.backend.controller;

import com.alunos.backend.model.Curso;
import com.alunos.backend.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoRepository repo;

    @GetMapping
    public List<Curso> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Curso criar(@RequestBody Curso c) {
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Curso editar(@PathVariable Long id, @RequestBody Curso c) {
        c.setId(id);
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repo.deleteById(id);
    }
    @GetMapping("/{id}")
    public Curso buscarPorId(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }
}
