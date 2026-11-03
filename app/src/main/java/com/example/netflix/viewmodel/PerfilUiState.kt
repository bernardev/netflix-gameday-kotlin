package com.example.netflix.viewmodel

import com.example.netflix.data.Perfil

data class PerfilUiState(
    val perfis: List<Perfil> = emptyList(),
    val perfilAtual: String = "lau",
    val idPerfilAtual: Int = 0,
    val ehAdmin: Boolean = false,
    val novoPerfil: String = "",
    val nomeEditado: String = "",
    val proximoEpisodio: Boolean = true,
    val previas: Boolean = true
)
