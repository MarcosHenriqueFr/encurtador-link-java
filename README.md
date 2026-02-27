
# Encurtador de Link 🔗🌐

## Tecnologias 🖥️
<ul>
    <li>Java 21
    <li>Maven 4
    <li>Spring Boot 3.4.5
    <li>JPA (Hibernate)
    <li>MySQL
    <li>Docker e Docker compose
    <li>Traefik
    <li>JUnit e Mockito
</ul>

## Como testar o projeto 🚀

### Pré-requisitos

Antes de iniciar o projeto, é necessário baixar os itens a seguir:
<ul>
    <li>JDK 21
    <li>Git
    <li>Banco MySQL ou Docker configurado
</ul>

### Clonando

Primeiro clone o projeto para uma pasta da sua máquina:

```bash
git clone https://github.com/MarcosHenriqueFr/encurtador-link-java
```
Depois entre na pasta criada:

```bash 
cd encurtador-link-java
```

### Configurando as variáveis de ambiente

#### 1. application.properties

Mude de acordo com as suas necessidades, unicamente se for trabalhar localmente com o projeto.
Porque o Docker-compose usa a imagem do projeto no docker hub.

#### 2. Chaves públicas e privadas

Para configurar as chaves é preciso usar o **openssl**.
Primeiro no seu terminal vá até a pasta de resources:

Um ponto importante a se destacar que o local das chaves vai importar dependendo
da forma como você vai rodar o projeto.

**Para rodar localmente sem o Docker**:
```bash
cd src/main/resources
```

**Ou se quiser rodar usando o Docker compose (mais prático):**
```bash
mkdir keys
cd keys
```

Depois gere a chave privada usando o openssl:

```bash
openssl genrsa -out app.key 2048
```

Por fim, extraia a chave pública da chave privada:

```bash
openssl rsa -in app.key -pubout -out app.pub
```

Os caminhos das chaves já estão configuradas no application.properties.

#### **Agora volte para a raiz do projeto**

### Rodando o projeto

Para rodar **sem o Docker:**

```bash
mvn spring-boot:run
```

Caso não possua o maven digite:
```bash
./mvnw spring-boot:run
```

Para rodar **usando o Docker:**
```bash
docker-compose up --build
```

*OBS: Dependendo do sistema operacional, será necessário fazer mudanças nas políticas de permissão ao socket do docker*

---

### Parando o projeto
```bash
docker-compose stop
```

Para apagar os containers:
```bash
docker-compose down -v
```

## Endpoints 🚩

| Endpoint                                 | Descrição                                              |
|------------------------------------------|--------------------------------------------------------|
| <kbd>POST /users/register</kbd>          | Cadastra o usuário no banco de dados.                  |
| <kbd>POST /users/login</kbd>             | Autentifica o usuário na API.                          |
| <kbd>POST /api/shorten</kbd>             | Encurta os links de forma anônima ou autenticada.      |
| <kbd>GET /api/links</kbd>                | Retorna os links criados pelo usuário.                 |
| <kbd>DELETE /api/links/{shortCode}</kbd> | Apaga um link curto criado pelo usuário.               |
| <kbd>GET /api/info/{shortCode}</kbd>     | Pega os logs de acesso de um link criado pelo usuário. |
| <kbd>GET /{shortCode}</kbd>              | Redireciona a pessoa que criou o link para o site.     |

<br>

O conjunto de Requisições da Client API estará na no arquivo `encurtador-link-collection.yaml`
na raiz do projeto, para testar com mais facilidade.
Simplesmente importe o arquivo YAML para sua Client API de preferência.

## O que foi aprendido 📝

<ul>
    <li> Como fazer testes unitários;
    <li> Como configurar o uso do docker juntamente do docker compose;
    <li> O trabalho de pool para o banco de dados dentro do arquivo de configuração;
    <li> Uso de mappers com mapstruct;
    <li> Aplicação do lombok para padrões builders/getters/setters;
    <li> Criação da imagem da API.
    <li> Como usar proxy para configurar o acesso ao docker e retirar informações de rede mesmo em um contâiner.
</ul>

## Possíveis Evoluções 📈


<ul>
    <li> Aplicação de um frontend para o projeto.
    <li> Uso de cache e filas para melhorar a performance da API.
</ul>
<br><br>

**Obrigado pela sua atenção. Qualquer feedback é bem-vindo!**
