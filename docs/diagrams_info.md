## System Architecture Design
---

This high-level architecture diagram illustrates the end-to-end flow of the NProjetoArtesanato system, from the user's hand to the cloud.

### 1. Client Side: Dispositivo Android
The mobile application is the primary entry point for the user. It is built using modern Android standards:
*   **UI (Jetpack Compose)**: Handles the rendering of views and user interactions.
*   **ViewModels**: Manages UI state and business logic, decoupling the UI from data sources.
*   **Repositories**: Orchestrates data synchronization between local and remote sources.
*   **Room Database**: Acts as the local "Source of Truth," allowing the app to function offline and providing reactive data updates.

### 2. Communication Layer
Handles the secure transport of data over the internet:
*   **Retrofit & OkHttp**: The HTTP client used for RESTful communication.
*   **JWT Auth Interceptor**: Automatically attaches the Bearer token to requests, ensuring that every call to the backend is authenticated.
*   **Internet**: The public cloud gateway that connects the client to the server.

### 3. Cloud Backend: Server Side
The backend infrastructure is hosted on **Railway** and provides the centralized data storage and logic:
*   **Spring Security**: Validates JWT tokens and protects the API endpoints.
*   **Spring Boot API**: Processes business logic, handles CRUD operations, and manages relationships between entities.
*   **PostgreSQL**: The relational database used for persistent, long-term storage of all artisan, product, and sales data.

---

## Component Diagram
---
This diagram presents the functional organization of the system, dividing it into logical modules and specifying their responsibilities.

### 1. Mobile App (Android)
*   **Interface Module (UI)**: Responsible for the visual representation of features like Authentication, Product Management, Sales, and Dashboard.
*   **Core Logic (ViewModels)**: Orchestrates the business logic for each UI module, acting as the bridge to the data layer.
*   **Data Module**:
    *   **Repositories Sync**: Manages the synchronization logic between local and remote data.
    *   **Room Persistence**: Handles local data storage.
    *   **Session Manager**: Maintains the global application state (active user and token).

### 2. Backend Services (Spring Boot)
*   **Spring Boot REST API**: Provides the business services and endpoints for the mobile app.
*   **JWT Security Module**: Ensures secure communication through token-based authentication.
*   **PostgreSQL**: Provides the long-term, relational persistence for all system data.

### Communication & Dependencies
*   The UI depends on ViewModels, which depend on Repositories.
*   Repositories communicate with the Backend API via HTTPS/REST, secured by JWT.
*   The local database (Room) is the primary source for the UI, ensuring resilience.

---

## Deployment Diagram
---
The deployment proposal describes the physical distribution of components and the communication protocols used between different environments.

### 1. User Environment (Mobile Device)
*   **Target Device**: Android Smartphone or Tablet running Android OS.
*   **Deployment Unit**: `NProjetoArtesanato.apk` (or AAB).
*   **Execution Runtime**: Android Runtime (ART) executing the Jetpack Compose binary.
*   **Local Storage**: A Room (SQLite) database instance running locally on the device's internal storage.

### 2. Cloud Infrastructure (Railway PaaS)
*   **Application Server**: The Spring Boot backend is deployed as a **Docker Container** on the Railway platform.
    *   **Runtime**: OpenJDK 21.
    *   **Scaling**: Can be horizontally scaled by increasing the number of container instances.
*   **Database Server**: A managed **PostgreSQL** instance provided by Railway.
    *   **Connectivity**: Accessed exclusively by the backend via an internal network for enhanced security.

### Justification & Decisions
*   **Organization**: We adopted a **Client-Server** model where the heavy lifting (persistence and business rules) is centralized in the cloud, while the interface logic and local data caching remain on the device.
*   **Environment Separation**: 
    *   **Client side** ensures a highly responsive UI and offline functionality.
    *   **Server side** ensures data consistency across multiple devices and centralized security management.
*   **Communication**: 
    *   **Public Network**: Uses **HTTPS (Port 443)** with TLS encryption and JWT authentication for all client-to-server calls.
    *   **Private Network**: The backend communicates with the database using the PostgreSQL protocol over an internal, non-public interface to minimize attack surface.
*   **Decision Rationale**: Railway was chosen for its native support for Docker and PostgreSQL, simplifying the CI/CD pipeline and deployment management.

---

## Entity-Relationship Diagram (ER)
---
The data modeling follows a relational structure optimized for the backend PostgreSQL database, reflecting the artisan's control over their products and transactions.

### 1. Entities & Attributes
*   **ARTESAO (Artisan)**: Central entity for authentication and ownership. Attributes include `nome`, `email`, `telefone`, and the encrypted `senha`.
*   **PRODUTO (Product)**: Items created by the artisan. Attributes include `nome`, `preco`, `quantidade_estoque`, and a unique `qr_code_id` (UUID) for identification.
*   **VENDA (Sale)**: Transactional record. Stores `dataHora`, `valor` (calculated at the time of sale), and `quantidade`.

### 2. Relationships & Cardinalities
*   **ARTESAO -- PRODUTO (1:N)**: An artisan can register multiple products, but each product is owned by a single artisan.
*   **ARTESAO -- VENDA (1:N)**: An artisan performs multiple sales. Since the system is per-user, the artisan acts as the seller.
*   **PRODUTO -- VENDA (1:N)**: A specific product can be sold multiple times throughout the system's history.

### 3. Data Integrity
*   Foreign keys (`artesao_id`, `produto_id`) ensure that all sales are linked to valid entities.
*   The `qr_code_id` is unique across the `PRODUTO` table to prevent identification conflicts.

---

## Application Flux (Updated)
---

### System Architecture Overview
The application follows a Clean Architecture approach with a hybrid data strategy:
*   **Local Persistence**: Uses Room Database (SQLite) as the Primary Source of Truth for the UI.
*   **Remote Synchronization**: Uses Retrofit to sync data with a backend API.
*   **Reactive UI**: ViewModels observe `StateFlow` from Repositories, which in turn observe Room DAOs.

---

### 1. Flux: Artesão Registration & Authentication

This flow describes how a user joins the platform and accesses their dashboard.

* **Actors/Objects**: User, Login/Cadastro UI, Login/Cadastro ViewModel, ArtesaoRepository, AuthService (Retrofit), ArtesaoDao (Room), SessionManager.

* **Step-by-Step Flux (Registration)**:
    i. User submits details via `CadastroArtesaoScreen`.
    ii. `ArtesaoRepository` calls `AuthService.signup(dto)`.
    iii. On success, the user is redirected to Login.

* **Step-by-Step Flux (Login)**:
    i. User enters credentials in `LoginScreen`.
    ii. `LoginViewModel` calls `ArtesaoRepository.loginRemote(email, password)`.
    iii. `ArtesaoRepository` receives a Token and Artisan data from the API.
    iv. `ArtesaoRepository` persists the Artisan locally via `ArtesaoDao.insert()`.
    v. `SessionManager` is updated with the token and artisan data.
    vi. UI navigates to `DashboardScreen`.

---

### 2. Flux: Dashboard & Data Synchronization

This flow ensures the artisan sees up-to-date metrics and information.

* **Actors/Objects**: DashboardScreen, DashboardViewModel, ProdutoRepository, VendaRepository, SessionManager, DAOs (Room).

* **Step-by-Step Flux**:
    i. `DashboardViewModel` initializes and calls `refreshDados()`.
    ii. `ProdutoRepository` fetches latest products from API and updates local DB (`deleteByArtesao` -> `insertAll`).
    iii. `VendaRepository` fetches latest sales history from API and updates local DB.
    iv. `DashboardViewModel` observes `observarPorArtesao()` and `observarTodas()` flows from Repositories.
    v. Room emits new data to Repositories, which emit to ViewModel.
    vi. The UI reacts to the updated `StateFlow` and displays summary cards and lists.

---

### 3. Flux: Product Management (CRUD & QR Code)

Covers creating, listing, and identifying products.

* **Actors/Objects**: User, Produto UI, ProdutoViewModel, ProdutoRepository, ProdutoApiService, ProdutoDao, QrCodeGenerator.

* **Step-by-Step Flux (Creation)**:
    i. User submits the product form.
    ii. `ProdutoRepository` sends `ProdutoCreateDTO` to the API.
    iii. Upon API success, `ProdutoRepository` saves the product to the local database.
    iv. The UI list updates automatically via Flow observation.

* **Step-by-Step Flux (QR Code)**:
    i. User selects a product to view its QR Code.
    ii. `ProdutoQrCodeScreen` fetches the product from the local DB via `ProdutoRepository`.
    iii. `QrCodeGenerator` uses the `qrCodeId` (UUID) to generate a Bitmap.
    iv. The QR Code is displayed for physical tagging.

---

### 4. Flux: Sales Registration (Offline-First Approach)

Describes how sales are recorded and stock is adjusted.

* **Actors/Objects**: User, RegistrarVendaScreen, VendaViewModel, VendaRepository, VendaApiService, ProdutoDao, VendaDao.

* **Step-by-Step Flux**:
    i. User selects a product and quantity in `RegistrarVendaScreen`.
    ii. `VendaViewModel` calls `registrarVendaRemote()`.
    iii. `VendaRepository` sends the request to the API.
    iv. On API success, `VendaRepository` performs two local operations:
        a. Updates local stock in `ProdutoDao`.
        b. Inserts the new sale record in `VendaDao`.
    v. All observing screens (Dashboard, Historico, Produtos) are updated instantly via Room Reactive Streams.

---

### 5. Flux: Profile Management & Session

Handles artisan identity and logout.

* **Actors/Objects**: ProfileScreen, ProfileViewModel, SessionManager, ArtesaoRepository.

* **Step-by-Step Flux**:
    i. User updates profile details.
    ii. `ProfileViewModel` calls `ArtesaoRepository.atualizar()`.
    iii. Repository updates Room and `SessionManager`.
    iv. On Logout, `SessionManager` clears the active artisan and token, navigating the user back to Login.