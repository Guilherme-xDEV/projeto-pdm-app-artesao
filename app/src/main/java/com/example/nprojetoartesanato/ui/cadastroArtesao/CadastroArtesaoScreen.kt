package com.example.nprojetoartesanato.ui.cadastroArtesao

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nprojetoartesanato.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroArtesaoScreen(
    navController: NavController,
    viewModel: CadastroArtesaoViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Novo Cadastro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Crie sua conta no Centro de Artesanato",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nome
            OutlinedTextField(
                value = viewModel.nome,
                onValueChange = viewModel::atualizarNome,
                label = { Text("Nome Completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = viewModel.nomeErro != null,
                supportingText = {
                    viewModel.nomeErro?.let { Text(it) }
                }
            )

            // Telefone
            OutlinedTextField(
                value = viewModel.telefone,
                onValueChange = viewModel::atualizarTelefone,
                label = { Text("Telefone (apenas números)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.telefoneErro != null,
                supportingText = {
                    viewModel.telefoneErro?.let { Text(it) }
                }
            )

            // E-mail
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = viewModel::atualizarEmail,
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.emailErro != null,
                supportingText = {
                    viewModel.emailErro?.let { Text(it) }
                }
            )

            // Senha
            OutlinedTextField(
                value = viewModel.senha,
                onValueChange = viewModel::atualizarSenha,
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.senhaErro != null,
                supportingText = {
                    viewModel.senhaErro?.let { Text(it) }
                }
            )

            viewModel.erro?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Concluir Cadastro", fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Já tenho uma conta. Fazer Login")
            }
        }
    }
}
