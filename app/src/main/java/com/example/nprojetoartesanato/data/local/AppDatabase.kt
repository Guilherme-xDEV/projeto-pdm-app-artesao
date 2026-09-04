package com.example.nprojetoartesanato.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nprojetoartesanato.data.local.dao.ArtesaoDao
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.dao.VendaDao
import com.example.nprojetoartesanato.data.local.entities.ArtesaoEntity
import com.example.nprojetoartesanato.data.local.entities.ProdutoEntity
import com.example.nprojetoartesanato.data.local.entities.VendaEntity

@Database(
    entities = [ArtesaoEntity::class, ProdutoEntity::class, VendaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artesaoDao(): ArtesaoDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun vendaDao(): VendaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "artesanato_database"
                )
                .fallbackToDestructiveMigration() // Simplified for development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
