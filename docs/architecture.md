# Project Architecture: NProjetoArtesanato

This project follows the **MVVM (Model-View-ViewModel)** architectural pattern combined with a **Repository** layer to ensure separation of concerns, testability, and a clean data flow.

---

## 1. Project Structure

The codebase is organized by technical layers and features:

```text
com.example.nprojetoartesanato/
├── data/               # Data Layer
│   ├── local/          # In-memory data source (LocalDataStore)
│   ├── repository/     # Abstraction between UI and Data (Repositories)
│   └── session/        # Global session management (SessionManager)
├── model/              # Domain Layer
│   ├── dto/            # Data Transfer Objects for updates/creation
│   └── (Entities)      # Core models: Artesao, Produto, Venda
├── navigation/         # Navigation configuration and routes
├── ui/                 # UI Layer (Jetpack Compose)
│   ├── components/     # Reusable Compose widgets
│   ├── theme/          # Material 3 Theme configuration
│   └── [feature]/      # Screens and ViewModels grouped by feature
└── util/               # Helper classes (e.g., QrCodeGenerator)
```

---

## 2. Layers Responsibility

### UI Layer (View + ViewModel)
*   **Screens (Compose)**: Declarative UI components that observe state and emit events.
*   **ViewModels**: Maintain the UI state using `StateFlow` and handle user interactions by calling Repositories. They survive configuration changes.

### Repository Layer
*   Acts as a mediator between the ViewModel and the Data Sources.
*   Encapsulates the logic for fetching and saving data, providing a clean API to the ViewModels.

### Data Layer
*   **LocalDataStore**: Currently serves as the "Single Source of Truth" using in-memory `MutableStateFlow` lists. It simulates a database for local persistence during the app's lifecycle.
*   **SessionManager**: A specialized singleton that holds the `artesaoAtual` (logged-in artisan) state, shared across the entire application.

### Domain Layer (Models)
*   **Entities**: Data classes representing the core business objects (`Artesao`, `Produto`, `Venda`).
*   **DTOs**: Lightweight objects used for specific operations like updating a product without passing the entire entity.

---

## 3. Data Flux & State Management

The application leverages **Reactive Programming** with Kotlin Coroutines and StateFlow:

1.  **Observation**: ViewModels observe `Flows` from Repositories, which in turn observe the `StateFlow` lists in `LocalDataStore`.
2.  **Updates**: When a user performs an action (e.g., registering a sale), the ViewModel calls the Repository, which updates the `LocalDataStore`.
3.  **Reaction**: The update in `LocalDataStore` automatically triggers all observing UIs to recompose with the new data.

### Interaction Example:
`UI (Event) -> ViewModel -> Repository -> LocalDataStore (State Update) -> Repository (Flow) -> ViewModel (State) -> UI (Recomposition)`

---

## 4. Tech Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Asynchronous Support**: Coroutines & Flow
*   **Lifecycle**: Android Jetpack ViewModel
*   **Navigation**: Compose Navigation with Sealed Classes
*   **Utilities**: ZXing (for QR Code generation)

---

> [!NOTE]
> While the current implementation uses an in-memory `LocalDataStore`, the architecture is prepared to integrate with external APIs (Retrofit/Spring Boot) or local databases (Room) by simply swapping the implementation within the Repository layer.
