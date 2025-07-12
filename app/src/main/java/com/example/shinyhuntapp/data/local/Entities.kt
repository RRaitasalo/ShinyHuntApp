package com.example.shinyhuntapp.data.local

import androidx.room.*
import com.example.shinyhuntapp.data.local.HuntMethod

class Converters {
    @TypeConverter
    fun fromHuntMethod(huntMethod: HuntMethod): String {
        return huntMethod.displayName
    }

    @TypeConverter
    fun toHuntMethod(displayName: String): HuntMethod {
        return HuntMethod.fromDisplayName(displayName) ?: HuntMethod.RANDOM_ENCOUNTER
    }
}

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
    val type2: String?,
    val spriteUrl: String,
    val shinySprite: String
)

@Entity(
    tableName = "user_pokemon",
    foreignKeys = [
        ForeignKey(
            entity = Pokemon::class,
            parentColumns = ["id"],
            childColumns = ["pokemonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pokemonId"), Index("userId")]
)
data class UserPokemon(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val pokemonId: Int,
    val userId: Int,
    val hasCaughtShiny: Boolean,
    val caughtDate: Long? = null,
    val caughtCount: Int = 0,
    val isFromHunt: Boolean = false
)


// Game entity for Pokémon games
@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: Int,
    val name: String
)

@Entity(
    tableName = "hunts",
    foreignKeys = [
        ForeignKey(
            entity = Pokemon::class,
            parentColumns = ["id"],
            childColumns = ["pokemonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pokemonId"), Index("userId")]
)
data class Hunt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pokemonId: Int,
    val userId: Int,
    val method: String,
    val encounters: Int,
    val huntMethod: HuntMethod,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isFoundShiny: Boolean = false
)

object GuestUser {
    const val ID = -1
    const val USERNAME = "guest"
}