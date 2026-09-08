# Mermaid Diagrams - NProjetoArtesanato

This document contains the visual representation of the application's architecture and flows.

---

## 1. System Architecture (Class Diagram)

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
            +insert()
            +getByEmail()
        }
        class ProdutoDao {
            <<Interface>>
            +insertAll()
            +getByArtesao()
            +updateStock()
        }
        class AuthApiService {
            <<Interface>>
            +login()
            +signup()
        }
        class ProdutoApiService {
            <<Interface>>
            +listarMeusProdutos()
            +criar()
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

    VendaRepository ..> VendaDao : persists
    VendaRepository ..> SessionManager : reads

    Artesao "1" --o "0..*" Produto : owns
```

---

## 2. Authentication & Session Flow

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

## 3. Dashboard Data Sync Flow

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

## 4. Sales Registration Flow

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
        Repo->>PDB: updateStock(id, newQtd)
        Repo->>VDB: insert(vendaEntity)
    end
    
    Repo-->>VM: Success
    VM-->>UI: Exibe Confirmação
```

---

## 5. Use Case Overview

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
