package com.example.netflix.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfis")
data class Perfil(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String
)
