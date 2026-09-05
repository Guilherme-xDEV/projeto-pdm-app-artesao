package com.example.nprojetoartesanato.ui.cadastroArtesao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.nprojetoartesanato.navigation.Screens

@Composable
fun CadastroArtesaoScreen(
    navController: NavController,
    viewModel: CadastroArtesaoViewModel = viewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Cadastro de Artesão")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.nome,
            onValueChange = viewModel::atualizarNome,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.telefone,
            onValueChange = viewModel::atualizarTelefone,
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.identificacao,
            onValueChange = viewModel::atualizarIdentificacao,
            label = { Text("Identificação") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.email,
            onValueChange = viewModel::atualizarEmail,
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.senha,
            onValueChange = viewModel::atualizarSenha,
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        viewModel.erro?.let {
            Text(
                text = it
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                viewModel.cadastrar {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.CadastroArtesao.route) {
                            inclusive = true
                        }
                    }
                }
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Cadastrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("voltar")
        }
    }
}