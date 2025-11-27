# Sistema de Controle de Cursos e Alunos (SGA)

## Descrição
O SGA é um sistema acadêmico desenvolvido para gerenciar de forma integrada **Alunos, Cursos, Professores, Turmas e Notas**. Ele permite cadastro, edição, exclusão e consultas, além de calcular médias e exibir a situação do aluno de forma dinâmica.

O sistema utiliza uma arquitetura **em camadas**, com backend em **Java + Spring Boot**, frontend em **HTML, CSS e JavaScript (SPA)**, e banco de dados **MySQL**, comunicando-se via **API REST**.

---

## Tecnologias Utilizadas
- **Backend:** Java 21, Spring Boot 3.4, Spring Data JPA  
- **Frontend:** HTML, CSS, JavaScript (SPA modular)  
- **Banco de Dados:** MySQL, MySQL Workbench  
- **Ferramentas:** IntelliJ IDEA (backend), Visual Studio Code (frontend), Maven (build)

---

## Estrutura do Projeto
/backend
└─ models, controllers, repositories, dtos (Java + Spring Boot)
/frontend
└─ HTML, CSS e JS modular (SPA)
/README.md


---

## Funcionalidades Principais
- CRUD completo para **Alunos, Cursos, Professores e Turmas**
- Lançamento e cálculo automático de **Notas e Médias**
- Dashboard e tabelas **dinâmicas e responsivas**
- Validações de campos obrigatórios, integridade referencial e alertas amigáveis
- Interface SPA simples para melhor experiência do usuário

---

## Como Rodar o Projeto

### Backend (Spring Boot)
1. Certifique-se de ter **Java 21** e **MySQL** instalados.  
2. Crie um banco de dados chamado `pessoasdb` no MySQL.  
3. Atualize o arquivo `application.properties` com usuário e senha do MySQL.  
4. Abra o backend no **IntelliJ IDEA** e rode a classe principal `BackendApplication.java`.  
5. O servidor irá iniciar em: `http://localhost:8080`.

### Frontend (HTML, CSS, JS)
1. Abra a pasta `/frontend` no **VS Code**.  
2. Use a extensão **Live Server** para rodar o frontend localmente.  
3. O frontend irá consumir a API do backend para exibir dados dinamicamente.

---

## Próximos Passos
- Implementar **login e perfis de usuário (JWT)**
- Adicionar **relatórios em PDF e exportação CSV**
- Incluir **gráficos de desempenho** para análise de notas

---

## Autor
**Fernanda Alencar de Lima** – Programação Orientada a Objetos I  
Prof. Orientador: Alexsander Barreto  
Novembro/2025
