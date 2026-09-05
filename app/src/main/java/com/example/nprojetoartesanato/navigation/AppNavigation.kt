package com.example.nprojetoartesanato.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nprojetoartesanato.ArtesanatoApplication
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.ui.cadastroArtesao.CadastroArtesaoScreen
import com.example.nprojetoartesanato.ui.common.ViewModelFactory
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
    val context = LocalContext.current
    val factory = ViewModelFactory(context.applicationContext as ArtesanatoApplication)

    val startDestination = remember {
        if (SessionManager.artesaoAtual.value != null) {
            Screens.Dashboard.route
        } else {
            Screens.Login.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screens.Login.route) {
            LoginScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.Dashboard.route) {
            DashboardScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.Produtos.route) {
            ProdutosScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.CadastroProduto.route) {
            CadastroProdutoScreen(navController, viewModel(factory = factory))
        }

        composable(
            route = Screens.ProdutoQrCode.route,
            arguments = listOf(navArgument("produtoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val produtoId = backStackEntry.arguments?.getLong("produtoId") ?: 0L
            ProdutoQrCodeScreen(navController, produtoId, viewModel(factory = factory))
        }

        composable(Screens.RegistrarVenda.route) {
            RegistrarVendaScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.Historico.route) {
            HistoricoVendasScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.CadastroArtesao.route) {
            CadastroArtesaoScreen(navController, viewModel(factory = factory))
        }

        composable(Screens.Perfil.route) {
            ProfileScreen(navController, viewModel(factory = factory))
        }
    }
}
