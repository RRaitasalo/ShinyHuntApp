package com.example.shinyhuntapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.data.local.PokemonDetails
import com.example.shinyhuntapp.data.local.PokemonEntry
import com.example.shinyhuntapp.data.local.UserPokemonDao
import com.example.shinyhuntapp.data.network.PokeApiService
import com.example.shinyhuntapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonDao: PokemonDao,
    private val userPokemonDao: UserPokemonDao,
    private val api: PokeApiService
): ViewModel() {

    private val _pokemonDetailsList = MutableStateFlow<List<PokemonDetails>>(emptyList())
    val pokemonDetailsList: StateFlow<List<PokemonDetails>> = _pokemonDetailsList

    fun fetchPokemonDetails() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPokemonList()
                for (pokemon in response.results) {
                    val detailsResponse = RetrofitInstance.api.getPokemonDetailsByUrl(url = pokemon.url)
                    Log.d("PokemonViewModel", "Pokemon details: $detailsResponse")
                    _pokemonDetailsList.value = _pokemonDetailsList.value + detailsResponse
                }
                Log.d("PokemonViewModel", "Pokemon details list: ${_pokemonDetailsList.value}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

class PokemonViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        val pokemonDao = db.pokemonDao()
        val userPokemonDao = db.userPokemonDao()
        val api = RetrofitInstance.api

        return PokemonViewModel(pokemonDao, userPokemonDao, api) as T
    }
}