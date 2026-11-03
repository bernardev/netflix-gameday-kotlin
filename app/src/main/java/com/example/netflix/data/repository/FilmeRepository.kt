package com.example.netflix.data.repository

import android.util.Log
import com.example.netflix.data.Filme
import com.example.netflix.data.FilmeDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class FilmeRepository(private val filmeDAO: FilmeDAO) {

    fun buscarTodos(): Flow<List<Filme>> {
        return filmeDAO.buscarTodos().catch { e ->
            Log.e("Erro ao buscar filmes", "Msg: ${e.message}")
            emit(emptyList())
        }
    }

    fun buscarBaixados(): Flow<List<Filme>> {
        return filmeDAO.buscarBaixados().catch { e ->
            Log.e("Erro ao buscar baixados", "Msg: ${e.message}")
            emit(emptyList())
        }
    }

    suspend fun inserir(filme: Filme) {
        try {
            filmeDAO.inserir(filme)
        } catch (e: Exception) {
            Log.e("Erro ao inserir filme", "Msg: ${e.message}")
        }
    }

    suspend fun atualizar(filme: Filme) {
        try {
            filmeDAO.atualizar(filme)
        } catch (e: Exception) {
            Log.e("Erro ao atualizar filme", "Msg: ${e.message}")
        }
    }

    suspend fun deletar(filme: Filme) {
        try {
            filmeDAO.deletar(filme)
        } catch (e: Exception) {
            Log.e("Erro ao deletar filme", "Msg: ${e.message}")
        }
    }

    suspend fun inserirIniciaisSeVazio() {
        try {
            if (filmeDAO.contar() == 0) {
                filmeDAO.inserir(Filme(0, "Outer Banks", false))
                filmeDAO.inserir(Filme(0, "O Mentalista", false))
                filmeDAO.inserir(Filme(0, "The Vampire Diaries", false))
                filmeDAO.inserir(Filme(0, "Bird Box", false))
                filmeDAO.inserir(Filme(0, "Seven", true))
                filmeDAO.inserir(Filme(0, "Clube da luta", false))
                filmeDAO.inserir(Filme(0, "Interestelar", false))
                filmeDAO.inserir(Filme(0, "Matrix", false))
            }
        } catch (e: Exception) {
            Log.e("Erro ao inserir filmes iniciais", "Msg: ${e.message}")
        }
    }

}
