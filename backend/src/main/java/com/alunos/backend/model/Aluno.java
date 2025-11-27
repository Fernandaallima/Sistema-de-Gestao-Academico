package com.alunos.backend.model;

// Importações para JPA e controle de JSON
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // Esta classe vira uma tabela no banco
public class Aluno {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incremento
    private Long id;

    private String nome;   // Nome do aluno
    private String email;  // Email do aluno

    @ManyToOne // Muitos alunos → 1 turma
    @JoinColumn(name = "turma_id") // FK no banco
    @JsonIgnoreProperties({"alunos"}) // Evita loop no JSON
    private Turma turma;

    public Aluno() {} // Construtor padrão

    public Aluno(String nome, String email) { // Construtor auxiliar
        this.nome = nome;
        this.email = email;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    @Transient // Não vira coluna no banco
    public Curso getCurso() {
        return turma != null ? turma.getCurso() : null; // Curso da turma
    }

    @Transient
    public Professor getProfessor() {
        return turma != null ? turma.getProfessor() : null; // Professor da turma
    }
}
