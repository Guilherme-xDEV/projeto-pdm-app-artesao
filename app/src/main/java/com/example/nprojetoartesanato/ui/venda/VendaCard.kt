package com.example.nprojetoartesanato.ui.venda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nprojetoartesanato.model.Venda

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
                text = "Quantidade: ${venda.quantidade}"
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
