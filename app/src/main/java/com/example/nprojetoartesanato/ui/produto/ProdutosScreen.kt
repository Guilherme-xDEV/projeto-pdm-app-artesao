package com.example.nprojetoartesanato.ui.produto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutosScreen(
    navController: NavController,
    viewModel: ProdutosViewModel = viewModel()
) {

    val produtos by viewModel.produtos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Meus Produtos")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            if (produtos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Você ainda não possui produtos cadastrados.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            navController.navigate(
                                "cadastro_produto"
                            )
                        }
                    ) {
                        Text("Cadastrar Produto")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {

                        Text(
                            text = "${produtos.size} produto(s) cadastrado(s)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                vertical = 16.dp
                            )
                        )
                    }

                    items(
                        items = produtos,
                        key = { produto ->
                            produto.id
                        }
                    ) { produto ->

                        ProdutoCard(
                            produto = produto
                        )
                    }
                }
            }
        }
    }
}