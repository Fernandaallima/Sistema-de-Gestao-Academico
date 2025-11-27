package com.alunos.backend.dto;

// DTO criado para enviar informações resumidas da Turma ao frontend
// Ele evita enviar objetos completos e pesados (Curso, Professor),
// retornando apenas os dados necessários para exibição.
public class TurmaDTO {

    // Identificador único da turma
    private Long id;

    // Nome da turma (Ex: "Turma A", "1º Ano")
    private String nome;

    // Nome do curso associado à turma
    // Enviado como texto em vez de um objeto Curso completo
    private String cursoNome;

    // Nome do professor responsável
    // Enviado como texto em vez de um objeto Professor completo
    private String professorNome;

    // Construtor que recebe todos os atributos do DTO
    // Usado para montar uma resposta personalizada no Controller
    public TurmaDTO(Long id, String nome, String cursoNome, String professorNome) {
        this.id = id;
        this.nome = nome;
        this.cursoNome = cursoNome;
        this.professorNome = professorNome;
    }

    // Getters e Setters — permitem acessar e modificar os atributos

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCursoNome() { return cursoNome; }
    public void setCursoNome(String cursoNome) { this.cursoNome = cursoNome; }

    public String getProfessorNome() { return professorNome; }
    public void setProfessorNome(String professorNome) { this.professorNome = professorNome; }
}
