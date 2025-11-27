package com.alunos.backend.model;

// Importações necessárias para mapeamento JPA e controle de JSON
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // Indica que esta classe representa uma tabela no banco de dados
public class Aluno {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento no MySQL
    private Long id;

    private String nome;  // Nome do aluno
    private String email; // Email do aluno

    @ManyToOne // Muitos alunos pertencem a uma única turma
    @JoinColumn(name = "turma_id") // Nome da coluna de chave estrangeira no banco
    @JsonIgnoreProperties({"alunos"})
    // Impede recursão infinita no JSON (Turma tem lista de alunos)
    private Turma turma;

    // Construtor padrão exigido pelo Spring/JPA
    public Aluno() {}

    // Construtor auxiliar para criar aluno mais facilmente
    public Aluno(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Getters e setters padrão
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    // =============================
    // Helpers para JSON no frontend
    // =============================
    @Transient
    // Este dado NÃO vira coluna no banco, apenas no JSON
    public Curso getCurso() {
        // Retorna o curso da turma (relacionamento indireto)
        return turma != null ? turma.getCurso() : null;
    }

    @Transient
    public Professor getProfessor() {
        // Retorna o professor da turma (relacionamento indireto)
        return turma != null ? turma.getProfessor() : null;
    }
}
