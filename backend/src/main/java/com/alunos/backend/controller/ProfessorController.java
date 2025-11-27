package com.alunos.backend.controller;

import com.alunos.backend.model.Professor;
import com.alunos.backend.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Indica que esta classe é um controller REST (responde com JSON)
@RestController

// Libera o acesso CORS para o frontend
@CrossOrigin(origins = "*")

// Define o caminho base dos endpoints: /professores
@RequestMapping("/professores")
public class ProfessorController {

    // Injeta automaticamente o repositório de professor
    @Autowired
    private ProfessorRepository repo;

    // -----------------------------------
    // LISTAR TODOS OS PROFESSORES
    // -----------------------------------
    @GetMapping
    public List<Professor> listar() {
        // Retorna todos os registros da tabela professor
        return repo.findAll();
    }

    // -----------------------------------
    // CRIAR UM NOVO PROFESSOR
    // -----------------------------------
    @PostMapping
    public Professor criar(@RequestBody Professor p) {
        // Salva o professor enviado no corpo da requisição
        return repo.save(p);
    }

    // -----------------------------------
    // EDITAR UM PROFESSOR EXISTENTE
    // -----------------------------------
    @PutMapping("/{id}")
    public Professor editar(@PathVariable Long id, @RequestBody Professor p) {
        // Garante que o ID enviado na URL será aplicado no objeto
        p.setId(id);

        // Atualiza os dados no banco
        return repo.save(p);
    }

    // -----------------------------------
    // EXCLUIR UM PROFESSOR POR ID
    // -----------------------------------
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        // Remove o registro correspondente ao ID
        repo.deleteById(id);
    }

    // -----------------------------------
    // BUSCAR UM PROFESSOR PELO ID
    // -----------------------------------
    @GetMapping("/{id}")
    public Professor buscarPorId(@PathVariable Long id) {
        // Retorna o professor ou null caso não exista
        return repo.findById(id).orElse(null);
    }

}
