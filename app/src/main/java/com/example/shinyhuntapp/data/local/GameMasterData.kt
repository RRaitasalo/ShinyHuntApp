package com.example.shinyhuntapp.data.local

object GameMasterData {
    val GAMES = listOf(
        Game(1, "Red", 1, "Kanto"),
        Game(2, "Blue", 1, "Kanto"),
        Game(3, "Yellow", 1, "Kanto"),
        Game(4, "Gold", 2, "Johto"),
        Game(5, "Silver", 2, "Johto"),
        Game(6, "Crystal", 2, "Johto"),
        Game(7, "Ruby", 3, "Hoenn"),
        Game(8, "Sapphire", 3, "Hoenn"),
        Game(9, "Emerald", 3, "Hoenn"),
        Game(10, "FireRed", 3, "Kanto"),
        Game(11, "LeafGreen", 3, "Kanto"),
        Game(12, "Diamond", 4, "Sinnoh"),
        Game(13, "Pearl", 4, "Sinnoh"),
        Game(14, "Platinum", 4, "Sinnoh"),
        Game(15, "HeartGold", 4, "Johto"),
        Game(16, "SoulSilver", 4, "Johto"),
        Game(17, "Black", 5, "Unova"),
        Game(18, "White", 5, "Unova"),
        Game(19, "Black 2", 5, "Unova"),
        Game(20, "White 2", 5, "Unova"),
        Game(21, "X", 6, "Kalos"),
        Game(22, "Y", 6, "Kalos"),
        Game(23, "Omega Ruby", 6, "Hoenn"),
        Game(24, "Alpha Sapphire", 6, "Hoenn"),
        Game(25, "Sun", 7, "Alola"),
        Game(26, "Moon", 7, "Alola"),
        Game(27, "Ultra Sun", 7, "Alola"),
        Game(28, "Ultra Moon", 7, "Alola"),
        Game(29, "Let's Go Pikachu", 7, "Kanto"),
        Game(30, "Let's Go Eevee", 7, "Kanto"),
        Game(31, "Sword", 8, "Galar"),
        Game(32, "Shield", 8, "Galar"),
        Game(33, "Brilliant Diamond", 8, "Sinnoh"),
        Game(34, "Shining Pearl", 8, "Sinnoh"),
        Game(35, "Legends: Arceus", 8, "Hisui"),
        Game(36, "Scarlet", 9, "Paldea"),
        Game(37, "Violet", 9, "Paldea")
    )

    fun getGameByName(name: String): Game? {
        return GAMES.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getGameIdByName(name: String): Int? {
        return getGameByName(name)?.id
    }
}