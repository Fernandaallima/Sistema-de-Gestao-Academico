package com.alunos.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity // Indica que esta classe é uma tabela no banco
@JsonIgnoreProperties("turmas") // Evita recursão no JSON (Curso → Turmas → Curso)
public class Curso {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento
    private Long id;

    private String nome; // Nome do curso

    @OneToMany(mappedBy = "curso", fetch = FetchType.EAGER)
    // Um curso → várias turmas / "curso" é o dono na classe Turma
    private List<Turma> turmas;

    public Curso() {} // Construtor padrão

    public Curso(String nome) { this.nome = nome; } // Construtor auxiliar

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Turma> getTurmas() { return turmas; }
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }
}
