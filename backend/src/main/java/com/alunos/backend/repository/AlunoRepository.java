package com.alunos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alunos.backend.model.Aluno;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findByTurma_CursoId(Long cursoId); // Busca alunos pelo ID do curso da turma

    List<Aluno> findByTurma_Id(Long turmaId); // Busca alunos pelo ID da turma
}
