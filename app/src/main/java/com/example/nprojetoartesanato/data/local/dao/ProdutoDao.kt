package com.example.nprojetoartesanato.data.local.dao

import androidx.room.*
import com.example.nprojetoartesanato.data.local.entities.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun getById(id: Long): ProdutoEntity?

    @Query("SELECT * FROM produtos WHERE artesaoId = :artesaoId")
    fun getByArtesao(artesaoId: Long): Flow<List<ProdutoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produto: ProdutoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(produtos: List<ProdutoEntity>)

    @Update
    suspend fun update(produto: ProdutoEntity)

    @Query("DELETE FROM produtos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM produtos WHERE artesaoId = :artesaoId")
    suspend fun deleteByArtesao(artesaoId: Long)

    @Query("SELECT * FROM produtos")
    fun getAll(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos")
    suspend fun getAllSync(): List<ProdutoEntity>
}
