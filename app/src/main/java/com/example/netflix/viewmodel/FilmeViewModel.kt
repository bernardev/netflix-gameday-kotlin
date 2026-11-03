package com.example.netflix

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilmeViewModel : ViewModel() {

    // As listas agora sao uma copia do que esta no banco (Room), e nao mais a "fonte" dos dados
    var filmes by mutableStateOf<List<Filme>>(emptyList())
        private set

    var baixados by mutableStateOf<List<Filme>>(emptyList())
        private set

    fun carregar(filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            var lista = buscarFilmes(filmeDAO)
            if (lista.isEmpty()) {
                // Primeira vez que o app abre: grava no banco os filmes que eram fixos no Trabalho 2
                inserirFilmesIniciais(filmeDAO)
                lista = buscarFilmes(filmeDAO)
            }
            filmes = lista
        }
    }

    fun carregarBaixados(filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            baixados = buscarFilmesBaixados(filmeDAO)
        }
    }

    fun adicionar(nome: String, filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            inserirFilme(Filme(0, nome, false), filmeDAO)
            filmes = buscarFilmes(filmeDAO)
        }
    }

    fun renomear(filme: Filme, novoNome: String, filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            atualizarFilme(Filme(filme.id, novoNome, filme.baixado), filmeDAO)
            filmes = buscarFilmes(filmeDAO)
            baixados = buscarFilmesBaixados(filmeDAO)
        }
    }

    fun excluir(filme: Filme, filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            deletarFilme(filme, filmeDAO)
            filmes = buscarFilmes(filmeDAO)
            baixados = buscarFilmesBaixados(filmeDAO)
        }
    }

    fun baixar(filme: Filme, filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            atualizarFilme(Filme(filme.id, filme.nome, true), filmeDAO)
            baixados = buscarFilmesBaixados(filmeDAO)
        }
    }

    fun excluirDownload(filme: Filme, filmeDAO: FilmeDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            atualizarFilme(Filme(filme.id, filme.nome, false), filmeDAO)
            baixados = buscarFilmesBaixados(filmeDAO)
        }
    }

    // Os cartazes fixos das telas usam o nome do filme; aqui achamos o id dele no banco (0 = nao encontrado)
    fun idPorNome(nome: String): Int {
        var id = 0
        filmes.forEach {
            if (it.nome == nome) {
                id = it.id
            }
        }
        return id
    }

}

suspend fun inserirFilmesIniciais(filmeDAO: FilmeDAO) {
    inserirFilme(Filme(0, "Outer Banks", false), filmeDAO)
    inserirFilme(Filme(0, "O Mentalista", false), filmeDAO)
    inserirFilme(Filme(0, "The Vampire Diaries", false), filmeDAO)
    inserirFilme(Filme(0, "Bird Box", false), filmeDAO)
    inserirFilme(Filme(0, "Seven", true), filmeDAO)
    inserirFilme(Filme(0, "Clube da luta", false), filmeDAO)
    inserirFilme(Filme(0, "Interestelar", false), filmeDAO)
    inserirFilme(Filme(0, "Matrix", false), filmeDAO)
}

suspend fun buscarFilmes(filmeDAO: FilmeDAO): List<Filme> {
    return try {
        filmeDAO.buscarTodos()
    } catch (e: Exception) {
        Log.e("Erro ao buscar filmes", "Msg: ${e.message}")
        emptyList()
    }
}

suspend fun buscarFilmesBaixados(filmeDAO: FilmeDAO): List<Filme> {
    return try {
        filmeDAO.buscarBaixados()
    } catch (e: Exception) {
        Log.e("Erro ao buscar baixados", "Msg: ${e.message}")
        emptyList()
    }
}

suspend fun buscarFilmePorId(id: Int, filmeDAO: FilmeDAO): Filme {
    return try {
        filmeDAO.buscarPorId(id)
    } catch (e: Exception) {
        Log.e("Erro ao buscar filme", "Msg: ${e.message}")
        Filme(0, "", false)
    }
}

suspend fun inserirFilme(filme: Filme, filmeDAO: FilmeDAO) {
    try {
        filmeDAO.inserir(filme)
    } catch (e: Exception) {
        Log.e("Erro ao inserir filme", "Msg: ${e.message}")
    }
}

suspend fun atualizarFilme(filme: Filme, filmeDAO: FilmeDAO) {
    try {
        filmeDAO.atualizar(filme)
    } catch (e: Exception) {
        Log.e("Erro ao atualizar filme", "Msg: ${e.message}")
    }
}

suspend fun deletarFilme(filme: Filme, filmeDAO: FilmeDAO) {
    try {
        filmeDAO.deletar(filme)
    } catch (e: Exception) {
        Log.e("Erro ao deletar filme", "Msg: ${e.message}")
    }
}
