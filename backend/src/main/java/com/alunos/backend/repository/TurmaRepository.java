package com.alunos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alunos.backend.model.Turma;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    //  método necessário para excluir professor sem erro
    List<Turma> findByProfessor_Id(Long professorId);

}
