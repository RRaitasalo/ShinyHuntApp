package com.example.shinyhuntapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Pokemon::class,
        UserPokemon::class,
        Game::class,
        Hunt::class
        ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pokemonDao(): PokemonDao
    abstract fun userPokemonDao(): UserPokemonDao
    abstract fun gameDao(): GameDao
    abstract fun huntDao(): HuntDao
}
