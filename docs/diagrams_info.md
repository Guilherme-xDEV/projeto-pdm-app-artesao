## Application Flux
---

### 1. Flux: Artesão Registration (Cadastro de Artesão)

This flow describes how a new artisan registers in the system.

* **Actors/Objects**: User, CadastroArtesaoScreen, CadastroArtesaoViewModel, ArtesaoRepository, LocalDataStore.

* **Step-by-Step Flux**:
    i. User accesses the registration screen from the LoginScreen.
    ii. User fills in personal and login details (Name, Phone, ID, Username, Password).
    iii. CadastroArtesaoViewModel validates the fields (none can be blank).
    iv. User clicks the "Cadastrar" button.
    v. CadastroArtesaoViewModel instantiates an `Artesao` object.
    vi. CadastroArtesaoViewModel calls `ArtesaoRepository.cadastrar(artesao)`.
    vii. `ArtesaoRepository` calls `LocalDataStore.adicionarArtesao(artesao)`.
    viii. `LocalDataStore` saves the artisan and assigns a unique ID.
    ix. User is navigated back to the LoginScreen.

---

### 2. Flux: Artesão Authentication (Login)

This flow describes how an artisan authenticates to access the system.

* **Actors/Objects**: User, LoginScreen (UI), LoginViewModel, ArtesaoRepository, SessionManager, LocalDataStore.

* **Step-by-Step Flux**:
    i. User enters username and password in LoginScreen.
    ii. LoginScreen updates state in LoginViewModel.
    iii. User clicks the "Login" button.
    iv. LoginViewModel calls `ArtesaoRepository.autenticar(user, password)`.
    v. `ArtesaoRepository` calls `LocalDataStore.buscarArtesao(user, password)`.
    vi. `LocalDataStore` filters the list and returns the matching `Artesao` (or null).
    vii. If successful, `ArtesaoRepository` updates `SessionManager.login(artesao)`.
    viii. `LoginViewModel` triggers navigation to `DashboardScreen`.

---

### 3. Flux: Product Registration (Cadastro de Produto)

This flow covers creating a new product associated with the logged-in artisan.

* **Actors/Objects**: User, CadastroProdutoScreen, CadastroProdutoViewModel, SessionManager, ProdutoRepository, LocalDataStore.

* **Step-by-Step Flux**:
    i. User fills out the product form in CadastroProdutoScreen.
    ii. User clicks "Cadastrar".
    iii. `CadastroProdutoViewModel` requests the current artisan from `SessionManager`.
    iv. `CadastroProdutoViewModel` generates a new UUID for the `qrCodeId`.
    v. `CadastroProdutoViewModel` instantiates a `Produto` object, mapping the `artesaoId` from the session.
    vi. `CadastroProdutoViewModel` calls `ProdutoRepository.cadastrar(produto)`.
    vii. `ProdutoRepository` calls `LocalDataStore.adicionarProduto(produto)`.
    viii. `LocalDataStore` assigns an ID and adds the product to the list, updating the `produtoList` StateFlow.
    ix. `CadastroProdutoViewModel` signals success and the UI returns to the Products list.

---

### 4. Flux: QR Code Viewing (Visualizar QR Code)

Describes how an artisan views and shares the QR Code for a specific product.

* **Actors/Objects**: User, ProdutosScreen, ProdutoQrCodeScreen, ProdutoRepository, QrCodeGenerator.

* **Step-by-Step Flux**:
    i. User selects a product from the list in `ProdutosScreen`.
    ii. Navigation passes the `produtoId` to `ProdutoQrCodeScreen`.
    iii. `ProdutoQrCodeScreen` fetches product details via `ProdutoRepository`.
    iv. `ProdutoQrCodeScreen` calls `QrCodeGenerator.generate(qrCodeId)`.
    v. The resulting Bitmap is displayed on the screen for physical tagging of the craft.

---

### 5. Flux: Sales Registration (Registrar Venda)

Describes the process of recording a sale and updating the inventory.

* **Actors/Objects**: User, RegistrarVendaScreen, VendaViewModel, VendaRepository, SessionManager, ProdutoRepository, LocalDataStore.

* **Step-by-Step Flux**:
    i. `RegistrarVendaScreen` displays the products available for sale.
    ii. User selects a product and confirms the sale.
    iii. `VendaViewModel` calls `vendaRepository.registrarVenda(produto, vendedorNome)`.
    iv. `VendaRepository` calls `LocalDataStore.baixarEstoqueProduto(produtoId)` to decrement inventory.
    v. `VendaRepository` creates a `Venda` object with timestamps and artisan details.
    vi. `VendaRepository` calls `LocalDataStore.adicionarVenda(venda)`.
    vii. `LocalDataStore` updates its internal lists and emits new states.
    viii. UI displays a confirmation and updates the dashboard counters.