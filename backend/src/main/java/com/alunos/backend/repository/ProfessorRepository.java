package com.alunos.backend.repository;

import com.alunos.backend.model.Professor;  // <-- IMPORT ESSENCIAL!!!
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
