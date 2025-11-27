package com.alunos.backend.dto;

public class TurmaDTO {
    private Long id;
    private String nome;
    private String cursoNome;
    private String professorNome;

    public TurmaDTO(Long id, String nome, String cursoNome, String professorNome) {
        this.id = id;
        this.nome = nome;
        this.cursoNome = cursoNome;
        this.professorNome = professorNome;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCursoNome() { return cursoNome; }
    public void setCursoNome(String cursoNome) { this.cursoNome = cursoNome; }

    public String getProfessorNome() { return professorNome; }
    public void setProfessorNome(String professorNome) { this.professorNome = professorNome; }
}
