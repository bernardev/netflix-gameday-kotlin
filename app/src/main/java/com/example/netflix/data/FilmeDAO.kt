package com.example.netflix.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmeDAO {

    @Insert
    suspend fun inserir(filme: Filme)

    @Query("SELECT * FROM filmes")
    fun buscarTodos(): Flow<List<Filme>>

    @Update
    suspend fun atualizar(filme: Filme)

    @Delete
    suspend fun deletar(filme: Filme)

    // Consultas filtradas por campo
    @Query("SELECT * FROM filmes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Filme

    @Query("SELECT * FROM filmes WHERE baixado = 1")
    fun buscarBaixados(): Flow<List<Filme>>

    @Query("SELECT COUNT(*) FROM filmes")
    suspend fun contar(): Int

}
