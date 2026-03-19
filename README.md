# 🐾 Adopet API

API REST desenvolvida em **Java** utilizando **Spring boot** para gerenciamento de adoção de pets.
A aplicação permite o cadastro de **tutores**, **pets**, **abrigos**, além de gerenciar todo o fluxo de adoção, aprovação e reprovação, aplicando regras de negócio e boas práticas de desenvolvimento de APIs REST.

## ⚙️ Ferramentas utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Bean Validation
* Lombok
* OpenAPI / Swagger
* Maven

## 🧱 Arquitetura do projeto

O projeto segue uma arquitetura em camadas:

* **Controller**: responsável por receber e controlar fluxo de requisições e respostas HTTP
* **DTOs**: responsáveis pela comunicação entre cliente e API
* **Service**: gerencia o fluxo das regras de negócio
* **Repository**: comunicação com o banco de dados

## 📈 Aprendizado com o desenvolvimento
* Arquitetura em camadas
* DTO pattern e validações de dados
* Tratamento global de exceções e padronização de retornos de erros
* Refatoração de código (Encapsulamento de entidades, validators para regras de negócio)
* Clean code
* Documentação de APIs

## ▶️ Como rodar o projeto localmente

1️⃣ Clone o repositório
```
git clone https://github.com/Gabriel-Labritz/apopet_api.git
```

2️⃣ No Windows, abra o menu **Iniciar** e pesquise por **"Variáveis de Ambiente"**.

3️⃣ Clique em **"Editar as variáveis de ambiente do sistema"**.

4️⃣ Clique em **"Variáveis de Ambiente..."**.

5️⃣ Em **Variáveis de usuário**, clique em **"Novo..."**.

6️⃣ Adicione as variáveis:

* **Nome da variável** -> DB_HOST, DB_NAME, DB_USERNAME, DB_PASSWORD
* **Valor da variável**

> [!NOTE]
> Os valores das variáveis serão definidas por você como: Nome do banco, host etc.

7️⃣ Após adicionar, clique em **OK** para salvar.

8️⃣ Reinicie o PC e depois abra o projeto na sua IDE de preferência.

## 📚 Documentação da API

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

Através do Swagger é possível:

* visualizar todos os endpoints
* testar requisições
* verificar exemplos de requisições e respostas
* entender os códigos de erro da API
## 👨‍💻 Autor

Desenvolvido por **Gabriel Labritz**

* LinkedIn: https://www.linkedin.com/in/gabriel-labritz-b88a2739a/
* GitHub: https://github.com/Gabriel-Labritz
