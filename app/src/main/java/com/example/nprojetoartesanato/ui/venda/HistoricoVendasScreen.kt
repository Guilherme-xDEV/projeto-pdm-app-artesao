package com.example.nprojetoartesanato.ui.venda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nprojetoartesanato.model.Venda

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
                title = { Text("Histórico de Vendas") }
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

@Preview(showBackground = true)
@Composable
fun HistoricoPreview() {
    MaterialTheme {
        HistoricoVendasScreen()
    }
}
