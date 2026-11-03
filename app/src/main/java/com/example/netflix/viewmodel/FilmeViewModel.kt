package com.example.netflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflix.data.Filme
import com.example.netflix.data.repository.FilmeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilmeViewModel(private val repository: FilmeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FilmeUiState())
    val uiState: StateFlow<FilmeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.inserirIniciaisSeVazio()
        }
        viewModelScope.launch {
            repository.buscarTodos().collect { lista ->
                _uiState.value = _uiState.value.copy(filmes = lista)
            }
        }
        viewModelScope.launch {
            repository.buscarBaixados().collect { lista ->
                _uiState.value = _uiState.value.copy(baixados = lista)
            }
        }
    }

    fun atualizarBusca(texto: String) {
        _uiState.value = _uiState.value.copy(busca = texto)
    }

    fun atualizarNovoNome(texto: String) {
        _uiState.value = _uiState.value.copy(novoNome = texto)
    }

    fun adicionar(): Boolean {
        val nome = _uiState.value.busca
        if (nome == "") {
            return false
        }
        _uiState.value = _uiState.value.copy(busca = "")
        viewModelScope.launch {
            repository.inserir(Filme(0, nome, false))
        }
        return true
    }

    fun renomear(filme: Filme): Boolean {
        val novoNome = _uiState.value.novoNome
        if (novoNome == "") {
            return false
        }
        _uiState.value = _uiState.value.copy(novoNome = "")
        viewModelScope.launch {
            repository.atualizar(Filme(filme.id, novoNome, filme.baixado))
        }
        return true
    }

    fun excluir(filme: Filme) {
        viewModelScope.launch {
            repository.deletar(filme)
        }
    }

    fun baixar(filme: Filme) {
        viewModelScope.launch {
            repository.atualizar(Filme(filme.id, filme.nome, true))
        }
    }

    fun excluirDownload(filme: Filme) {
        viewModelScope.launch {
            repository.atualizar(Filme(filme.id, filme.nome, false))
        }
    }

    // Os cartazes fixos das telas usam o nome do filme; aqui achamos o id dele no banco (0 = nao encontrado)
    fun idPorNome(nome: String): Int {
        var id = 0
        _uiState.value.filmes.forEach {
            if (it.nome == nome) {
                id = it.id
            }
        }
        return id
    }

}
