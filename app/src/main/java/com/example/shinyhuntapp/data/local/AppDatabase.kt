package com.example.shinyhuntapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        User::class,
        Pokemon::class,
        UserPokemon::class,
        Game::class,
        Hunt::class
        ],
    version = 3 // Increment this when the schema is changed
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pokemonDao(): PokemonDao
    abstract fun userPokemonDao(): UserPokemonDao
    abstract fun gameDao(): GameDao
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
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
