package com.example.netflix

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmeDAO {

    @Insert
    suspend fun inserir(filme: Filme)

    @Query("SELECT * FROM filmes")
    suspend fun buscarTodos(): List<Filme>

    @Update
    suspend fun atualizar(filme: Filme)

    @Delete
    suspend fun deletar(filme: Filme)

    // Consultas filtradas por campo
    @Query("SELECT * FROM filmes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Filme

    @Query("SELECT * FROM filmes WHERE baixado = 1")
    suspend fun buscarBaixados(): List<Filme>

}
