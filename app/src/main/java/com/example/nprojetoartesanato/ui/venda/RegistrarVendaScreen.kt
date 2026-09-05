package com.example.nprojetoartesanato.ui.venda

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.model.Produto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RegistrarVendaScreen(
    navController: NavController,
    vendaViewModel: VendaViewModel = viewModel()
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var produtoEncontrado by remember { mutableStateOf<Produto?>(null) }
    var quantidadeSelecionada by remember { mutableIntStateOf(1) }
    var showManualSearch by remember { mutableStateOf(false) }
    var filtroTexto by remember { mutableStateOf("") }
    val produtosDisponiveis by vendaViewModel.produtosDisponiveis.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    
    // Impede múltiplas leituras simultâneas do mesmo frame; é resetada
    // manualmente (ao cancelar o diálogo) ou automaticamente após uma
    // leitura que não encontrou produto, para permitir uma nova tentativa.
    var leituraEmProcessamento by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Venda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cameraPermissionState.status.isGranted && produtoEncontrado == null) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onQrCodeDetected = { conteudo ->
                        if (!leituraEmProcessamento) {
                            leituraEmProcessamento = true

                            coroutineScope.launch {
                                // Produto.qrCodeId é o campo que o QR Code efetivamente carrega
                                val produto = vendaViewModel.buscarPorQrCode(conteudo)

                                if (produto != null) {
                                    produtoEncontrado = produto
                                    // leituraEmProcessamento continua true enquanto o diálogo
                                    // estiver aberto, para não reabrir o diálogo em cima dele mesmo
                                } else {
                                    snackbarHostState.showSnackbar(
                                        "QR Code não corresponde a nenhum produto cadastrado."
                                    )
                                    delay(1000) // evita reabrir o snackbar imediatamente com o mesmo código em quadro
                                    leituraEmProcessamento = false
                                }
                            }
                        }
                    }
                )

                // Quadro de mira
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .align(Alignment.Center)
                        .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )

                Text(
                    text = "Posicione o QR Code no quadro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                )

                // Botão para busca manual
                Button(
                    onClick = { showManualSearch = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Busca manual",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

            } else if (!cameraPermissionState.status.isGranted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Conceda permissão para usar a câmera.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Permitir Câmera")
                    }
                }
            }

            // Diálogo de confirmação exibido assim que um QR Code corresponde a um produto
            produtoEncontrado?.let { produto ->
                AlertDialog(
                    onDismissRequest = {
                        produtoEncontrado = null
                        leituraEmProcessamento = false
                        quantidadeSelecionada = 1
                    },
                    title = {
                        Text(
                            text = "Produto encontrado",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Nome: ${produto.nome}")
                            Text(text = "Preço Unitário: R$ %.2f".format(produto.preco))
                            Text(text = "Estoque: ${produto.quantidadeEstoque}")
                            Spacer(modifier = Modifier.height(8.dp))

                            if (produto.quantidadeEstoque <= 0) {
                                Text(
                                    text = "Produto sem estoque disponível.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(text = "Selecione a quantidade:")
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Slider(
                                        value = quantidadeSelecionada.toFloat(),
                                        onValueChange = { quantidadeSelecionada = it.toInt() },
                                        valueRange = 1f..produto.quantidadeEstoque.toFloat(),
                                        steps = if (produto.quantidadeEstoque > 1) produto.quantidadeEstoque - 2 else 0,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = quantidadeSelecionada.toString(),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                Text(
                                    text = "Total: R$ %.2f".format(produto.preco * quantidadeSelecionada),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            enabled = produto.quantidadeEstoque > 0,
                            onClick = {
                                coroutineScope.launch {
                                    val sucesso = vendaViewModel.registrarVenda(produto, quantidadeSelecionada)
                                    produtoEncontrado = null
                                    leituraEmProcessamento = false
                                    val qtdVendida = quantidadeSelecionada
                                    quantidadeSelecionada = 1
                                    snackbarHostState.showSnackbar(
                                        if (sucesso) "Venda de $qtdVendida unidades de \"${produto.nome}\" registrada!"
                                        else "Não foi possível registrar a venda. Tente novamente."
                                    )
                                }
                            }
                        ) {
                            Text("Registrar Venda")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                produtoEncontrado = null
                                leituraEmProcessamento = false
                                quantidadeSelecionada = 1
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Bottom Sheet para busca manual de produtos
            if (showManualSearch) {
                ModalBottomSheet(
                    onDismissRequest = { showManualSearch = false },
                    sheetState = bottomSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "Selecionar Produto",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = filtroTexto,
                            onValueChange = { filtroTexto = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Digite o nome do produto...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val produtosFiltrados = produtosDisponiveis.filter {
                            it.nome.contains(filtroTexto, ignoreCase = true)
                        }

                        if (produtosFiltrados.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum produto encontrado.")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 400.dp)
                            ) {
                                items(produtosFiltrados) { produto ->
                                    ListItem(
                                        headlineContent = { Text(produto.nome) },
                                        supportingContent = { Text("Estoque: ${produto.quantidadeEstoque}") },
                                        trailingContent = { Text("R$ %.2f".format(produto.preco)) },
                                        modifier = Modifier.clickable {
                                            produtoEncontrado = produto
                                            showManualSearch = false
                                            filtroTexto = ""
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onQrCodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888) // Força o formato correto
                    .build()

                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    QrCodeAnalyzer { barcode ->
                        onQrCodeDetected(barcode)
                    }
                )

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

private class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // Garante que a rotação seja passada corretamente do ImageProxy para o InputImage
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    Log.d("QrCodeAnalyzer", "Frame processado, ${barcodes.size} código(s) encontrado(s)")
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            Log.d("QrCodeAnalyzer", "QR lido: $value")
                            onQrCodeScanned(value)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QrCodeAnalyzer", "Falha ao processar imagem", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            Log.w("QrCodeAnalyzer", "mediaImage nulo, frame ignorado")
            imageProxy.close()
        }
    }
}