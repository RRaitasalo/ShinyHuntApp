package com.example.shinyhuntapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.example.shinyhuntapp.data.local.relations.PokemonWithGames
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

    @Transaction
    @Query("SELECT * FROM pokemon WHERE id = :pokemonId")
    suspend fun getPokemonWithGames(pokemonId: Int): PokemonWithGames?

    @Transaction
    @Query("""
        SELECT DISTINCT p.* FROM pokemon p 
        INNER JOIN game_availability ga ON p.id = ga.pokemonId 
        WHERE ga.gameId = :gameId
        AND ga.obtainMethod IN ('wild', 'evolve', 'breed')
    """)
    suspend fun getCatchablePokemonByGame(gameId: Int): List<Pokemon>

    @Query("SELECT * FROM pokemon WHERE id BETWEEN :startId AND :endId")
    suspend fun getPokemonByGeneration(startId: Int, endId: Int): List<Pokemon>

    @Query("""
        SELECT * FROM pokemon 
        WHERE nationalDexNumber BETWEEN :start1 AND :end1
        OR nationalDexNumber BETWEEN :start2 AND :end2
        OR nationalDexNumber BETWEEN :start3 AND :end3
        OR nationalDexNumber BETWEEN :start4 AND :end4
        OR nationalDexNumber BETWEEN :start5 AND :end5
        OR nationalDexNumber BETWEEN :start6 AND :end6
        OR nationalDexNumber BETWEEN :start7 AND :end7
        OR nationalDexNumber BETWEEN :start8 AND :end8
        OR nationalDexNumber BETWEEN :start9 AND :end9
        ORDER BY nationalDexNumber
    """)
    suspend fun getPokemonByGenerationsRaw(
        start1: Int = 0, end1: Int = 0,
        start2: Int = 0, end2: Int = 0,
        start3: Int = 0, end3: Int = 0,
        start4: Int = 0, end4: Int = 0,
        start5: Int = 0, end5: Int = 0,
        start6: Int = 0, end6: Int = 0,
        start7: Int = 0, end7: Int = 0,
        start8: Int = 0, end8: Int = 0,
        start9: Int = 0, end9: Int = 0
    ): List<Pokemon>

    suspend fun getPokemonByGenerations(ranges: List<Pair<Int, Int>>): List<Pokemon> {
        val paddedRanges = ranges + List(9 - ranges.size) { Pair(0, 0) }

        return getPokemonByGenerationsRaw(
            paddedRanges[0].first, paddedRanges[0].second,
            paddedRanges[1].first, paddedRanges[1].second,
            paddedRanges[2].first, paddedRanges[2].second,
            paddedRanges[3].first, paddedRanges[3].second,
            paddedRanges[4].first, paddedRanges[4].second,
            paddedRanges[5].first, paddedRanges[5].second,
            paddedRanges[6].first, paddedRanges[6].second,
            paddedRanges[7].first, paddedRanges[7].second,
            paddedRanges[8].first, paddedRanges[8].second
        )
    }

    @Query("""
    SELECT p.* FROM pokemon p
    INNER JOIN game_availability ga ON p.id = ga.pokemonId AND ga.obtainMethod IN ('wild', 'evolve', 'breed')
    INNER JOIN games g ON ga.gameId = g.id
    WHERE g.name = :gameName
    AND (p.nationalDexNumber BETWEEN :start1 AND :end1
    OR p.nationalDexNumber BETWEEN :start2 AND :end2
    OR p.nationalDexNumber BETWEEN :start3 AND :end3
    OR p.nationalDexNumber BETWEEN :start4 AND :end4
    OR p.nationalDexNumber BETWEEN :start5 AND :end5
    OR p.nationalDexNumber BETWEEN :start6 AND :end6
    OR p.nationalDexNumber BETWEEN :start7 AND :end7
    OR p.nationalDexNumber BETWEEN :start8 AND :end8
    OR p.nationalDexNumber BETWEEN :start9 AND :end9)
    ORDER BY p.nationalDexNumber
""")
    suspend fun getPokemonByGameAndGenerationsRaw(
        gameName: String,
        start1: Int = 0, end1: Int = 0,
        start2: Int = 0, end2: Int = 0,
        start3: Int = 0, end3: Int = 0,
        start4: Int = 0, end4: Int = 0,
        start5: Int = 0, end5: Int = 0,
        start6: Int = 0, end6: Int = 0,
        start7: Int = 0, end7: Int = 0,
        start8: Int = 0, end8: Int = 0,
        start9: Int = 0, end9: Int = 0
    ): List<Pokemon>

    suspend fun getPokemonByGameAndGenerations(gameName: String, ranges: List<Pair<Int, Int>>): List<Pokemon> {
        val paddedRanges = ranges + List(9 - ranges.size) { Pair(0, 0) }

        return getPokemonByGameAndGenerationsRaw(
            gameName,
            paddedRanges[0].first, paddedRanges[0].second,
            paddedRanges[1].first, paddedRanges[1].second,
            paddedRanges[2].first, paddedRanges[2].second,
            paddedRanges[3].first, paddedRanges[3].second,
            paddedRanges[4].first, paddedRanges[4].second,
            paddedRanges[5].first, paddedRanges[5].second,
            paddedRanges[6].first, paddedRanges[6].second,
            paddedRanges[7].first, paddedRanges[7].second,
            paddedRanges[8].first, paddedRanges[8].second
        )
    }
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