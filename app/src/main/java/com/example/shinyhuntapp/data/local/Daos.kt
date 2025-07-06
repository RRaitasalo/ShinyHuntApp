package com.example.shinyhuntapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}

@Dao
interface PokemonDao {
    @Insert
    suspend fun insertPokemons(pokemons: List<Pokemon>)

    @Query("SELECT * FROM pokemon")
    suspend fun getAllPokemons(): List<Pokemon>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): Pokemon?
}

@Dao
interface GameDao {
    @Insert
    suspend fun insertGames(games: List<Game>)
}

@Dao
interface PokemonInGameDao {
    @Insert
    suspend fun insertPokemonGames(pokemonGames: List<PokemonInGame>)
}

@Dao
interface HuntDao {
    @Insert
    suspend fun insertHunt(hunt: Hunt)

    @Update
    suspend fun updateHunt(hunt: Hunt)

    @Delete
    suspend fun deleteHunt(hunt: Hunt)

    @Query("SELECT * FROM hunt WHERE userId = :userId AND pokemonId = :pokemonId")
    suspend fun getHuntsByUserAndPokemon(userId: Int, pokemonId: Int): List<Hunt>

    @Query("SELECT * FROM hunt WHERE userId = :userId AND isFoundShiny = 1")
    suspend fun getCompletedHuntsByUser(userId: Int): List<Hunt>

    @Query("SELECT * FROM hunt WHERE userId = :userId AND isFoundShiny = 0")
    suspend fun getOngoingHuntsByUser(userId: Int): List<Hunt>
}