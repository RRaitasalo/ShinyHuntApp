package com.example.shinyhuntapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// User entity for account information
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String //Needs to be hashed later
)

// Pokémon entity for basic Pokémon information
@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey val id: Int,
    val name: String,
    val nationalDexNumber: Int,
    val type1: String,
    val type2: String?
)

// Game entity for Pokémon games
@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: Int,
    val name: String
)

// Junction table for Pokémon and Games (many-to-many relationship)
@Entity(tableName = "pokemonInGame", primaryKeys = ["pokemonId", "gameId"])
data class PokemonInGame(
    val pokemonId: Int,
    val gameId: Int
)

// Hunt entity for tracking shiny hunts
@Entity(tableName = "hunt")
data class Hunt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val pokemonId: Int,
    val method: String, // Hunting method, e.g., "WildEncounter"
    val encounters: Int, // Number of encounters
    val isFoundShiny: Boolean // Whether shiny was found
)