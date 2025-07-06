package com.example.shinyhuntapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Pokemon::class,
        Game::class,
        PokemonInGame::class,
        Hunt::class
        ],
    version = 2 // Increment this when the schema is changed
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pokemonDao(): PokemonDao
    abstract fun gameDao(): GameDao
    abstract fun pokemonGameDao(): PokemonInGameDao
    abstract fun huntDao(): HuntDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            prePopulateData(context, getDatabase(context))
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prePopulateData(context: Context, db: AppDatabase) {
            val json = context.assets.open("pokemon.json").bufferedReader().use { it.readText() }
            val pokemonDataList = Gson().fromJson(json, Array<PokemonData>::class.java)

            val gameNames = pokemonDataList.flatMap { it.games }.distinct()
            val games = gameNames.mapIndexed { index, name -> Game(id = index + 1, name = name) }
            db.gameDao().insertGames(games)
            val gameMap = games.associateBy { it.name }

            val pokemons = pokemonDataList.map { data ->
                Pokemon(id = data.id, name = data.name, description = data.description, type = data.type)
            }
            db.pokemonDao().insertPokemons(pokemons)

            val pokemonGames = pokemonDataList.flatMap { data ->
                data.games.map { gameName ->
                    PokemonInGame(pokemonId = data.id, gameId = gameMap[gameName]!!.id)
                }
            }
            db.pokemonGameDao().insertPokemonGames(pokemonGames)
        }
    }
}

// Data class to match JSON structure
data class PokemonData(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val games: List<String>
)
