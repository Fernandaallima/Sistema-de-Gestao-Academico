package com.alunos.backend.repository;

import com.alunos.backend.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByTurmaId(Long turmaId);
    List<Nota> findByAlunoId(Long alunoId);
}
