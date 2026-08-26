package com.example.nprojetoartesanato.ui.produto

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nprojetoartesanato.util.QrCodeUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CadastroProdutoScreen(
    navController: NavController,
    viewModel: CadastroProdutoViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    val writePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Cadastro de Produto")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Informações do Produto",
                style = MaterialTheme.typography.titleLarge
            )

            // Nome

            OutlinedTextField(
                value = viewModel.nome,
                onValueChange = viewModel::atualizarNome,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Nome do Produto")
                },
                singleLine = true
            )

            // Descrição

            OutlinedTextField(
                value = viewModel.descricao,
                onValueChange = viewModel::atualizarDescricao,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Descrição")
                },
                minLines = 3
            )

            // Preço

            OutlinedTextField(
                value = viewModel.preco,
                onValueChange = viewModel::atualizarPreco,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Preço (R$)")
                },
                singleLine = true
            )

            // Estoque

            OutlinedTextField(
                value = viewModel.quantidadeEstoque,
                onValueChange = viewModel::atualizarQuantidadeEstoque,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Quantidade em estoque")
                },
                singleLine = true
            )

            // Erro

            viewModel.erro?.let { mensagem ->

                Text(
                    text = mensagem,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider()

            // Cadastro

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.cadastrar()
                }
            ) {
                Text("Cadastrar Produto")
            }

            // Sucesso

            if (viewModel.cadastroRealizado) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Produto cadastrado com sucesso!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    viewModel.ultimoProdutoCadastrado?.let { (nome, qrCodeId) ->
                        val qrCodeBitmap = remember(qrCodeId) {
                            QrCodeUtils.gerarQrCodeBitmap(qrCodeId)
                        }

                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "QR Code do Produto",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp)
                        )

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || writePermissionState.status.isGranted) {
                                    val uri = QrCodeUtils.salvarBitmapNaGaleria(context, qrCodeBitmap, nome)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (uri != null) "QR Code salvo na galeria!"
                                            else "Erro ao salvar QR Code."
                                        )
                                    }
                                } else {
                                    writePermissionState.launchPermissionRequest()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("Salvar QR Code no Aparelho")
                        }
                    }
                }
            }

            // Voltar

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Voltar")
            }
        }
    }
}