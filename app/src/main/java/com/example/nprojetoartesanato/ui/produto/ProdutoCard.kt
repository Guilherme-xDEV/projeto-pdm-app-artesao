package com.example.nprojetoartesanato.ui.produto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nprojetoartesanato.model.Produto

@Composable
fun ProdutoCard(
    produto: Produto,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = produto.nome,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = produto.descricao,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "R$ %.2f".format(produto.preco),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Estoque: ${produto.quantidadeEstoque}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}