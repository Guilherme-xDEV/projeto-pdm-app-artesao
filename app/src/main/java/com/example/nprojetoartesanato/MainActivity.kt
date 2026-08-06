package com.example.nprojetoartesanato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nprojetoartesanato.ui.theme.NProjetoArtesanatoTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NProjetoArtesanatoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object Dashboard : Screens("dashboard")
    object CadastroProduto : Screens("cadastro_produto")
    object RegistrarVenda : Screens("registrar_venda")
    object Historico : Screens("historico")
}

@Composable
fun LoginScreen(
    navController: NavController
) {

    var usuario by remember {
        mutableStateOf("")
    }

    var senha by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Centro de Artesanato",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Sistema de Controle de Vendas",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Usuário")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Senha")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(
                            Screens.Dashboard.route
                        )
                    }
                ) {
                    Text("Entrar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Centro de Artesanato")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Olá, Maria!",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "Bem-vinda ao sistema",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Produtos")
                        Text(
                            text = "42",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Vendas")
                        Text(
                            text = "127",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            Text(
                text = "Ações rápidas",
                style = MaterialTheme.typography.titleMedium
            )

            DashboardButton(
                title = "Cadastrar Produto",
                icon = Icons.Default.AddBox
            ) {
                navController.navigate(Screens.CadastroProduto.route)
            }

            DashboardButton(
                title = "Registrar Venda",
                icon = Icons.Default.QrCodeScanner
            ) {
                navController.navigate(Screens.RegistrarVenda.route)
            }

            DashboardButton(
                title = "Histórico de Vendas",
                icon = Icons.Default.History
            ) {
                navController.navigate(Screens.Historico.route)
            }

            Text(
                text = "Centro de Artesanato de Tauá",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DashboardButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProdutoScreen() {

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    val artesaos = listOf(
        "Maria Silva",
        "Ana Souza",
        "João Pereira"
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    var artesaoSelecionado by remember {
        mutableStateOf(artesaos.first())
    }

    var produtoCadastrado by remember {
        mutableStateOf(false)
    }

    var qrGerado by remember {
        mutableStateOf(false)
    }

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

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Nome do Produto")
                }
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Descrição")
                }
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Valor (R$)")
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {

                OutlinedTextField(
                    value = artesaoSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = {
                        Text("Artesão Responsável")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    artesaos.forEach { artesao ->

                        DropdownMenuItem(
                            text = {
                                Text(artesao)
                            },
                            onClick = {
                                artesaoSelecionado = artesao
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    produtoCadastrado = true
                }
            ) {
                Text("Cadastrar Produto")
            }

            if (produtoCadastrado) {

                HorizontalDivider()

                Text(
                    text = "Produto Cadastrado",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text("Produto: $nome")
                        Text("Descrição: $descricao")
                        Text("Valor: R$ $valor")
                        Text("Artesão: $artesaoSelecionado")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        qrGerado = true
                    }
                ) {
                    Text("Gerar QR Code")
                }
            }

            if (qrGerado) {

                Text(
                    text = "QR Code Gerado",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }

                Text(
                    text = "Código associado ao produto cadastrado.",
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class Venda(
    val produto: String,
    val artesao: String,
    val vendedor: String,
    val valor: String,
    val dataHora: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoVendasScreen() {

    val vendas = listOf(
        Venda(
            produto = "Boneca de Crochê",
            artesao = "Maria Silva",
            vendedor = "João",
            valor = "R$ 45,00",
            dataHora = "05/06/2026 14:30"
        ),
        Venda(
            produto = "Rede Artesanal",
            artesao = "Ana Souza",
            vendedor = "Maria",
            valor = "R$ 120,00",
            dataHora = "05/06/2026 15:05"
        ),
        Venda(
            produto = "Bolsa de Palha",
            artesao = "Carlos",
            vendedor = "João",
            valor = "R$ 80,00",
            dataHora = "06/06/2026 10:15"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Histórico de Vendas")
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Resumo",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Total de vendas: ${vendas.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            items(vendas) { venda ->

                VendaCard(venda)
            }
        }
    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {

        composable(Screens.Login.route) {
            LoginScreen(navController)
        }

        composable(Screens.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(Screens.CadastroProduto.route) {
            CadastroProdutoScreen()
        }

        composable(Screens.RegistrarVenda.route) {
            RegistrarVendaScreen()
        }

        composable(Screens.Historico.route) {
            HistoricoVendasScreen()
        }
    }
}

@Composable
fun VendaCard(
    venda: Venda
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = venda.produto,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Artesão: ${venda.artesao}"
            )

            Text(
                text = "Vendedor: ${venda.vendedor}"
            )

            Text(
                text = venda.valor,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = venda.dataHora,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoricoPreview() {

    MaterialTheme {
        HistoricoVendasScreen()
    }
}

//@Composable
//fun CadastroProdutoScreen() {
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text("Cadastro de Produto")
//    }
//}

@Composable
fun RegistrarVendaScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Registrar Venda")
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {

    MaterialTheme {

        LoginScreen(
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {

    MaterialTheme {

        DashboardScreen(
            navController = rememberNavController()
        )
    }
}