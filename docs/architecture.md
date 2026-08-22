## Folders Architecture (MVVM project pattern)

app/
│
├── ui/
│   ├── screens/
│   ├── components/
│   └── navigation/
│
├── viewmodel/
│
├── repository/
│
├── network/
│   ├── dto/
│   ├── RetrofitInstance.kt
│   └── ApiService.kt
│
├── model/
│
└── MainActivity.kt

---

## Request Basic Flux

User

↓
Screen (UI)

↓
ViewModel

↓
Repository

↓
Network (Retrofit)

↓
Spring Boot API

↓

Database

---

## Internal Data Access Flux

model
↓
representation of the application data

data
↓
origin and data persistence

ui
↓
interface and state show to user

navigation
↓
flux among functionalities

---

## How MVVM will retrieve data using Retrofit and Spring and display it into UI?

1. Example with a 'Venda' object

VendaScreen
↓
VendaViewModel
↓
VendaRepository
↓
VendaApi
↓
Retrofit
↓
Spring Boot

2. Example with Artesao login
LoginViewModel
↓
ArtesaoRepository
↓
Retrofit
↓
POST /auth/login
↓
Spring Security / Controller
↓
PostgreSQL

## Current Application Flux

                    LOGIN
                      │
                      ▼
              LoginViewModel
                      │
                      ▼
             ArtesaoRepository
                      │
                      ▼
              LocalDataStore
                      │
                encontrou?
                  /       \
                não       sim
                │          │
              erro         ▼
                       SessionManager
                            │
                            ▼
                        Dashboard
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
       Cadastrar       Registrar       Histórico
        Produto          Venda           Vendas
             │
             ▼
    ProdutoViewModel
             │
             ▼
    ProdutoRepository
             │
             ▼
      LocalDataStore
             │
             ▼
      artesaoId = sessão.id

### Or Simply
Cadastro de Artesão
↓
ArtesaoRepository
↓
LocalDataStore
↓
Login
↓
ArtesaoRepository.autenticar()
↓
SessionManager
↓
Dashboard

### Next Flux
Dashboard
↓
CadastroProdutoScreen
↓
CadastroProdutoViewModel
↓
ProdutoRepository
↓
LocalDataStore
↓
Produto
↓
artesaoId = SessionManager.artesaoAtual.id

---

### New Product Flux

                  Cadastrar
                      │
                      ▼
             CadastroProdutoViewModel
                      │
                      ▼
          Existe artesão autenticado?
                 /          \
               não          sim
                │            │
               erro          ▼
                     Validar campos
                            │
                       ┌────┴────┐
                       │         │
                    inválido    válido
                       │         │
                      erro       ▼
                         Converter dados
                               │
                               ▼
                            Produto
                               │
                     artesaoId = sessão.id
                               │
                               ▼
                    ProdutoRepository
                               │
                               ▼
                        LocalDataStore

CadastroProdutoScreen
│
│ viewModel.cadastrar()
▼
CadastroProdutoViewModel
│
├── verifica artesão autenticado
│
├── valida nome
├── valida descrição
├── valida preço
├── valida estoque
│
▼
Produto
│
│ artesaoId = SessionManager.artesaoAtual.id
▼
ProdutoRepository
│
▼
LocalDataStore