package com.alunos.backend.repository;

import com.alunos.backend.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> { // Repositório para CRUD de Curso
}
