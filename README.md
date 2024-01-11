# Teste


---


---

- [Getting started](#getting-started)
    - [Introdução](#introdução)
    - [Build configuration](#build-configuration)
    - [Como baixar ? ](#como-baixar-e-rodar-este-projeto)


## Introdução

Gostaria de apresentar o projeto SumaxxSoft desenvolvido para o teste tecnico para a empresa MaxSoft, que permite gerenciar os clientes e serviços prestados de uma empresa. Com ela, é possível realizar as seguintes operações:

* Deve ser possível realizar as operações CRUD em um produto;
* O usuário pode inativar um produto ou excluir permanentemente;
* Os produtos devem ser listados com paginação, com possibilidade de
  escolher a quantidade de itens por página e ordenação de todos os
  campos;
* A listagem  deve permitir filtrar pelo usuário que cadastrou o
   produto;
* A listagem deve permitir filtrar por múltiplos campos. Exemplo:
  filtrar por Nome, Nome e Categoria, Nome e Categoria e Data de cadastro.
  Deve ser possível filtrar por qualquer um dos campos;
* O sistema deve permitir que o administrador crie uma regra para quais
  campos não serão exibidos para o estoquista. Exemplo: o administrador
  não quer exibir ICMS e Custo. O administrador pode alterar essa regra
  para os campos exibidos a qualquer momento. 
* O sistema deve ter um endpoint que liste os valores agregados dos
   produtos. Exemplo:
   Produto A - Custo: R$ 1,00 - Custo Total: R$ 10,00 - Quantidade: 10 -
   Valor Total Previsto: R$ 20,00. O endpoint deve filtrar da mesma forma
   que os itens 4 e 5.
* O sistema deve emitir um relatório dos produtos em formato CSV ou
   XLSX (o usuário deve escolher qual formato), podendo filtrar os dados da
   mesma forma que os itens 4 e 5. O usuário, no momento da geração do
   relatório, poderá escolher quais campos do produto deseja exibir no
   relatório. Exemplo: todos, apenas ID e nome e SKU e etc. 
* O sistema deve registrar auditoria de todos os eventos realizados
   (criação, atualização, exclusão e etc.), registrando as seguintes
   informações: objeto alterado, ação realizada (inclusão, alteração,
   exclusão), data/hora e usuário que realizou a alteração. Deve ser
   possível detalhar a auditoria, mostrando o campo alterado, valor
   anterior e valor atual.
* O usuário estoquista, na atualização de um produto, não deve alterar
    o Valor de Custo e ICMS.

## Autorização e autenticação

* A API deve utilizar a estratégia de autenticação JWT. 
* Deve utilizar o conceito de refresh token, com um tempo de 5 minutos.
* O sistema deve ter dois níveis de acesso: administrador e estoquista.
* O sistema deve permitir que o administrador crie uma regra para quais
  campos não serão exibidos para o estoquista. Exemplo: o administrador
  não quer exibir ICMS e Custo. O administrador pode alterar essa regra
  para os campos exibidos a qualquer momento.

Foi utilizado uma arquitetura REST e os métodos HTTP padrão
(GET, POST, PUT e DELETE) para disponibilizar essas funcionalidades.

Para garantir a qualidade dos dados, foram incluídas validações e em alguns campos.

* Validação e máscaras para input
* Tratamento de Exceptions
* Foi feita a documentação da Api com Swagger.
  para acessar a doc após baixar o projeto entrar no endereço http://localhost:8080/swagger-ui.html
  previa da Documentação da API.

## Build configuration

Dependências

| Dependência                                       | Grupo                    | Artefato                           | Versão   | Escopo | Opcional |
|---------------------------------------------------|--------------------------|------------------------------------|----------|--------|----------|
| spring-boot-starter-data-jpa                      | org.springframework.boot | spring-boot-starter-data-jpa       |          |        |          |
| spring-boot-starter-security                      | org.springframework.boot | spring-boot-starter-security       |          |        |          |
| spring-boot-starter-validation                   | org.springframework.boot | spring-boot-starter-validation    |          |        |          |
| spring-boot-starter-web                           | org.springframework.boot | spring-boot-starter-web            |          |        |          |
| spring-data-envers                                | org.springframework.data | spring-data-envers                 | 3.2.1    |        |          |
| spring-boot-devtools                              | org.springframework.boot | spring-boot-devtools               |          | runtime| true     |
| postgresql                                        | org.postgresql           | postgresql                         | 42.7.1   |        |          |
| lombok                                            | org.projectlombok         | lombok                             |          |        | true     |
| spring-boot-starter-test                          | org.springframework.boot | spring-boot-starter-test           |          | test   |          |
| spring-security-test                              | org.springframework.security| spring-security-test             |          | test   |          |
| modelmapper                                       | org.modelmapper          | modelmapper                        | 3.2.0    |        |          |
| springdoc-openapi-starter-webmvc-ui               | org.springdoc            | springdoc-openapi-starter-webmvc-ui | 2.3.0    |        |          |
| java-jwt                                          | com.auth0                | java-jwt                           | 4.4.0    |        |          |
| javers-core                                       | org.javers               | javers-core                        | 7.3.6    |        |          |
| super-csv                                         | net.sf.supercsv          | super-csv                          | 2.4.0    |        |          |
| poi                                               | org.apache.poi            | poi                                | 5.2.5    |        |          |
| poi-ooxml                                         | org.apache.poi            | poi-ooxml                          | 5.2.3    |        |          |


## Como baixar e rodar este projeto

Para baixar e rodar o projeto Java, siga os passos abaixo:

1. Faça o clone do projeto no GitHub:

`git@github.com:GabrielPereira187/desafio.git`

2. Entre na pasta do projeto:

``cd desafio``

3. Execute o comando abaixo para baixar as dependências do Maven:

``mvn clean install``

4. Execute o projeto com o comando abaixo:

``mvn spring-boot:run``

5. Acesse a aplicação em seu navegador pelo endereço: `http://localhost:8080`