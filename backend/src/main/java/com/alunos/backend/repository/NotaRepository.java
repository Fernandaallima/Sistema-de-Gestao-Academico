package com.alunos.backend.repository;

import com.alunos.backend.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByTurmaId(Long turmaId);  // Buscar todas as notas daquela turma

    List<Nota> findByAlunoId(Long alunoId);  // Buscar notas de um aluno específico

    void deleteByTurmaId(Long turmaId);      // 🔥 Deletar todas notas pelo turma_id
}
