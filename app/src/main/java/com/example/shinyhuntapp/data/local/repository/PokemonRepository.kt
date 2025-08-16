package com.example.shinyhuntapp.data.local.repository

import android.content.Context
import android.util.Log
import com.example.shinyhuntapp.data.local.DataBundle
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.tools.DataBundleCreator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val context: Context,
    private val pokemonDao: PokemonDao,
) {

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
            val jsonString = context.assets.open("pokemon_bundle_v1.json")
                .bufferedReader().use { it.readText() }
            val bundle = Gson().fromJson(jsonString, DataBundle::class.java)

            pokemonDao.insertAllPokemon(bundle.pokemon)

            Log.d("Repository", "Loaded ${bundle.pokemon.size} Pokemon from JSON bundle")
            true

        } catch (e: Exception) {
            Log.d("Repository", "Could not load JSON bundle: ${e.message}")
            false
        }
    }

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
}