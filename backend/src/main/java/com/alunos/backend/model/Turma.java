package com.alunos.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Entity // Marca a classe como entidade JPA
public class Turma {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incremento
    private Long id;

    private String nome; // Nome da turma

    @ManyToOne(fetch = FetchType.EAGER) // Muitas turmas para um curso
    @JoinColumn(name = "curso_id") // Coluna FK do curso
    @JsonIgnoreProperties({"turmas"}) // Ignora lista de turmas do curso no JSON
    private Curso curso;

    @ManyToOne(fetch = FetchType.EAGER) // Muitas turmas para um professor
    @JoinColumn(name = "professor_id") // Coluna FK do professor
    @JsonIgnoreProperties({"turmas"}) // Evita loop JSON
    private Professor professor;

    @OneToMany(mappedBy = "turma", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties({"turma"}) // impede loop
    private List<Aluno> alunos;

    @OneToMany(
            mappedBy = "turma",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnoreProperties({"turma"})
    private List<Nota> notas;


    public Turma() {} // Construtor padrão

    public Long getId() { return id; } // Getter id
    public void setId(Long id) { this.id = id; } // Setter id

    public String getNome() { return nome; } // Getter nome
    public void setNome(String nome) { this.nome = nome; } // Setter nome

    public Curso getCurso() { return curso; } // Getter curso
    public void setCurso(Curso curso) { this.curso = curso; } // Setter curso

    public Professor getProfessor() { return professor; } // Getter professor
    public void setProfessor(Professor professor) { this.professor = professor; } // Setter professor

    public List<Aluno> getAlunos() { return alunos; } // Getter alunos
    public void setAlunos(List<Aluno> alunos) { this.alunos = alunos; } // Setter alunos
}
