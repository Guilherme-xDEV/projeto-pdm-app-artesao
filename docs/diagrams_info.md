## Application Flux
---

### 1. Flux: Artesão Authentication (Login)

This flow describes how a user authenticates using their credentials.

* Actors/Objects: User, LoginScreen (UI), LoginViewModel, ArtesaoRepository, LocalDataStore, SessionManager.

* Step-by-Step Flux:
```
i. User enters username and password in LoginScreen.
ii. LoginScreen updates state in LoginViewModel (via onUsuarioChange / onSenhaChange).
iii. User clicks the "Login" button.
iv. LoginScreen calls LoginViewModel.login().
v. LoginViewModel checks for blank fields.
vi. LoginViewModel calls ArtesaoRepository.autenticar(user, password).
vii. ArtesaoRepository calls LocalDataStore.buscarArtesao(user, password).
viii. LocalDataStore filters its internal artesaoList and returns the Artesao object (or null).
```

---

### 2. Flux: Product Registration (Cadastro de Produto)

This flow covers creating a new product associated with the logged-in artisan.

* Actors/Objects: User, CadastroProdutoScreen, CadastroProdutoViewModel, SessionManager, ProdutoRepository, LocalDataStore.

* Step-by-Step Flux:
```
i.User fills out the product form in CadastroProdutoScreen.
ii.User clicks "Cadastrar".
iii.CadastroProdutoScreen calls CadastroProdutoViewModel.cadastrar().
iv.CadastroProdutoViewModel requests the current artisan from SessionManager (artesaoAtual.value).
v.CadastroProdutoViewModel performs data validation (checks for empty fields and valid number formats).
vi.CadastroProdutoViewModel generates a new UUID for the qrCodeId.
vii.CadastroProdutoViewModel instantiates a Produto object, mapping the artesaoId from the session.
viii.CadastroProdutoViewModel calls ProdutoRepository.cadastrar(produto).
ix.ProdutoRepository calls LocalDataStore.adicionarProduto(produto).
x.LocalDataStore assigns an auto-incremented ID and adds the product to the list.
xi.LocalDataStore updates the produtoList StateFlow, which notifies any observing UIs.
xii.CadastroProdutoViewModel clears the UI fields and signals success to the Screen.
```

---

### 3. Flux: Sales Registration (Registrar Venda - Projected)
Based on the Venda model and the existing architecture pattern, this is the projected flow for the sales functionality you are currently building.

* Actors/Objects: User, RegistrarVendaScreen, VendaViewModel, SessionManager, ProdutoRepository, VendaRepository (to be implemented), LocalDataStore.

* Step-by-Step Flux:

```
i.RegistrarVendaScreen requests the list of products for the current artisan via VendaViewModel.
ii.VendaViewModel calls ProdutoRepository.buscarProdutosDoArtesao(artesaoId).
iii.User selects a product and enters sales details.
iv.User confirms the sale.
v.VendaViewModel creates a Venda object containing product details and timestamps.
vi.VendaViewModel calls VendaRepository to record the transaction.
vii.VendaRepository updates LocalDataStore to:
    ▪Decrement the quantidadeEstoque of the specific Produto.
    ▪Save the Venda record.
viii.LocalDataStore emits updated states.
ix.RegistrarVendaScreen displays a confirmation message.
```