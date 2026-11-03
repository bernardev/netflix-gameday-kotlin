package com.example.netflix

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filmes")
data class Filme(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val baixado: Boolean
)
