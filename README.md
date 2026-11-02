# Artesão Digital - Android App 🎨📱

Modern Android client application for **NProjetoArtesanato**, designed to empower local artisans to manage their products, sales, and profiles seamlessly, with offline capabilities and integration with a Spring Boot backend REST API.

---

## 🌐 Try It Online (No Installation Required)

Non-technical reviewer or evaluator? You can test the app instantly right inside your web browser without installing anything or setting up Android Studio:

👉 **[Launch Artesão Digital Online on Appetize.io](https://appetize.io/app/b_mi5hsa5rwpm76sab5svamvadyi)**

---

## 🚀 Technologies & Libraries

Built with modern Android development best practices and Jetpack libraries:

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with **Material 3**
- **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) (with KSP)
- **Backend / Cloud Data:** [Firebase Authentication](https://firebase.google.com/docs/auth) and [Cloud Firestore](https://firebase.google.com/docs/firestore)
- **QR Code & Scanning:** 
  - [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
  - [CameraX](https://developer.android.com/training/camerax) (Lifecycle, Camera2, View)
  - [ZXing Core](https://github.com/zxing/zxing) for QR code generation
- **Permissions:** [Accompanist Permissions](https://google.github.io/accompanist/permissions/)
- **Concurrency:** Kotlin Coroutines & Flow

---

## 📂 Project Structure

```tree
com.example.nprojetoartesanato/
│
├── data/
│   ├── local/          # Room database entities, DAOs, and AppDatabase
│   ├── network/        # Retrofit API service interfaces, client, and DTOs
│   ├── repository/     # Data repositories bridging local & remote sources
│   └── session/        # Session management and authentication tokens
│
├── model/              # Domain models and request/response DTOs
│
├── navigation/         # Compose navigation graphs and routes
│
├── ui/
│   ├── cadastroArtesao/# Artisan registration screen & ViewModel
│   ├── common/         # Common UI components & ViewModel factories
│   ├── dashboard/      # Main dashboard screen & ViewModel
│   ├── login/          # Authentication & Login screen & ViewModel
│   ├── perfil/         # Artisan profile screen & ViewModel
│   ├── produto/        # Product listing, creation, QR Code generation & scanning
│   ├── theme/          # Material 3 colors, typography, and theme
│   └── venda/          # Sales registration, history, and cards
│
└── util/               # QR Code generators and utility helpers
```

---

## ✨ Key Features

- **🔐 Authentication & Session:** Secure user login and persistent session state handling.
- **🎨 Artisan Management:** Profile creation, viewing, and updating artisan details.
- **📦 Product Management:** 
  - Register, edit, and list handmade products.
  - Generate and display QR Codes for products.
  - Scan product QR codes using CameraX and ML Kit.
- **💰 Sales & Transactions:**
  - Register new sales linked with products and artisans.
  - Track complete sales history with detailed cards.
- **🔄 Hybrid Sync / Data Storage:** Local caching powered by Room, synchronized with the shared Firebase Cloud Firestore project used by the web application.

---

## ⚙️ Prerequisites & Setup (For Developers)

1. **Android Studio:** Jellyfish / Koala or newer recommended.
2. **SDK Requirements:**
   - **Min SDK:** 24 (Android 7.0+)
   - **Compile/Target SDK:** 36 (Android 15+)
3. **Firebase:** Ensure `app/google-services.json` belongs to the same Firebase project used by Artesão Digital Web, and enable Email/Password authentication and Cloud Firestore.

---

## 🏃‍♂️ Getting Started (Local Development)

1. Clone the repository:
   ```bash
   git clone https://github.com/Guilherme-xDEV/projeto-pdm-app-artesao
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync dependencies.
4. Run the app on an Android emulator or a physical device with Camera permissions enabled.

---

## Shared Firestore product contract

The Android and web clients share the same Firestore collections.

- `artesaos/{uid}`: `uid` and document ID match Firebase Authentication. The numeric `id` is retained as a local/legacy compatibility identifier.
- `produtos/{documentId}`: `id` is a deterministic numeric compatibility key used by the Room/UI layer, `firestoreId` is the authoritative Firestore document ID, `artesaoId` is the Firebase Auth UID, `qrCodeId` is the scanned QR payload, `precoCentavos` is an integer, `quantidadeEstoque` is an integer, and `ativo` is boolean.
- `vendas/{id}`: canonical sales use `produtoId`, UID-based artisan and seller identifiers, `precoUnitarioCentavos`, `totalCentavos`, and a Firestore Timestamp in `dataHora`.

New product documents use their QR UUID as their document ID. Older products can have a separate QR payload and document ID; migrations preserve both values so printed labels continue to work.

The local Room database adds `firestoreId` through a version 1-to-2 migration. When the artisan opens the product list, the repository loads canonical UID-owned records and legacy numeric-ID records, then migrates the latter in place. Updated Firestore Security Rules from the web repository must be deployed before releasing this Android version.
