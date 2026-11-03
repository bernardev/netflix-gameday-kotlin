package com.example.netflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflix.data.Perfil
import com.example.netflix.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: PerfilRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val nomesAdmin = listOf(
        "admin", "admiN", "admIn", "admIN",
        "adMin", "adMiN", "adMIn", "adMIN",
        "aDmin", "aDmiN", "aDmIn", "aDmIN",
        "aDMin", "aDMiN", "aDMIn", "aDMIN",
        "Admin", "AdmiN", "AdmIn", "AdmIN",
        "AdMin", "AdMiN", "AdMIn", "AdMIN",
        "ADmin", "ADmiN", "ADmIn", "ADmIN",
        "ADMin", "ADMiN", "ADMIn", "ADMIN"
    )

    init {
        viewModelScope.launch {
            repository.inserirIniciaisSeVazio()
        }
        viewModelScope.launch {
            repository.buscarTodos().collect { lista ->
                _uiState.value = _uiState.value.copy(perfis = lista)
            }
        }
    }

    fun selecionar(perfil: Perfil) {
        _uiState.value = _uiState.value.copy(
            perfilAtual = perfil.nome,
            idPerfilAtual = perfil.id,
            ehAdmin = nomesAdmin.contains(perfil.nome)
        )
    }

    fun atualizarNovoPerfil(texto: String) {
        _uiState.value = _uiState.value.copy(novoPerfil = texto)
    }

    fun adicionar(): Boolean {
        val nome = _uiState.value.novoPerfil
        if (nome == "") {
            return false
        }
        _uiState.value = _uiState.value.copy(novoPerfil = "")
        viewModelScope.launch {
            repository.inserir(Perfil(0, nome))
        }
        return true
    }

    fun comecarEdicao() {
        _uiState.value = _uiState.value.copy(nomeEditado = _uiState.value.perfilAtual)
    }

    fun atualizarNomeEditado(texto: String) {
        _uiState.value = _uiState.value.copy(nomeEditado = texto)
    }

    fun renomear(): Boolean {
        val novoNome = _uiState.value.nomeEditado
        if (novoNome == "") {
            return false
        }
        val perfil = Perfil(_uiState.value.idPerfilAtual, novoNome)
        _uiState.value = _uiState.value.copy(
            perfilAtual = novoNome,
            ehAdmin = nomesAdmin.contains(novoNome)
        )
        viewModelScope.launch {
            repository.atualizar(perfil)
        }
        return true
    }

    fun excluir() {
        val perfil = Perfil(_uiState.value.idPerfilAtual, _uiState.value.perfilAtual)
        viewModelScope.launch {
            repository.deletar(perfil)
        }
    }

    fun alternarProximoEpisodio() {
        _uiState.value = _uiState.value.copy(proximoEpisodio = !_uiState.value.proximoEpisodio)
    }

    fun alternarPrevias() {
        _uiState.value = _uiState.value.copy(previas = !_uiState.value.previas)
    }

}
