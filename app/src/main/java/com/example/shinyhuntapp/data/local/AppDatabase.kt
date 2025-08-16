package com.example.shinyhuntapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        User::class,
        Pokemon::class,
        UserPokemon::class,
        Game::class,
        Hunt::class,
        GameAvailability::class
        ],
    version = 10
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pokemonDao(): PokemonDao
    abstract fun userPokemonDao(): UserPokemonDao
    abstract fun gameDao(): GameDao
    abstract fun huntDao(): HuntDao
    abstract fun gameAvailabilityDao(): GameAvailabilityDao
}
