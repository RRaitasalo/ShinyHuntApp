package com.example.shinyhuntapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shinyhuntapp.data.PreferenceManager
import com.example.shinyhuntapp.data.local.DatabaseProvider
import com.example.shinyhuntapp.data.local.Hunt
import com.example.shinyhuntapp.data.local.HuntDao
import com.example.shinyhuntapp.data.local.HuntMethod
import com.example.shinyhuntapp.data.local.Pokemon
import com.example.shinyhuntapp.data.local.PokemonDao
import com.example.shinyhuntapp.data.local.UserPokemon
import com.example.shinyhuntapp.data.local.UserPokemonDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HuntViewModel(
    private val huntDao: HuntDao,
    private val pokemonDao: PokemonDao,
    private val userPokemonDao: UserPokemonDao,
    private val preferences: PreferenceManager
): ViewModel() {

    private val _currentHunt = MutableStateFlow<Hunt?>(null)
    val currentHunt: StateFlow<Hunt?> = _currentHunt

    private val _currentPokemon = MutableStateFlow<Pokemon?>(null)
    val currentPokemon: StateFlow<Pokemon?> = _currentPokemon

    private val _encounters = MutableStateFlow(0)
    val encounters: StateFlow<Int> = _encounters

    private val _incrementAmount = MutableStateFlow(1)
    val incrementAmount: StateFlow<Int> = _incrementAmount

    private val _isCustomAmount = MutableStateFlow(false)
    val isCustomAmount: StateFlow<Boolean> = _isCustomAmount

    private val _huntMethod = MutableStateFlow<HuntMethod>(HuntMethod.RANDOM_ENCOUNTER)
    val huntMethod: StateFlow<HuntMethod> = _huntMethod

    private val _allHunts = MutableStateFlow<List<Hunt>>(emptyList())
    val allHunts: StateFlow<List<Hunt>> = _allHunts

    private val _huntForPokemon = MutableStateFlow<Hunt?>(null)
    val huntForPokemon: StateFlow<Hunt?> = _huntForPokemon

    fun setIncrementAmount(amount: Int) {
        _incrementAmount.value = amount

        val presetAmounts = listOf(1, 2, 5, 15)
        _isCustomAmount.value = amount !in presetAmounts
    }

    fun setIsCustomAmount(isCustom: Boolean) {
        _isCustomAmount.value = isCustom
    }

    fun setHuntMethod(method: HuntMethod) {
        _huntMethod.value = method
    }

    fun startNewHunt(pokemonId: Int) {
        viewModelScope.launch {
            try {
                val pokemon = pokemonDao.getPokemonById(pokemonId)
                if (pokemon != null) {
                    val userId = preferences.getLoggedInUserId()

                    // Check if there's already an active hunt for this Pokemon
                    val existingHunt = huntDao.getHuntByUserAndPokemon(userId, pokemonId)

                    if (existingHunt != null) {
                        // Load existing hunt
                        _currentHunt.value = existingHunt
                        _encounters.value = existingHunt.encounters
                        _huntMethod.value = existingHunt.method
                    } else {
                        val newHunt = Hunt(
                            pokemonId = pokemonId,
                            pokemon = pokemon,
                            userId = userId,
                            method = _huntMethod.value,
                        )
                        huntDao.insertHunt(newHunt)
                        _currentHunt.value = newHunt.copy(id = newHunt.id)
                        _encounters.value = 0
                    }

                    _currentPokemon.value = pokemon
                }
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error starting hunt", e)
            }
        }
    }

    fun addEncounter() {
        val currentHunt = _currentHunt.value ?: return
        val amount = _incrementAmount.value

        viewModelScope.launch {
            try {
                huntDao.addEncounters(currentHunt.id, amount)
                _encounters.value = _encounters.value + amount

                _currentHunt.value = currentHunt.copy(encounters = _encounters.value)
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error adding encounter", e)
            }
        }
    }

    fun saveHunt() {
        val currentHunt = _currentHunt.value ?: return

        viewModelScope.launch {
            try {
                val updatedHunt = currentHunt.copy(
                    method = _huntMethod.value,
                )
                huntDao.updateHunt(updatedHunt)
                _currentHunt.value = updatedHunt
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error saving hunt", e)
            }
        }
    }

    fun completeHunt(onSuccess: () -> Unit) {
        val currentHunt = _currentHunt.value ?: return
        val currentPokemon = _currentPokemon.value ?: return

        viewModelScope.launch {
            try {
                val userId = preferences.getLoggedInUserId()

                val completedHunt = currentHunt.copy(
                    isFoundShiny = true,
                    endDate = System.currentTimeMillis(),
                    method = _huntMethod.value,
                )
                huntDao.updateHunt(completedHunt)

                val existingUserPokemon = userPokemonDao.getUserPokemon(userId, currentPokemon.id)
                if (existingUserPokemon == null) {
                    val newUserPokemon = UserPokemon(
                        pokemonId = currentPokemon.id,
                        userId = userId,
                        hasCaughtShiny = true,
                        caughtDate = System.currentTimeMillis(),
                        caughtCount = 1,
                        isFromHunt = true
                    )
                    userPokemonDao.insert(newUserPokemon)
                } else {
                    val updated = existingUserPokemon.copy(
                        hasCaughtShiny = true,
                        caughtDate = existingUserPokemon.caughtDate ?: System.currentTimeMillis(),
                        caughtCount = existingUserPokemon.caughtCount + 1,
                        isFromHunt = true
                    )
                    userPokemonDao.updateUserPokemon(updated)
                }

                _currentHunt.value = null
                _currentPokemon.value = null
                _encounters.value = 0

                onSuccess()
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error completing hunt", e)
            }
        }
    }

    fun getAllHunts() {
        viewModelScope.launch {
            try {
                val userId = preferences.getLoggedInUserId()
                val allHunts = huntDao.getAllHunts(userId)
                _allHunts.value = allHunts
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error getting all hunts", e)
            }
        }
    }

    fun getHuntForPokemon(pokemonId: Int) {
        viewModelScope.launch {
            try {
                val userId = preferences.getLoggedInUserId()
                val hunt = huntDao.getHuntByUserAndPokemon(userId, pokemonId)
                _huntForPokemon.value = hunt
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error getting hunt for pokemon", e)
            }
        }
    }

    fun deleteHunt(onSuccess: () -> Unit) {
        val currentHunt = _currentHunt.value ?: return

        viewModelScope.launch {
            try {
                huntDao.deleteHunt(currentHunt)

                _currentHunt.value = null
                _currentPokemon.value = null
                _encounters.value = 0

                onSuccess()
            } catch (e: Exception) {
                Log.e("HuntViewModel", "Error deleting hunt", e)
            }
        }
    }
}

class HuntViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        val preferences = PreferenceManager(context)

        return HuntViewModel(
            huntDao = db.huntDao(),
            pokemonDao = db.pokemonDao(),
            userPokemonDao = db.userPokemonDao(),
            preferences = preferences
        ) as T
    }
}