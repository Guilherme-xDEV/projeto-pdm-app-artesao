### Application Flux (after product is registered)

Meus Produtos
↓
seleciona produto
↓
ProdutoQrCodeScreen
↓
produto.qrCodeId
↓
QrCodeGenerator
↓
Bitmap
↓
Compose

### How does Bitmap was implemented:

1. ZXing produces BitMatrix
2. QrCodeGenerator converts it into Bitmap
3. Compose convert Bitmap to ImageBitmap

## Complete Flux:
7

String
│
│ qrCodeId
▼
ZXing
│
▼
BitMatrix
│
▼
Bitmap
│
▼
ImageBitmap
│
▼
Compose Image