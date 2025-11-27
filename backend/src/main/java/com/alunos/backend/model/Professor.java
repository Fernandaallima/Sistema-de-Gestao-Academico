package com.alunos.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity // Indica que a classe é uma entidade do banco
public class Professor {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento
    private Long id;

    private String nome; // Nome do professor

    @OneToMany(
            mappedBy = "professor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    ) // Relacionamento 1:N com Turma
    @JsonIgnore // Evita loop de JSON
    private List<Turma> turmas; // Lista de turmas do professor


    public Professor() {} // Construtor padrão

    public Long getId() { return id; } // Getter ID
    public void setId(Long id) { this.id = id; } // Setter ID

    public String getNome() { return nome; } // Getter nome
    public void setNome(String nome) { this.nome = nome; } // Setter nome

    public List<Turma> getTurmas() { return turmas; } // Getter turmas
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; } // Setter turmas
}
