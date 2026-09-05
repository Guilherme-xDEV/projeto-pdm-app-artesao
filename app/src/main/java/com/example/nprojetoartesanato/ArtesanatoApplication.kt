package com.example.nprojetoartesanato

import android.app.Application
import com.example.nprojetoartesanato.data.local.AppDatabase
import com.example.nprojetoartesanato.data.session.SessionManager

class ArtesanatoApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
