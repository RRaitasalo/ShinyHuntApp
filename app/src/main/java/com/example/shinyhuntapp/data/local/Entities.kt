package com.example.shinyhuntapp.data.local

import androidx.room.*
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromHuntMethod(huntMethod: HuntMethod): String {
        return huntMethod.displayName
    }

    @TypeConverter
    fun toHuntMethod(displayName: String): HuntMethod {
        return HuntMethod.fromDisplayName(displayName) ?: HuntMethod.RANDOM_ENCOUNTER
    }

    @TypeConverter
    fun fromPokemon(pokemon: Pokemon): String {
        return Gson().toJson(pokemon)
    }

    @TypeConverter
    fun toPokemon(pokemonJson: String): Pokemon {
        return Gson().fromJson(pokemonJson, Pokemon::class.java)
    }
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String //Needs to be hashed later
)

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


@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val generation: Int? = null,
    val region: String? = null
)

@Entity(
    tableName = "game_availability",
    foreignKeys = [
        ForeignKey(
            entity = Pokemon::class,
            parentColumns = ["id"],
            childColumns = ["pokemonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["pokemonId"]),
        Index(value = ["gameId"]),
        Index(value = ["pokemonId", "gameId"], unique = true)
    ]
)
data class GameAvailability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pokemonId: Int,
    val gameId: Int,
    val locations: String,
    val obtainMethod: String
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pokemonId: Int,
    val userId: Int,
    val pokemon: Pokemon,
    val encounters: Int = 0,
    val method: HuntMethod,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isFoundShiny: Boolean = false
)

object GuestUser {
    const val ID = -1
    const val USERNAME = "guest"
}