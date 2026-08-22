package com.example.nprojetoartesanato.ui.produto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.navigation.Screens

import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutosScreen(
    navController: NavController,
    viewModel: ProdutosViewModel = viewModel()
) {
    val produtos by viewModel.produtos.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // State to control dialogs
    var produtoParaEditar by remember { mutableStateOf<Produto?>(null) }
    var produtoParaDeletar by remember { mutableStateOf<Produto?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meus Produtos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.CadastroProduto.route)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Produto")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (produtos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum produto encontrado",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Comece adicionando seu primeiro artesanato!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 80.dp,
                        top = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${produtos.size} produto(s) no total",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = produtos,
                        key = { it.id }
                    ) { produto ->
                        ProdutoCard(
                            produto = produto,
                            onQrCodeClick = {
                                navController.navigate(Screens.ProdutoQrCode.createRoute(produto.id))
                            },
                            onEditClick = {
                                produtoParaEditar = produto
                            },
                            onDeleteClick = {
                                produtoParaDeletar = produto
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog Modal to update Product DTO
    produtoParaEditar?.let { produto ->
        EditarProdutoDialog(
            produto = produto,
            onDismiss = { produtoParaEditar = null },
            onConfirm = { nome, descricao, preco ->
                viewModel.atualizarProduto(produto.id, nome, descricao, preco)
                produtoParaEditar = null
            }
        )
    }

    // Exclusion Confirmation Modal
    produtoParaDeletar?.let { produto ->
        AlertDialog(
            onDismissRequest = { produtoParaDeletar = null },
            title = { Text(text = "Excluir Produto") },
            text = {
                Text(text = "Tem certeza que deseja excluir '${produto.nome}'? Esta ação não poderá ser desfeita.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletarProduto(produto.id)
                        produtoParaDeletar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error // red button!
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { produtoParaDeletar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EditarProdutoDialog(
    produto: Produto,
    onDismiss: () -> Unit,
    onConfirm: (nome: String, descricao: String, preco: Double) -> Unit
) {
    var nome by remember { mutableStateOf(produto.nome) }
    var descricao by remember { mutableStateOf(produto.descricao) }
    var precoText by remember { mutableStateOf(produto.preco.toString()) }

    val nomeErro = nome.isBlank()
    val precoDouble = precoText.replace(",", ".").toDoubleOrNull()
    val precoErro = precoDouble == null || precoDouble <= 0.0

    val formValido = !nomeErro && !precoErro

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Produto") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome *") },
                    isError = nomeErro,
                    supportingText = {
                        if (nomeErro) {
                            Text(
                                text = "O nome não pode ficar em branco",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = precoText,
                    onValueChange = { precoText = it },
                    label = { Text("Preço (R$) *") },
                    isError = precoErro,
                    supportingText = {
                        if (precoErro) {
                            Text(
                                text = if (precoDouble == null) "Informe um valor numérico válido" else "O preço deve ser maior que zero",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = formValido, // Desabilita o botão se houver erros de validação
                onClick = {
                    if (formValido && precoDouble != null) {
                        onConfirm(nome.trim(), descricao.trim(), precoDouble)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}