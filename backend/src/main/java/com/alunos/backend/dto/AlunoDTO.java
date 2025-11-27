package com.alunos.backend.dto;

// DTO usado para enviar dados simplificados do aluno ao frontend
public class AlunoDTO {

    // Campos que serão enviados como resposta
    // Diferente do model Aluno, aqui só mandamos o necessário
    private Long id;
    private String nome;
    private String email;
    private String curso;      // Nome do curso (em vez do objeto completo)
    private String professor;  // Nome do professor
    private String turma;      // Nome da turma

    // Construtor que recebe todos os atributos do DTO
    // Usado no AlunoController para montar as respostas personalizadas
    public AlunoDTO(Long id, String nome, String email, String curso, String professor, String turma) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.curso = curso;
        this.professor = professor;
        this.turma = turma;
    }

    // Getters e Setters — permitem acessar e modificar os atributos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }
}
