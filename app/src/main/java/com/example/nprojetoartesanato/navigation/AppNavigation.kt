package com.example.nprojetoartesanato.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nprojetoartesanato.ui.cadastroArtesao.CadastroArtesaoScreen
import com.example.nprojetoartesanato.ui.dashboard.DashboardScreen
import com.example.nprojetoartesanato.ui.login.LoginScreen
import com.example.nprojetoartesanato.ui.perfil.ProfileScreen
import com.example.nprojetoartesanato.ui.produto.CadastroProdutoScreen
import com.example.nprojetoartesanato.ui.produto.ProdutoQrCodeScreen
import com.example.nprojetoartesanato.ui.produto.ProdutosScreen
import com.example.nprojetoartesanato.ui.venda.HistoricoVendasScreen
import com.example.nprojetoartesanato.ui.venda.RegistrarVendaScreen

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object Dashboard : Screens("dashboard")
    object Produtos : Screens("produtos")
    object CadastroProduto : Screens("cadastro_produto")
    object ProdutoQrCode : Screens("produto_qrcode/{produtoId}") {
        fun createRoute(produtoId: Long) = "produto_qrcode/$produtoId"
    }
    object RegistrarVenda : Screens("registrar_venda")
    object Historico : Screens("historico")
    object CadastroArtesao : Screens("cadastro_artesao")
    object Perfil : Screens("perfil")
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

        composable(
            route = Screens.ProdutoQrCode.route,
            arguments = listOf(navArgument("produtoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val produtoId = backStackEntry.arguments?.getLong("produtoId") ?: 0L
            ProdutoQrCodeScreen(navController, produtoId)
        }

        composable(Screens.RegistrarVenda.route) {
            RegistrarVendaScreen(navController)
        }

        composable(Screens.Historico.route) {
            HistoricoVendasScreen()
        }

        composable(Screens.CadastroArtesao.route) {
            CadastroArtesaoScreen(navController)
        }

        composable(Screens.Perfil.route) {
            ProfileScreen(navController)
        }
    }
}
