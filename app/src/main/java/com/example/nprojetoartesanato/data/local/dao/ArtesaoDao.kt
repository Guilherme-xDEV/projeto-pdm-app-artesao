package com.example.nprojetoartesanato.data.local.dao

import androidx.room.*
import com.example.nprojetoartesanato.data.local.entities.ArtesaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtesaoDao {
    @Query("SELECT * FROM artesaos WHERE id = :id")
    suspend fun getById(id: Long): ArtesaoEntity?

    @Query("SELECT * FROM artesaos WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): ArtesaoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artesao: ArtesaoEntity)

    @Update
    suspend fun update(artesao: ArtesaoEntity)

    @Query("DELETE FROM artesaos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM artesaos")
    fun getAll(): Flow<List<ArtesaoEntity>>
}
