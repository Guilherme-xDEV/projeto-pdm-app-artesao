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

    style Room fill:#e91e63,stroke:#880e4f,stroke-width:2px,color:#fff
    style Postgres fill:#e91e63,stroke:#880e4f,stroke-width:2px,color:#fff
    style Internet fill:#2196f3,stroke:#0d47a1,stroke-dasharray: 5 5,color:#fff
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
    classDef presentation fill:#2196f3,stroke:#0d47a1,stroke-width:2px,color:#fff;
    classDef logic fill:#ffc107,stroke:#ff8f00,stroke-width:2px,color:#000;
    classDef data fill:#4caf50,stroke:#2e7d32,stroke-width:2px,color:#fff;
    classDef infrastructure fill:#e91e63,stroke:#880e4f,stroke-width:2px,color:#fff;

    class AuthUI,ProdUI,SalesUI,DashUI presentation;
    class AuthVM,ProdVM,SalesVM logic;
    class Repo,Session data;
    class Room,API,Security,DB infrastructure;
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
    style User_Environment fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#000
    style Cloud_Infrastructure fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px,color:#000
    style Backend_Node fill:#bbdefb,stroke:#1976d2,stroke-dasharray: 5 5,color:#000
    style Database_Node fill:#f8bbd0,stroke:#c2185b,stroke-dasharray: 5 5,color:#000
```

---

## 4. System Architecture (Class Diagram - Compact)

Simplified view focusing on dependencies and core logic. Metadata and trivial fields are hidden to improve readability.

### Complete
```mermaid
classDiagram
    direction TB

    %% Presentation Layer
    namespace Presentation {
        class LoginViewModel {
            +loginRemote(email, senha)
        }
        class CadastroArtesaoViewModel {
            +signupRemote(dto)
        }
        class DashboardViewModel {
            -artesaoAtual: StateFlow
            -produtos: StateFlow
            -vendas: StateFlow
            +refreshDados()
        }
        class ProdutosViewModel {
            -produtos: StateFlow
            +deletarProduto(id)
        }
        class CadastroProdutoViewModel {
            +cadastrarRemote(dto)
        }
        class VendaViewModel {
            -vendas: StateFlow
            +registrarVendaRemote(produtoId, qtd)
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
            +String telefone
            +String email
            +String senha
        }
        class Produto {
            +Long id
            +String nome
            +String descricao
            +Double preco
            +Int quantidadeEstoque
            +String qrCodeId
            +Boolean ativo
            +Long artesaoId
        }
        class Venda {
            +Long id
            +String produto
            +String artesao
            +Long artesaoId
            +String vendedor
            +String valor
            +Int quantidade
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
    CadastroArtesaoViewModel ..> ArtesaoRepository : uses
    DashboardViewModel ..> ProdutoRepository : uses
    DashboardViewModel ..> VendaRepository : uses
    ProdutosViewModel ..> ProdutoRepository : uses
    CadastroProdutoViewModel ..> ProdutoRepository : uses
    VendaViewModel ..> VendaRepository : uses
    ProfileViewModel ..> ArtesaoRepository : uses
    
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
    Produto "1" --o "0..*" Venda : sold in
    Artesao "1" --o "0..*" Venda : performs
```

### Compacted
```mermaid
classDiagram
    direction TB

    %% Presentation Layer
    class ViewModels {
        +LoginVM
        +DashboardVM
        +ProdutosVM
        +VendaVM
        +ProfileVM
    }

    %% Domain Layer
    class Models {
        +Artesao
        +Produto
        +Venda
    }

    %% Data Layer
    class Repositories {
        +ArtesaoRepo
        +ProdutoRepo
        +VendaRepo
    }

    class SessionManager {
        <<Singleton>>
        +artesaoAtual
    }

    %% Infrastructure
    class Local_Persistence {
        <<Room>>
        +ArtesaoDao
        +ProdutoDao
        +VendaDao
    }

    class Remote_API {
        <<Retrofit>>
        +AuthAPI
        +ProdutoAPI
        +VendaAPI
    }

    %% Relationships
    ViewModels ..> Repositories : calls
    Repositories ..> Local_Persistence : local storage
    Repositories ..> Remote_API : sync remote
    Repositories ..> SessionManager : updates
    
    %% Business Logic Links
    Models "1" --o "*" Models : associations
    Repositories ..> Models : manages

    style ViewModels fill:#2196f3,stroke:#0d47a1,color:#fff
    style Repositories fill:#ffc107,stroke:#ff8f00,color:#000
    style Local_Persistence fill:#4caf50,stroke:#2e7d32,color:#fff
    style Remote_API fill:#e91e63,stroke:#880e4f,color:#fff
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

This diagram represents the database schema of the Spring Boot backend, updated to reflect the Artisan as the primary actor for all operations (including Sales).

```mermaid
erDiagram
    ARTESAO ||--o{ PRODUTO : "cadastra"
    ARTESAO ||--o{ VENDA : "realiza"
    PRODUTO ||--o{ VENDA : "vendido_em"

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

    VENDA {
        Long venda_id PK
        datetime dataHora
        Double valor
        Integer quantidade
        Long artesao_id FK
        Long produto_id FK
    }
```

---

## 9. Use Case Diagram (UML Standard)

This diagram describes the functional requirements and the interaction between the primary actor (Artisan) and the system.

```mermaid
flowchart LR
    %% Actor
    subgraph Actor ["Ator"]
        A((Artesão))
    end

    %% System Boundary
    subgraph System ["NProjetoArtesanato - Sistema de Gestão"]
        direction TB
        
        %% Use Cases
        UC1([Autenticação e Perfil])
        UC2([Cadastrar Produto])
        UC3([Gerenciar Catálogo])
        UC4([Visualizar Métricas])
        UC5([Gerar QR Code])
        UC6([Registrar Venda])
        UC7([Consultar Histórico])
        UC8([Sincronizar Dados])
    end

    %% Associations
    A --- UC1
    A --- UC2
    A --- UC3
    A --- UC4
    A --- UC5
    A --- UC6
    A --- UC7
    A --- UC8

    %% Relationships (Include/Extend)
    UC2 -.->|include| UC8
    UC6 -.->|include| UC8
    UC6 -.->|update stock| UC3
    UC5 -.->|extend| UC3

    %% Styling to look like Use Case
    classDef actor fill:#ececff,stroke:#9370db,stroke-width:2px,color:#000;
    classDef usecase fill:#2196f3,stroke:#0d47a1,stroke-width:2px,color:#fff;
    
    class A actor;
    class UC1,UC2,UC3,UC4,UC5,UC6,UC7,UC8 usecase;
```
