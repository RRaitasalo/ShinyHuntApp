package com.example.shinyhuntapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.data.local.PokemonDetails
import com.example.shinyhuntapp.data.local.UserPokemonDao
import com.example.shinyhuntapp.data.network.PokeApiService
import com.example.shinyhuntapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonDao: PokemonDao,
    private val userPokemonDao: UserPokemonDao,
    private val api: PokeApiService,
    private val preferences: PreferenceManager
): ViewModel() {

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

    fun getPokemonById(id: Int): Pokemon? {
        viewModelScope.launch {
            try {
                pokemonDao.getPokemonById(id)
                Log.d("PokemonViewModel", "Pokemon fetched from Room")
            } catch (e: Exception) {
                Log.d("PokemonViewModel", "Error fetching Pokémon", e)
            }
        }
        return null
    }

    fun fetchPokemonList() {
        viewModelScope.launch {
            try {
                val pokemonList = pokemonDao.getAllPokemon()
                _pokemonList.value = pokemonList
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon", e)
            }
        }
    }

    fun fetchAndStorePokemonIfNeeded() {
        if (!preferences.hasFetchedPokemon()) {
            fetchAndStoreAllPokemon()
        }
    }

    fun fetchAndStoreAllPokemon() {
        Log.d("PokemonViewModel", "Fetching and storing all Pokémon")
        viewModelScope.launch {
            try {
                val listResponse = api.getPokemonList()
                val entries = listResponse.results

                val pokemonList = mutableListOf<Pokemon>()

                for (entry in entries) {
                    Log.d("PokemonViewModel", "Fetching details for ${entry.name}")
                    val url = entry.url
                    val details = api.getPokemonDetailsByUrl(url)
                    val pokemon = mapToPokemon(details)
                    pokemonList.add(pokemon)
                }

                // Save all to Room in one go
                pokemonDao.insertAllPokemon(pokemonList)
                preferences.setHasFetchedPokemon(true)

            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon", e)
            }
        }
    }

    private fun mapToPokemon(details: PokemonDetails): Pokemon {
        val type1 = details.types.find { it.slot == 1 }?.type?.name ?: "unknown"
        val type2 = details.types.find { it.slot == 2 }?.type?.name

        return Pokemon(
            id = details.id,
            name = details.name.replaceFirstChar { it.uppercase() },
            nationalDexNumber = details.id,
            type1 = type1,
            type2 = type2
        )
    }

    // -- DEVELOPER TOOLS --
    fun clearPokemonTable(){
        viewModelScope.launch {
            pokemonDao.deleteAll()
            preferences.setHasFetchedPokemon(false)
        }
    }

    fun resetFirstLaunchFlag() {
        preferences.setHasFetchedPokemon(false)
    }

    fun forceFetchPokemon() {
        fetchAndStoreAllPokemon()
        preferences.setHasFetchedPokemon(true)
    }

    fun getPokemon(id: Int): Pokemon? {
        return getPokemonById(id)
    }


}

class PokemonViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        val preferenceManager = PreferenceManager(context)
        val pokemonDao = db.pokemonDao()
        val userPokemonDao = db.userPokemonDao()
        val api = RetrofitInstance.api

        return PokemonViewModel(pokemonDao, userPokemonDao, api, preferenceManager) as T
    }
}