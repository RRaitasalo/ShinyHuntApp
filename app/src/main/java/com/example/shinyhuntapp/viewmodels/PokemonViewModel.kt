package com.example.shinyhuntapp.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.UserPokemon
import com.example.shinyhuntapp.data.local.UserPokemonDao
import com.example.shinyhuntapp.data.local.repository.PokemonRepository
import com.example.shinyhuntapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository,
    private val userPokemonDao: UserPokemonDao,
    private val preferences: PreferenceManager
): ViewModel() {

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

    private val _userPokemonMap = MutableStateFlow<Map<Int, UserPokemon>>(emptyMap())
    val userPokemonMap: StateFlow<Map<Int, UserPokemon>> = _userPokemonMap

    private val _isUserPokemonLoaded = MutableStateFlow(false)
    val isUserPokemonLoaded: StateFlow<Boolean> = _isUserPokemonLoaded

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAndStorePokemonIfNeeded() {
        if (!preferences.hasFetchedPokemon()) {
            fetchAndStorePokemonData()
        } else {
            Log.d("PokemonViewModel", "Pokemon already fetched")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAndStorePokemonData() {
        viewModelScope.launch {
            try {
                val success = repository.initializePokemonData()
                if (success) {
                    preferences.setHasFetchedPokemon(true)
                    fetchPokemonList()
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading Pokemon", e)
            }
        }
    }

    fun fetchPokemonList() {
        viewModelScope.launch {
            try {
                val pokemonList = repository.getAllPokemon()
                _pokemonList.value = pokemonList
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon", e)
            }
        }
    }

    fun fetchUserPokemon() {
        viewModelScope.launch {
            try {
                val userId = preferences.getLoggedInUserId()
                val userPokemonList = userPokemonDao.getAllUserPokemon(userId)
                _userPokemonMap.value = userPokemonList.associateBy { it.pokemonId }
                _isUserPokemonLoaded.value = true
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching user Pokemon", e)
                _isUserPokemonLoaded.value = true
            }
        }
    }

    fun getPokemonByIdDevToolsTest(id: Int): Pokemon? {
        viewModelScope.launch {
            try {
                repository.getPokemonById(id)
                Log.d("PokemonViewModel", "Pokemon fetched from Room")
            } catch (e: Exception) {
                Log.d("PokemonViewModel", "Error fetching Pokémon", e)
            }
        }
        return null
    }

    fun getPokemonById(id: Int, onResult: (Pokemon?) -> Unit) {
        viewModelScope.launch {
            try {
                val pokemon = repository.getPokemonById(id)
                onResult(pokemon)
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon by ID", e)
                onResult(null)
            }
        }
    }

    fun getPokemonByGame(gameName: String) {
        viewModelScope.launch {
            try {
                val pokemonList = repository.getPokemonByGame(gameName)
                Log.d("PokemonViewModel", "Pokemon fetched by game: $pokemonList")
                _pokemonList.value = pokemonList
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon by game", e)
            }
        }
    }

    fun getPokemonByGenerations(generations: Set<Int>) {
        viewModelScope.launch {
            try {
                val pokemonList = repository.getPokemonByGenerations(generations)
                Log.d("PokemonViewModel", "Pokemon fetched by generations: $pokemonList")
                _pokemonList.value = pokemonList
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon by generations", e)
            }
        }
    }

    fun getPokemonByGameAndGenerations(gameName: String, generations: Set<Int>) {
        viewModelScope.launch {
            try {
                val pokemonList = repository.getPokemonByGameAndGenerations(gameName, generations)
                Log.d("PokemonViewModel", "Pokemon fetched by game and generations: $pokemonList")
                _pokemonList.value = pokemonList
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon by game and generations", e)
            }
        }
    }

    fun toggleShinyStatus(pokemonId: Int) {
        viewModelScope.launch {
            try {
                val userId = preferences.getLoggedInUserId()
                val existingUserPokemon = userPokemonDao.getUserPokemon(userId, pokemonId)

                if (existingUserPokemon == null) {

                    val newUserPokemon = UserPokemon(
                        pokemonId = pokemonId,
                        userId = userId,
                        hasCaughtShiny = true,
                        caughtDate = null,
                        caughtCount = 1,
                        isFromHunt = false
                    )
                    userPokemonDao.insert(newUserPokemon)
                } else {
                    if (existingUserPokemon.hasCaughtShiny) {
                        if (!existingUserPokemon.isFromHunt) {
                            userPokemonDao.deleteUserPokemon(existingUserPokemon)
                        } else {
                            val updated = existingUserPokemon.copy(hasCaughtShiny = false)
                            userPokemonDao.updateUserPokemon(updated)
                        }
                    } else {
                        val updated = existingUserPokemon.copy(
                            hasCaughtShiny = true,
                            caughtDate = existingUserPokemon.caughtDate
                        )
                        userPokemonDao.updateUserPokemon(updated)
                    }
                }

                fetchUserPokemon()
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error toggling shiny status", e)
            }
        }
    }

    // -- DEVELOPER TOOLS --
    fun clearPokemonTable(){
        viewModelScope.launch {
            repository.clearDatabase()
            preferences.setHasFetchedPokemon(false)
        }
    }

    fun resetFirstLaunchFlag() {
        preferences.setHasFetchedPokemon(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun forceFetchPokemon() {
        fetchAndStorePokemonData()
        preferences.setHasFetchedPokemon(true)
    }

    fun getPokemon(id: Int): Pokemon? {
        return getPokemonByIdDevToolsTest(id)
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
        val gameDao = db.gameDao()
        val gameAvailabilityDao = db.gameAvailabilityDao()
        val repository = PokemonRepository(context, pokemonDao, gameDao, gameAvailabilityDao)

        return PokemonViewModel(repository, userPokemonDao, preferenceManager) as T
    }
}