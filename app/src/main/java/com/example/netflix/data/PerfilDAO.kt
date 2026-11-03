package com.example.netflix

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PerfilDAO {

    @Insert
    suspend fun inserir(perfil: Perfil)

    @Query("SELECT * FROM perfis")
    suspend fun buscarTodos(): List<Perfil>

    @Update
    suspend fun atualizar(perfil: Perfil)

    @Delete
    suspend fun deletar(perfil: Perfil)

}
