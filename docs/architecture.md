# Project Architecture: NProjetoArtesanato

This project follows the **Clean Architecture** principles using the **MVVM (Model-View-ViewModel)** pattern combined with a **Repository** layer to ensure separation of concerns, testability, and a robust offline-first data flow.

---

## 1. Project Structure

The codebase is organized by technical layers and features:

```text
com.example.nprojetoartesanato/
├── data/               # Data Layer
│   ├── local/          # Local Persistence (Room Database)
│   │   ├── dao/        # Data Access Objects
│   │   └── entities/   # Database Entities & Mappers
│   ├── network/        # Remote Data (Retrofit & API Services)
│   ├── repository/     # Abstraction & Synchronization Logic
│   └── session/        # Global session management (SessionManager)
├── model/              # Domain Layer
│   ├── dto/            # Data Transfer Objects (DTOs)
│   └── [Models]        # Domain classes: Artesao, Produto, Venda
├── navigation/         # Navigation configuration (Compose Navigation)
├── ui/                 # UI Layer (Jetpack Compose)
│   ├── components/     # Reusable Compose widgets
│   ├── theme/          # Material 3 Theme configuration
│   └── [feature]/      # Screens and ViewModels grouped by feature
└── util/               # Helper classes (e.g., QrCodeGenerator)
```

---

## 2. Layers Responsibility

### UI Layer (View + ViewModel)
*   **Screens (Compose)**: Declarative UI components that observe `StateFlow` and emit events.
*   **ViewModels**: Maintain the UI state and handle user interactions by calling Repositories. They are lifecycle-aware and survive configuration changes.

### Repository Layer
*   **Source of Truth**: Manages the synchronization between the **Remote API** and the **Local Database**.
*   **Reactive Flow**: Exposes data as `Flow<List<T>>` to ViewModels, ensuring the UI is always in sync with the local database.

### Data Layer
*   **Room Database**: The primary Source of Truth for the UI. Provides persistent storage and reactive streams (Flow).
*   **Retrofit (API)**: Handles communication with the Spring Boot backend for data persistence and multi-device synchronization.
*   **SessionManager**: A specialized singleton that holds the authentication token and the `artesaoAtual` state.

### Domain Layer (Models)
*   **Entities**: Represent the database schema.
*   **Domain Models**: Represent the business logic objects.
*   **DTOs**: Optimized objects for network transfers.

---

## 3. Data Flux & Synchronization

The application leverages a **Hybrid Sync Strategy**:

1.  **Observability**: ViewModels observe Room DAOs through Repositories. Any change in the database immediately triggers a UI update.
2.  **Write Operations**: When a user performs a write (e.g., adding a product), the Repository first calls the **Remote API**. Upon success, it updates the **Local Database**.
3.  **Read Operations (Sync)**: When the user accesses the Dashboard or lists, the Repository triggers a background fetch from the API to update the Local Database (Delete/Insert strategy).

### Interaction Flow:
`UI (Event) -> ViewModel -> Repository -> API (Success) -> Room (Update) -> Flow (Emit) -> ViewModel (State) -> UI (Recomposition)`

---

## 4. Tech Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Persistence**: Room Persistence Library
*   **Networking**: Retrofit & OKHttp
*   **Asynchronous Support**: Coroutines & Flow
*   **Lifecycle**: Android Jetpack ViewModel
*   **Navigation**: Compose Navigation
*   **Serialization**: GSON
*   **Utilities**: ZXing (QR Code generation)

---

> [!TIP]
> This architecture ensures that the application remains functional even with intermittent connectivity, as the UI always reads from the local database, which is periodically synchronized with the remote server.
