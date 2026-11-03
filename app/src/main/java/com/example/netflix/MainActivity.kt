package com.example.netflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.netflix.data.AppDatabase
import com.example.netflix.data.repository.FilmeRepository
import com.example.netflix.data.repository.PerfilRepository
import com.example.netflix.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val perfilRepository = PerfilRepository(db.perfilDAO())
        val filmeRepository = FilmeRepository(db.filmeDAO())

        setContent {
            MaterialTheme {
                // Ponto de entrada da navegacao
                AppNavigation(perfilRepository, filmeRepository)
            }
        }
    }
}
