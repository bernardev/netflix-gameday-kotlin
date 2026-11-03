package com.example.netflix.viewmodel

import com.example.netflix.data.Filme

data class FilmeUiState(
    val filmes: List<Filme> = emptyList(),
    val baixados: List<Filme> = emptyList(),
    val busca: String = "",
    val novoNome: String = ""
)
