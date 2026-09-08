# Mermaid Diagrams - NProjetoArtesanato

This document contains the visual representation of the application's architecture and flows.

---

## 1. System Architecture Design

High-level overview of the system components, including the mobile client, local persistence, cloud-based backend, and authentication.

```mermaid
graph TD
    subgraph Client_Side ["Dispositivo Android (Client)"]
        UI["Interface (Jetpack Compose)"]
        VM["ViewModels (State Management)"]
        Repo["Repositories (Data Sync)"]
        Room[("Local DB (Room)")]
        
        UI <--> VM
        VM <--> Repo
        Repo <--> Room
    end

    subgraph Network ["Comunicação"]
        Retrofit["Retrofit + OkHttp"]
        JWT["JWT Auth Interceptor"]
        Internet((Internet))
        
        Repo <--> Retrofit
        Retrofit --- JWT
        JWT <--> Internet
    end

    subgraph Server_Side ["Cloud Backend (Railway)"]
        Spring["Spring Boot API"]
        Security["Spring Security (JWT)"]
        Postgres[("PostgreSQL DB")]
        
        Internet <--> Security
        Security <--> Spring
        Spring <--> Postgres
    end

    style Room fill:#f9f,stroke:#333,stroke-width:2px
    style Postgres fill:#f9f,stroke:#333,stroke-width:2px
    style Internet fill:#fff,stroke:#333,stroke-dasharray: 5 5
```

---

## 2. Component Diagram

Visualizes the functional modules of the system, their responsibilities, and how they interact through defined interfaces and services.

```mermaid
graph TB
    subgraph Mobile_App ["Aplicativo Mobile (Android)"]
        subgraph UI_Module ["Módulo de Interface (UI)"]
            AuthUI[Autenticação/Perfil]
            ProdUI[Gestão de Produtos]
            SalesUI[Registro de Vendas]
            DashUI[Dashboard/Métricas]
        end

        subgraph Core_Logic ["Lógica de Negócio (ViewModels)"]
            AuthVM[AuthViewModel]
            ProdVM[ProdutoViewModel]
            SalesVM[VendaViewModel]
        end

        subgraph Data_Module ["Módulo de Dados"]
            Repo[Repositories Sync]
            Room[Room Persistence]
            Session[Session Manager]
        end

        AuthUI --> AuthVM
        ProdUI --> ProdVM
        SalesUI --> SalesVM
        DashUI --> ProdVM
        DashUI --> SalesVM

        AuthVM --> Repo
        ProdVM --> Repo
        SalesVM --> Repo
        
        Repo --> Room
        Repo --> Session
    end

    subgraph Backend_Services ["Serviços Backend (Spring)"]
        API[Spring Boot REST API]
        Security[Módulo de Segurança JWT]
        DB[(PostgreSQL)]
        
        Repo -- "HTTPS/REST" --> Security
        Security --> API
        API --> DB
    end

    %% Interfaces/Services
    classDef component fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    class AuthUI,ProdUI,SalesUI,DashUI,AuthVM,ProdVM,SalesVM,Repo,Room,Session,API,Security component;
```

---

## 3. Deployment Diagram

Illustrates the physical deployment of the system, showing where the components are executed and how they communicate across different environments.

```mermaid
graph TD
    subgraph User_Environment ["Ambiente do Usuário"]
        Device["Android Device<br/>(Smartphone/Tablet)"]
        subgraph Mobile_Runtime ["Runtime de Execução"]
            App["NProjetoArtesanato.apk<br/>(Jetpack Compose App)"]
            Room[("SQLite / Room DB")]
        end
        Device --- App
        App --- Room
    end

    subgraph Cloud_Infrastructure ["Railway Cloud (PaaS)"]
        subgraph Backend_Node ["Docker Container: API"]
            Spring["Spring Boot Service<br/>(Java JRE 21)"]
        end

        subgraph Database_Node ["Instância de Banco de Dados"]
            Postgres[("PostgreSQL Server")]
        end
    end

    %% Communication paths
    App -- "HTTPS / TLS (Port 443)<br/>REST API + JWT" --> Spring
    Spring -- "JDBC / PostgreSQL Protocol<br/>Internal Network" --> Postgres

    %% Styling
    style User_Environment fill:#f5f5f5,stroke:#333,stroke-width:2px
    style Cloud_Infrastructure fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    style Backend_Node fill:#fff,stroke:#1e88e5,stroke-dasharray: 5 5
    style Database_Node fill:#fff,stroke:#1e88e5,stroke-dasharray: 5 5
```

---

## 4. System Architecture (Class Diagram)

Describes the structure of the application, showing the relationships between UI components, repositories, local persistence (Room), and remote synchronization (Retrofit).

```mermaid
classDiagram
    direction TB

    %% Presentation Layer
    namespace Presentation {
        class LoginViewModel {
            +loginRemote(email, senha)
        }
        class DashboardViewModel {
            -artesaoAtual: StateFlow
            -produtos: StateFlow
            -vendas: StateFlow
            +refreshDados()
        }
        class ProfileViewModel {
            +atualizarPerfil(nome, telefone)
            +logout()
        }
    }

    %% Domain Layer (Models)
    namespace Domain {
        class Artesao {
            +Long id
            +String nome
            +String email
        }
        class Produto {
            +Long id
            +String nome
            +Double preco
            +Int quantidadeEstoque
            +String qrCodeId
        }
        class Venda {
            +Long id
            +String produto
            +String valor
            +String dataHora
        }
    }

    %% Data Layer (Repositories & Session)
    namespace Data {
        class ArtesaoRepository {
            +loginRemote()
            +autenticar()
            +atualizar()
        }
        class ProdutoRepository {
            +listarMeusProdutosRemote()
            +observarPorArtesao()
            +cadastrar()
        }
        class VendaRepository {
            +registrarVendaRemote()
            +sincronizarHistoricoRemote()
            +observarTodas()
        }
        class SessionManager {
            <<Singleton>>
            +artesaoAtual: StateFlow
            +token: String
            +iniciarSessao()
            +encerrarSessao()
        }
    }

    %% Infrastructure (Room & Retrofit)
    namespace Infrastructure {
        class ArtesaoDao {
            <<Interface>>
            +insert(artesao)
            +getByEmail(email)
        }
        class ProdutoDao {
            <<Interface>>
            +insertAll(list)
            +getByArtesao(id)
            +update(produto)
        }
        class VendaDao {
            <<Interface>>
            +insert(venda)
            +getAll()
        }
        class AuthApiService {
            <<Interface>>
            +login(request)
            +signup(dto)
        }
        class ProdutoApiService {
            <<Interface>>
            +listarMeusProdutos()
            +criar(dto)
        }
        class VendaApiService {
            <<Interface>>
            +registrar(request)
            +listarMinhasVendas()
        }
    }

    %% Relationships
    LoginViewModel ..> ArtesaoRepository : uses
    DashboardViewModel ..> ProdutoRepository : uses
    DashboardViewModel ..> VendaRepository : uses
    
    ArtesaoRepository ..> AuthApiService : calls
    ArtesaoRepository ..> ArtesaoDao : persists
    ArtesaoRepository ..> SessionManager : updates

    ProdutoRepository ..> ProdutoApiService : calls
    ProdutoRepository ..> ProdutoDao : persists

    VendaRepository ..> VendaApiService : calls
    VendaRepository ..> VendaDao : persists
    VendaRepository ..> ProdutoDao : updates stock
    VendaRepository ..> SessionManager : reads

    Artesao "1" --o "0..*" Produto : owns
```

---

## 5. Authentication & Session Flow

Describes how a user logs in and how the session is established both locally and remotely.

```mermaid
sequenceDiagram
    autonumber
    actor User as Artesão
    participant UI as LoginScreen
    participant VM as LoginViewModel
    participant Repo as ArtesaoRepository
    participant API as AuthApiService
    participant DB as ArtesaoDao
    participant Session as SessionManager

    User->>UI: Insere Email e Senha
    UI->>VM: loginRemote(email, senha)
    VM->>Repo: loginRemote(email, senha)
    
    Repo->>API: POST /auth/login
    API-->>Repo: Retorna Token + Dados Artesão
    
    Repo->>DB: insert(artesaoEntity)
    Repo->>Session: iniciarSessao(artesao, token)
    
    Session-->>VM: StateFlow artesaoAtual atualizado
    VM-->>UI: Navega para Dashboard
```

---

## 6. Dashboard Data Sync Flow

Shows how the dashboard stays updated by fetching remote data and updating the local source of truth.

```mermaid
sequenceDiagram
    autonumber
    participant UI as DashboardScreen
    participant VM as DashboardViewModel
    participant PRepo as ProdutoRepository
    participant VRepo as VendaRepository
    participant PAPI as ProdutoApiService
    participant PDB as ProdutoDao

    UI->>VM: init / refreshDados()
    
    par Sync Products
        VM->>PRepo: listarMeusProdutosRemote()
        PRepo->>PAPI: GET /produtos
        PAPI-->>PRepo: Lista de Produtos
        PRepo->>PDB: deleteByArtesao()
        PRepo->>PDB: insertAll(remoteList)
    and Sync Sales
        VM->>VRepo: sincronizarHistoricoRemote()
        VRepo->>VRepo: Sincroniza com VendaApiService
    end

    Note over PDB, UI: Room emite Flow atualizado
    PDB-->>PRepo: List<ProdutoEntity>
    PRepo-->>VM: List<Produto> (Flow)
    VM-->>UI: Recomposition (UI atualizada)
```

---

## 7. Sales Registration Flow

Details the process of recording a sale, involving stock update and persistence.

```mermaid
sequenceDiagram
    autonumber
    actor User as Vendedor
    participant UI as RegistrarVendaScreen
    participant VM as VendaViewModel
    participant Repo as VendaRepository
    participant VAPI as VendaApiService
    participant PDB as ProdutoDao
    participant VDB as VendaDao

    User->>UI: Seleciona Produto e Confirma Venda
    UI->>VM: registrarVenda(produto, quantidade)
    VM->>Repo: registrarVendaRemote(produtoId, qtd)
    
    Repo->>VAPI: POST /vendas
    VAPI-->>Repo: Venda Efetuada (Success)
    
    critical Local Updates (Atomic)
        Repo->>PDB: update(produtoEntity)
        Repo->>VDB: insert(vendaEntity)
    end
    
    Repo-->>VM: Success
    VM-->>UI: Exibe Confirmação
```

---

## 8. Entity-Relationship Diagram (ER Diagram)

This diagram represents the database schema of the Spring Boot backend, including entities, attributes, and relationships.

```mermaid
erDiagram
    ARTESAO ||--o{ PRODUTO : "cadastra"
    ARTESAO ||--o{ VENDA : "possui"
    PRODUTO ||--o{ VENDA : "vendido_em"
    VENDEDOR |o--o{ VENDA : "realiza"

    ARTESAO {
        Long artesao_id PK
        String nome
        String telefone
        String email
        String senha
    }

    PRODUTO {
        Long id PK
        String nome
        String descricao
        Double preco
        Integer quantidade_estoque
        String qr_code_id
        Boolean ativo
        Long artesao_id FK
    }

    VENDEDOR {
        Long vendedor_id PK
        String nome
        String telefone
        String senha
    }

    VENDA {
        Long venda_id PK
        datetime dataHora
        Double valor
        Integer quantidade
        Long artesao_id FK
        Long produto_id FK
        Long vendedor_id FK
    }
```

---

## 9. Use Case Overview

Summary of main interactions within the artisan management system.

```mermaid
graph LR
    subgraph Atores
        Artesao((Artesão))
        Vendedor((Vendedor/Artesão))
    end

    subgraph "NProjetoArtesanato - Sistema"
        UC1(Autenticação e Perfil)
        UC2(Gestão de Catálogo)
        UC3(Visualização de Métricas)
        UC4(Geração de QR Code)
        UC5(Registro de Vendas)
        UC6(Histórico de Vendas)
    end

    Artesao --- UC1
    Artesao --- UC2
    Artesao --- UC3
    Artesao --- UC4
    
    Vendedor --- UC5
    Vendedor --- UC6

    UC2 -.->|updates| UC3
    UC5 -.->|decrements stock| UC2
    UC5 -.->|requires| UC1
```
