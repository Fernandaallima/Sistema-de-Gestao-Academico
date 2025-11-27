package com.alunos.backend.controller;

// Importa o model Curso e o repositório responsável por acessar o banco
import com.alunos.backend.model.Curso;
import com.alunos.backend.repository.CursoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Indica que esta classe é um controller REST (retorna JSON)
@RestController

// Libera acesso de qualquer origem (uso do frontend)
@CrossOrigin(origins = "*")

// Define que todos os endpoints começam com /cursos
@RequestMapping("/cursos")
public class CursoController {

    // Injeta automaticamente o repositório (acesso ao banco)
    @Autowired
    private CursoRepository repo;

    // -------------------------
    // LISTAR TODOS OS CURSOS
    // -------------------------
    @GetMapping
    public List<Curso> listar() {
        // Retorna todos os cursos do banco
        return repo.findAll();
    }

    // -------------------------
    // CRIAR NOVO CURSO
    // -------------------------
    @PostMapping
    public Curso criar(@RequestBody Curso c) {
        // Salva o curso enviado no corpo da requisição
        return repo.save(c);
    }

    // -------------------------
    // EDITAR CURSO EXISTENTE
    // -------------------------
    @PutMapping("/{id}")
    public Curso editar(@PathVariable Long id, @RequestBody Curso c) {
        // Garante que o ID enviado na URL será usado
        c.setId(id);

        // Atualiza os dados no banco
        return repo.save(c);
    }

    // -------------------------
    // DELETAR CURSO
    // -------------------------
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        // Exclui o curso pelo ID
        repo.deleteById(id);
    }

    // -------------------------
    // BUSCAR UM CURSO PELO ID
    // -------------------------
    @GetMapping("/{id}")
    public Curso buscarPorId(@PathVariable Long id) {
        // Busca o curso ou retorna null se não existir
        return repo.findById(id).orElse(null);
    }
}
