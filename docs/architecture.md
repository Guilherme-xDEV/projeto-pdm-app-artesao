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