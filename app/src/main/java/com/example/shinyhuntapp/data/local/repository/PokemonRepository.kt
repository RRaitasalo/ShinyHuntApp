package com.example.shinyhuntapp.data.local.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shinyhuntapp.data.local.DataBundle
import com.example.shinyhuntapp.data.local.Game
import com.example.shinyhuntapp.data.local.GameAvailabilityDao
import com.example.shinyhuntapp.data.local.GameDao
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.tools.DataBundleCreator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val context: Context,
    private val pokemonDao: PokemonDao,
    private val gameDao: GameDao,
    private val gameAvailabilityDao: GameAvailabilityDao
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initializePokemonData(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (pokemonDao.getPokemonCount() > 0) {
                Log.d("Repository", "Pokemon data already exists")
                return@withContext true
            }

            if (loadFromJsonBundle()) {
                Log.d("Repository", "Loaded from JSON bundle")
                return@withContext true
            }

            Log.d("Repository", "JSON not found, using API fallback")
            return@withContext loadFromApiFallback()

        } catch (e: Exception) {
            Log.e("Repository", "Error initializing Pokemon data", e)
            false
        }
    }

    private suspend fun loadFromJsonBundle(): Boolean {
        return try {
            val jsonString = context.assets.open("pokemon_bundle_v2.json")
                .bufferedReader().use { it.readText() }
            val bundle = Gson().fromJson(jsonString, DataBundle::class.java)

            pokemonDao.insertAllPokemon(bundle.pokemon)
            gameDao.insertGames(bundle.games)
            gameAvailabilityDao.insertAvailability(bundle.gameAvailability)

            Log.d("Repository", "Loaded ${bundle.pokemon.size} Pokemon from JSON bundle")
            true

        } catch (e: Exception) {
            Log.d("Repository", "Could not load JSON bundle: ${e.message}")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadFromApiFallback(): Boolean {
        return try {
            val creator = DataBundleCreator()
            val bundle = creator.createBasicBundle()

            pokemonDao.insertAllPokemon(bundle.pokemon)
            Log.d("Repository", "Loaded ${bundle.pokemon.size} Pokemon from API")
            true

        } catch (e: Exception) {
            Log.e("Repository", "API fallback failed", e)
            false
        }
    }

    suspend fun getAllPokemon(): List<Pokemon> = pokemonDao.getAllPokemon()
    suspend fun getPokemonById(id: Int): Pokemon? = pokemonDao.getPokemonById(id)
    suspend fun clearDatabase() = pokemonDao.deleteAll()
    suspend fun getGamesByPokemon(pokemonId: Int): List<Game> = gameDao.getGamesByPokemonId(pokemonId)

    suspend fun getPokemonByGame(gameName: String): List<Pokemon> {
        val game = gameDao.getGameByName(gameName) ?: return emptyList()
        return pokemonDao.getCatchablePokemonByGame(game.id)
    }

    suspend fun getPokemonByGeneration(generation: Int): List<Pokemon> {
        val (start, end) = when (generation) {
            1 -> Pair(1, 151)
            2 -> Pair(152, 251)
            3 -> Pair(252, 386)
            4 -> Pair(387, 493)
            5 -> Pair(494, 649)
            6 -> Pair(650, 721)
            7 -> Pair(722, 809)
            8 -> Pair(810, 905)
            9 -> Pair(906, 1025)
            else -> return emptyList()
        }
        return pokemonDao.getPokemonByGeneration(start, end)
    }
}