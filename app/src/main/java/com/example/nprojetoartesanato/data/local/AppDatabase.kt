package com.example.nprojetoartesanato.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nprojetoartesanato.data.local.dao.ArtesaoDao
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.dao.VendaDao
import com.example.nprojetoartesanato.data.local.entities.ArtesaoEntity
import com.example.nprojetoartesanato.data.local.entities.ProdutoEntity
import com.example.nprojetoartesanato.data.local.entities.VendaEntity

@Database(
    entities = [ArtesaoEntity::class, ProdutoEntity::class, VendaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artesaoDao(): ArtesaoDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun vendaDao(): VendaDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE produtos ADD COLUMN firestoreId TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "artesanato_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
