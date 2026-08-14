package com.example.nprojetoartesanato.ui.produto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProdutoScreen(
    navController: NavController,
    viewModel: CadastroProdutoViewModel = viewModel()
) {

    Scaffold(
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

                Text(
                    text = "Produto cadastrado com sucesso!",
                    style = MaterialTheme.typography.titleMedium
                )
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