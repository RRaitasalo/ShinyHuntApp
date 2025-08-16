package com.example.shinyhuntapp.tools

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shinyhuntapp.data.local.DataBundle
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.PokemonDetails
import com.example.shinyhuntapp.data.network.PokeApiService
import com.example.shinyhuntapp.data.network.RetrofitInstance
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    runBlocking {
        DataBundleCreator().createBasicBundle()
    }
}

class DataBundleCreator {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createBasicBundle(): DataBundle{

        // Use your existing API fetching logic
        val api = RetrofitInstance.api
        val pokemonList = fetchPokemonFromAPI(api)

        saveBundleToFile(DataBundle(
            pokemon = pokemonList,
            lastUpdated = java.time.LocalDate.now().toString()
            )
        )

        return DataBundle(pokemon = pokemonList,
            lastUpdated = java.time.LocalDate.now().toString()
        )
    }

    private suspend fun fetchPokemonFromAPI(api: PokeApiService): List<Pokemon> {
        val pokemonList = mutableListOf<Pokemon>()
        try {
            val listResponse = api.getPokemonList()
            val entries = listResponse.results

            for (entry in entries) {
                val details = api.getPokemonDetailsByUrl(entry.url)
                val pokemon = mapToPokemon(details)
                pokemonList.add(pokemon)
            }
        } catch (e: Exception) {
            Log.e("PokemonViewModel", "Error fetching Pokémon", e)
        }

        return pokemonList
    }

    private fun mapToPokemon(details: PokemonDetails): Pokemon {
        val type1 = details.types.find { it.slot == 1 }?.type?.name ?: "unknown"
        val type2 = details.types.find { it.slot == 2 }?.type?.name

        return Pokemon(
            id = details.id,
            name = details.name.replaceFirstChar { it.uppercase() },
            nationalDexNumber = details.id,
            type1 = type1,
            type2 = type2,
            spriteUrl = details.sprites.other.officialArtwork.frontDefault ?: "",
            shinySprite = details.sprites.other.officialArtwork.frontShiny ?: ""
        )
    }

    fun saveBundleToFile(bundle: DataBundle, filename: String = "pokemon_bundle_v1.json") {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(bundle)
        File(filename).writeText(json)
    }
}