package com.example.netflix.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDAO {

    @Insert
    suspend fun inserir(perfil: Perfil)

    @Query("SELECT * FROM perfis")
    fun buscarTodos(): Flow<List<Perfil>>

    @Update
    suspend fun atualizar(perfil: Perfil)

    @Delete
    suspend fun deletar(perfil: Perfil)

    @Query("SELECT COUNT(*) FROM perfis")
    suspend fun contar(): Int

}
