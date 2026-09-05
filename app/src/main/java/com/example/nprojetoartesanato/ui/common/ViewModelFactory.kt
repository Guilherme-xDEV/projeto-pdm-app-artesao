package com.example.nprojetoartesanato.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nprojetoartesanato.ArtesanatoApplication
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.repository.VendaRepository
import com.example.nprojetoartesanato.ui.cadastroArtesao.CadastroArtesaoViewModel
import com.example.nprojetoartesanato.ui.dashboard.DashboardViewModel
import com.example.nprojetoartesanato.ui.login.LoginViewModel
import com.example.nprojetoartesanato.ui.perfil.ProfileViewModel
import com.example.nprojetoartesanato.ui.produto.CadastroProdutoViewModel
import com.example.nprojetoartesanato.ui.produto.ProdutoQrCodeViewModel
import com.example.nprojetoartesanato.ui.produto.ProdutosViewModel
import com.example.nprojetoartesanato.ui.venda.VendaViewModel

class ViewModelFactory(private val application: ArtesanatoApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = application.database
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(
                    ProdutoRepository(db.produtoDao()),
                    VendaRepository(db.vendaDao(), db.produtoDao())
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(ArtesaoRepository(db.artesaoDao())) as T
            }
            modelClass.isAssignableFrom(ProdutosViewModel::class.java) -> {
                ProdutosViewModel(ProdutoRepository(db.produtoDao())) as T
            }
            modelClass.isAssignableFrom(VendaViewModel::class.java) -> {
                VendaViewModel(
                    VendaRepository(db.vendaDao(), db.produtoDao()),
                    ProdutoRepository(db.produtoDao())
                ) as T
            }
            modelClass.isAssignableFrom(CadastroProdutoViewModel::class.java) -> {
                CadastroProdutoViewModel(ProdutoRepository(db.produtoDao())) as T
            }
            modelClass.isAssignableFrom(CadastroArtesaoViewModel::class.java) -> {
                CadastroArtesaoViewModel(ArtesaoRepository(db.artesaoDao())) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(ArtesaoRepository(db.artesaoDao())) as T
            }
            modelClass.isAssignableFrom(ProdutoQrCodeViewModel::class.java) -> {
                val db = application.database
                ProdutoQrCodeViewModel(ProdutoRepository(db.produtoDao())) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
