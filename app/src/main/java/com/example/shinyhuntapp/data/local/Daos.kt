package com.example.shinyhuntapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemon(pokemonList: List<Pokemon>)

    @Query("SELECT * FROM pokemon")
    suspend fun getAllPokemon(): List<Pokemon>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): Pokemon?

    @Query("DELETE FROM pokemon")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getPokemonCount(): Int
}

@Dao
interface UserPokemonDao {

    @Query("SELECT * FROM user_pokemon WHERE userId = :userId AND pokemonId = :pokemonId")
    suspend fun getUserPokemon(userId: Int, pokemonId: Int): UserPokemon?

    @Query("SELECT * FROM user_pokemon WHERE userId = :userId")
    suspend fun getAllUserPokemon(userId: Int): List<UserPokemon>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPokemon: UserPokemon)

    @Query("SELECT * FROM user_pokemon WHERE pokemonId = :pokemonId")
    fun observeByPokemonId(pokemonId: Int): Flow<UserPokemon?>

    @Query("UPDATE user_pokemon SET hasCaughtShiny = :caught WHERE pokemonId = :pokemonId")
    suspend fun setCaughtShiny(pokemonId: Int, caught: Boolean)

    @Update
    suspend fun updateUserPokemon(userPokemon: UserPokemon)

    @Delete
    suspend fun deleteUserPokemon(userPokemon: UserPokemon)
}

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Query("SELECT * FROM games WHERE name = :name")
    suspend fun getGameByName(name: String): Game?

    @Query("""
        SELECT g.* FROM games g
        INNER JOIN game_availability ga ON g.id = ga.gameId
        WHERE ga.pokemonId = :pokemonId
    """)
    suspend fun getGamesByPokemonId(pokemonId: Int): List<Game>
}

@Dao
interface GameAvailabilityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(availability: List<GameAvailability>)

    @Query("SELECT * FROM game_availability WHERE pokemonId = :pokemonId AND gameId = :gameId")
    suspend fun getAvailability(pokemonId: Int, gameId: Int): GameAvailability?
}

@Dao
interface HuntDao {
    @Insert
    suspend fun insertHunt(hunt: Hunt)

    @Update
    suspend fun updateHunt(hunt: Hunt)

    @Delete
    suspend fun deleteHunt(hunt: Hunt)

    @Query("SELECT * FROM hunts WHERE userId = :userId AND pokemonId = :pokemonId")
    suspend fun getHuntByUserAndPokemon(userId: Int, pokemonId: Int): Hunt?

    @Query("SELECT * FROM hunts WHERE id = :huntId")
    suspend fun getHuntById(huntId: Long): Hunt?

    @Query("SELECT * FROM hunts WHERE userId = :userId AND isFoundShiny = 1")
    suspend fun getCompletedHuntsByUser(userId: Int): List<Hunt>

    @Query("SELECT * FROM hunts WHERE userId = :userId AND isFoundShiny = 0")
    suspend fun getOngoingHuntsByUser(userId: Int): List<Hunt>

    @Query("SELECT * FROM hunts WHERE userId = :userId")
    suspend fun getAllHunts(userId: Int): List<Hunt>

    @Query("UPDATE hunts SET encounters = encounters + :amount WHERE id = :huntId")
    suspend fun addEncounters(huntId: Long, amount: Int)
}