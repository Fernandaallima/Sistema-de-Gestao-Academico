package com.alunos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alunos.backend.model.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
}
