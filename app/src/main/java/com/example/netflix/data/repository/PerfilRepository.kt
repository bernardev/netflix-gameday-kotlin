package com.example.netflix.data.repository

import android.util.Log
import com.example.netflix.data.Perfil
import com.example.netflix.data.PerfilDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class PerfilRepository(private val perfilDAO: PerfilDAO) {

    fun buscarTodos(): Flow<List<Perfil>> {
        return perfilDAO.buscarTodos().catch { e ->
            Log.e("Erro ao buscar perfis", "Msg: ${e.message}")
            emit(emptyList())
        }
    }

    suspend fun inserir(perfil: Perfil) {
        try {
            perfilDAO.inserir(perfil)
        } catch (e: Exception) {
            Log.e("Erro ao inserir perfil", "Msg: ${e.message}")
        }
    }

    suspend fun atualizar(perfil: Perfil) {
        try {
            perfilDAO.atualizar(perfil)
        } catch (e: Exception) {
            Log.e("Erro ao atualizar perfil", "Msg: ${e.message}")
        }
    }

    suspend fun deletar(perfil: Perfil) {
        try {
            perfilDAO.deletar(perfil)
        } catch (e: Exception) {
            Log.e("Erro ao deletar perfil", "Msg: ${e.message}")
        }
    }

    suspend fun inserirIniciaisSeVazio() {
        try {
            if (perfilDAO.contar() == 0) {
                perfilDAO.inserir(Perfil(0, "Manu"))
                perfilDAO.inserir(Perfil(0, "Rsjr"))
                perfilDAO.inserir(Perfil(0, "buarda"))
                perfilDAO.inserir(Perfil(0, "lau"))
                perfilDAO.inserir(Perfil(0, "Gabi"))
                perfilDAO.inserir(Perfil(0, "Admin"))
            }
        } catch (e: Exception) {
            Log.e("Erro ao inserir perfis iniciais", "Msg: ${e.message}")
        }
    }

}
