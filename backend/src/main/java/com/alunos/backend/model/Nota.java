package com.alunos.backend.model;

import jakarta.persistence.*;

@Entity
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // vincula a nota ao aluno
    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    // vincula a nota à turma
    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    private Double nota1;
    private Double nota2;

    private Double media;
    private String status; // "APROVADO" ou "REPROVADO"

    public Nota() {}

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public Double getNota1() { return nota1; }
    public void setNota1(Double nota1) { this.nota1 = nota1; }

    public Double getNota2() { return nota2; }
    public void setNota2(Double nota2) { this.nota2 = nota2; }

    public Double getMedia() { return media; }
    public void setMedia(Double media) { this.media = media; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
