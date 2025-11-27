package com.alunos.backend.controller;

// Importações dos modelos, repositórios, DTOs e anotações do Spring
import com.alunos.backend.model.Aluno;
import com.alunos.backend.model.Turma;
import com.alunos.backend.repository.AlunoRepository;
import com.alunos.backend.repository.TurmaRepository;
import com.alunos.backend.dto.AlunoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import com.alunos.backend.repository.NotaRepository;
import java.util.List;

@RestController // Indica que esta classe expõe endpoints REST
@CrossOrigin(origins = "*") // Libera acesso CORS para qualquer origem (Front-End)
@RequestMapping("/alunos") // Rota base de todos os endpoints deste controller
public class AlunoController {

    @Autowired
    private AlunoRepository repo; // Repositório principal de alunos

    @Autowired
    private TurmaRepository turmaRepository; // Repositório de turmas, usado para vinculação

    @Autowired
    private NotaRepository notaRepo; // ✔ Repositório de notas relacionado a alunos

    // ----------------------------------
    // LISTAR TODOS OS ALUNOS
    // ----------------------------------
    @GetMapping
    public List<Aluno> listar() {
        return repo.findAll(); // Retorna todos os alunos cadastrados
    }

    // ----------------------------------
    // BUSCAR ALUNO POR ID
    // ----------------------------------
    @GetMapping("/{id}")
    public Aluno buscarPorId(@PathVariable Long id) {
        return repo.findById(id).orElse(null); // Busca segura pelo ID
    }

    // ----------------------------------
    // CRIAR ALUNO SIMPLES (sem lógica adicional)
    // ----------------------------------
    @PostMapping
    public Aluno criar(@RequestBody Aluno a) {
        return repo.save(a); // Salva o novo aluno
    }

    // ----------------------------------
    // EDITAR ALUNO
    // ----------------------------------
    @PutMapping("/{id}")
    public Aluno editar(@PathVariable Long id, @RequestBody Aluno a) {
        a.setId(id); // Garante que o ID recebido na URL seja aplicado
        return repo.save(a); // Atualiza o aluno
    }

    // ----------------------------------
    // DELETE COMPLETO
    // Remove aluno, notas e vínculos
    // ----------------------------------
    @DeleteMapping("/{id}")
    @Transactional // Garante que tudo seja executado como uma transação
    public void deletar(@PathVariable Long id) {

        // 1) Carrega o aluno
        Aluno aluno = repo.findById(id).orElse(null);
        if (aluno == null) return; // Se não existir, não faz nada

        // 2) Remove vínculo com a turma para evitar erro de FK
        aluno.setTurma(null);
        repo.save(aluno);

        // 3) Apaga todas as notas vinculadas ao aluno
        notaRepo.findByAlunoId(id).forEach(n -> notaRepo.deleteById(n.getId()));

        // 4) Apaga o aluno definitivamente
        repo.deleteById(id);
    }

    // ----------------------------------
    // LISTAR ALUNOS POR CURSO
    // ----------------------------------
    @GetMapping("/curso/{cursoId}")
    public List<Aluno> listarPorCurso(@PathVariable Long cursoId) {
        return repo.findByTurma_CursoId(cursoId); // Busca baseada no relacionamento
    }

    // ----------------------------------
    // LISTAR ALUNOS POR TURMA
    // ----------------------------------
    @GetMapping("/turma/{turmaId}")
    public List<Aluno> listarPorTurma(@PathVariable Long turmaId) {
        return repo.findByTurma_Id(turmaId); // Busca baseada no relacionamento
    }

    // ----------------------
    // DTO: Retorna Alunos com dados derivados:
    // Curso, Professor e Nome da Turma
    // ----------------------
    @GetMapping("/dto")
    public List<AlunoDTO> listarAlunosDTO() {
        return repo.findAll().stream()
                .map(a -> new AlunoDTO(
                        a.getId(),
                        a.getNome(),
                        a.getEmail(),
                        a.getTurma() != null && a.getTurma().getCurso() != null ? a.getTurma().getCurso().getNome() : null,
                        a.getTurma() != null && a.getTurma().getProfessor() != null ? a.getTurma().getProfessor().getNome() : null,
                        a.getTurma() != null ? a.getTurma().getNome() : null
                ))
                .toList(); // Transforma os dados em lista de DTOs
    }

    // ----------------------
    // POST COMPLETO
    // Criação de aluno com turma vinculada
    // ----------------------
    @PostMapping("/completo")
    public Aluno criarAlunoCompleto(@RequestBody Aluno aluno) {

        // Se foi enviado um ID de turma, busca e vincula
        if(aluno.getTurma() != null && aluno.getTurma().getId() != null){
            Turma turma = turmaRepository.findById(aluno.getTurma().getId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

            aluno.setTurma(turma); // Vincula a turma ao aluno
        }

        return repo.save(aluno); // Salva com vínculo completo
    }
}
