package com.alunos.backend.repository;

import com.alunos.backend.model.Professor;  // Importa a entidade Professor
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> { // Repositório para CRUD de Professor
}
