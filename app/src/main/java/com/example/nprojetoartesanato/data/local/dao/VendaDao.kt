package com.example.nprojetoartesanato.data.local.dao

import androidx.room.*
import com.example.nprojetoartesanato.data.local.entities.VendaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendaDao {
    @Query("SELECT * FROM vendas ORDER BY dataHora DESC")
    fun getAll(): Flow<List<VendaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(venda: VendaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vendas: List<VendaEntity>)

    @Query("DELETE FROM vendas")
    suspend fun deleteAll()

    @Query("SELECT * FROM vendas WHERE artesaoId = :artesaoId ORDER BY dataHora DESC")
    fun getByArtesao(artesaoId: Long): Flow<List<VendaEntity>>
}
