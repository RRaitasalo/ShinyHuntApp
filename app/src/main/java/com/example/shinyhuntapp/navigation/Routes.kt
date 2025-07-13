package com.example.shinyhuntapp.navigation

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val REGISTER = "register"
    const val POKEMON_LIST = "pokemon_list"
    const val DEV_TOOLS = "dev_tools"
    const val POKEMON_INFO = "pokemon_info"
    const val HUNT = "hunt"

    fun pokemonInfoWithId(pokemonId: Int) = "$POKEMON_INFO/$pokemonId"
    fun huntWithPokemonId(pokemonId: Int) = "$HUNT/$pokemonId"
}