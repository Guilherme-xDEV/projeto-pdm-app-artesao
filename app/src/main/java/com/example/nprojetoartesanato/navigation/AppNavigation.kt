package com.example.nprojetoartesanato.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nprojetoartesanato.ui.cadastroArtesao.CadastroArtesaoScreen
import com.example.nprojetoartesanato.ui.dashboard.DashboardScreen
import com.example.nprojetoartesanato.ui.login.LoginScreen
import com.example.nprojetoartesanato.ui.produto.CadastroProdutoScreen
import com.example.nprojetoartesanato.ui.produto.ProdutosScreen
import com.example.nprojetoartesanato.ui.venda.HistoricoVendasScreen
import com.example.nprojetoartesanato.ui.venda.RegistrarVendaScreen

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object Dashboard : Screens("dashboard")
    object Produtos : Screens("produtos")
    object CadastroProduto : Screens("cadastro_produto")
    object RegistrarVenda : Screens("registrar_venda")
    object Historico : Screens("historico")
    object CadastroArtesao : Screens("cadastro_artesao")
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

        composable(Screens.Produtos.route) {
            ProdutosScreen(navController)
        }

        composable(Screens.CadastroProduto.route) {
            CadastroProdutoScreen(navController)
        }

        composable(Screens.RegistrarVenda.route) {
            RegistrarVendaScreen()
        }

        composable(Screens.Historico.route) {
            HistoricoVendasScreen()
        }

        composable(Screens.CadastroArtesao.route) {
            CadastroArtesaoScreen(navController)
        }
    }
}
