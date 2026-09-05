package com.example.nprojetoartesanato.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nprojetoartesanato.navigation.Screens
import com.example.nprojetoartesanato.ui.theme.NProjetoArtesanatoTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(),
) {
    val artesao by viewModel.artesaoAtual.collectAsState()
    val produtos by viewModel.produtos.collectAsState()
    val vendas by viewModel.vendas.collectAsState()
    val isSalesVisible by viewModel.isSalesVisible.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }

    // Cálculo do total de vendas
    val totalVendas = vendas.sumOf { 
        (it.valor.replace("R$", "").replace(",", ".").trim().toDoubleOrNull() ?: 0.0) 
    }

    if (showHelpDialog) {
        HelpDialog(onDismiss = { showHelpDialog = false })
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Parte Superior (Cabeçalho Estilo Nubank)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Bar: Avatar e Ícones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Clicável (Esquerda)
                    Box(
                        modifier = Modifier
                            .size(56.dp) // Aumentado um pouco para visibilidade
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { navController.navigate(Screens.Perfil.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Ações (Direita)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { viewModel.toggleSalesVisibility() }) {
                            Icon(
                                imageVector = if (isSalesVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isSalesVisible) "Ocultar Saldo" else "Mostrar Saldo",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { showHelpDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Ajuda", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Saudação
                Text(
                    text = "Olá, ${artesao?.nome ?: "Artesão"}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Saldo" ou Valor em Vendas
                Column {
                    Text(
                        text = "Vendas Realizadas",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = if (isSalesVisible) "R$ ${String.format(Locale.ROOT, "%.2f", totalVendas)}" else "R$ •••••",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botões Rápidos (Pílulas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionPill(
                        label = "Nova Venda",
                        icon = Icons.Default.QrCodeScanner,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screens.RegistrarVenda.route) }
                    )
                    QuickActionPill(
                        label = "Novo Produto",
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screens.CadastroProduto.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Parte Inferior (Grade de Serviços)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Serviços e Gestão",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Grid de 2x2 ou 2 colunas
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ServiceItem(
                                title = "Meus Produtos",
                                icon = Icons.Default.Inventory,
                                count = produtos.size.toString(),
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(Screens.Produtos.route) }
                            )
                            ServiceItem(
                                title = "Histórico",
                                icon = Icons.Default.History,
                                count = vendas.size.toString(),
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(Screens.Historico.route) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Centro de Artesanato de Tauá\nArgila Vermelha • Artesanato Vivo",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionPill(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ServiceItem(
    title: String,
    icon: ImageVector,
    count: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "$count itens",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guia do Dashboard", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HelpItem("Avatar (Topo Esquerdo)", "Acesse seu perfil para editar dados ou sair da conta.")
                HelpItem("Olho (Topo Direito)", "Clique para ocultar ou mostrar o valor total das suas vendas.")
                HelpItem("Nova Venda", "Abra a câmera para escanear o QR Code de um produto e realizar uma venda.")
                HelpItem("Novo Produto", "Cadastre novos itens no seu estoque.")
                HelpItem("Serviços", "Veja a lista completa de produtos e o histórico detalhado de vendas.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendi", color = MaterialTheme.colorScheme.primary)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun HelpItem(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    NProjetoArtesanatoTheme(dynamicColor = false) {
        DashboardScreen(
            navController = rememberNavController()
        )
    }
}
