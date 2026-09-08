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