package com.example.netflix

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    // A lista agora e uma copia do que esta no banco (Room)
    var perfis by mutableStateOf<List<Perfil>>(emptyList())
        private set

    val nomesAdmin = mutableStateListOf(
        "admin", "admiN", "admIn", "admIN",
        "adMin", "adMiN", "adMIn", "adMIN",
        "aDmin", "aDmiN", "aDmIn", "aDmIN",
        "aDMin", "aDMiN", "aDMIn", "aDMIN",
        "Admin", "AdmiN", "AdmIn", "AdmIN",
        "AdMin", "AdMiN", "AdMIn", "AdMIN",
        "ADmin", "ADmiN", "ADmIn", "ADmIN",
        "ADMin", "ADMiN", "ADMIn", "ADMIN"
    )

    var perfilAtual by mutableStateOf("lau")
        private set

    // id do perfil atual no banco, usado para atualizar e excluir a linha certa
    var idPerfilAtual by mutableStateOf(0)
        private set

    var proximoEpisodio by mutableStateOf(true)
        private set

    var previas by mutableStateOf(true)
        private set

    fun carregar(perfilDAO: PerfilDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            var lista = buscarPerfis(perfilDAO)
            if (lista.isEmpty()) {
                // Primeira vez que o app abre: grava no banco os perfis que eram fixos no Trabalho 2
                inserirPerfisIniciais(perfilDAO)
                lista = buscarPerfis(perfilDAO)
            }
            perfis = lista
        }
    }

    fun selecionar(perfil: Perfil) {
        perfilAtual = perfil.nome
        idPerfilAtual = perfil.id
    }

    fun adicionar(nome: String, perfilDAO: PerfilDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            inserirPerfil(Perfil(0, nome), perfilDAO)
            perfis = buscarPerfis(perfilDAO)
        }
    }

    fun renomear(novoNome: String, perfilDAO: PerfilDAO) {
        val perfil = Perfil(idPerfilAtual, novoNome)
        perfilAtual = novoNome
        CoroutineScope(Dispatchers.IO).launch {
            atualizarPerfil(perfil, perfilDAO)
            perfis = buscarPerfis(perfilDAO)
        }
    }

    fun excluir(perfilDAO: PerfilDAO) {
        val perfil = Perfil(idPerfilAtual, perfilAtual)
        CoroutineScope(Dispatchers.IO).launch {
            deletarPerfil(perfil, perfilDAO)
            perfis = buscarPerfis(perfilDAO)
        }
    }

    fun alternarProximoEpisodio() {
        proximoEpisodio = !proximoEpisodio
    }

    fun alternarPrevias() {
        previas = !previas
    }

}

suspend fun inserirPerfisIniciais(perfilDAO: PerfilDAO) {
    inserirPerfil(Perfil(0, "Manu"), perfilDAO)
    inserirPerfil(Perfil(0, "Rsjr"), perfilDAO)
    inserirPerfil(Perfil(0, "buarda"), perfilDAO)
    inserirPerfil(Perfil(0, "lau"), perfilDAO)
    inserirPerfil(Perfil(0, "Gabi"), perfilDAO)
    inserirPerfil(Perfil(0, "Admin"), perfilDAO)
}

suspend fun buscarPerfis(perfilDAO: PerfilDAO): List<Perfil> {
    return try {
        perfilDAO.buscarTodos()
    } catch (e: Exception) {
        Log.e("Erro ao buscar perfis", "Msg: ${e.message}")
        emptyList()
    }
}

suspend fun inserirPerfil(perfil: Perfil, perfilDAO: PerfilDAO) {
    try {
        perfilDAO.inserir(perfil)
    } catch (e: Exception) {
        Log.e("Erro ao inserir perfil", "Msg: ${e.message}")
    }
}

suspend fun atualizarPerfil(perfil: Perfil, perfilDAO: PerfilDAO) {
    try {
        perfilDAO.atualizar(perfil)
    } catch (e: Exception) {
        Log.e("Erro ao atualizar perfil", "Msg: ${e.message}")
    }
}

suspend fun deletarPerfil(perfil: Perfil, perfilDAO: PerfilDAO) {
    try {
        perfilDAO.deletar(perfil)
    } catch (e: Exception) {
        Log.e("Erro ao deletar perfil", "Msg: ${e.message}")
    }
}
