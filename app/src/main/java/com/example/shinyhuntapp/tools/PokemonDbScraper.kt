package com.example.shinyhuntapp.tools

import com.example.shinyhuntapp.data.local.GameAvailability
import com.example.shinyhuntapp.data.local.GameMasterData
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.TimeUnit

class PokemonDbScraper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun scrapeGameAvailability(pokemonId: Int): List<GameAvailability> {
        return try {
            val url = "https://pokemondb.net/pokedex/$pokemonId"
            val html = fetchHtml(url)
            val doc = Jsoup.parse(html)

            parseGameAvailability(doc, pokemonId)
        } catch (e: Exception) {
            println("PokemonScraper: Error scraping Pokemon $pokemonId: $e")
            emptyList()
        }
    }

    private suspend fun fetchHtml(url: String): String {
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build()

        val response = client.newCall(request).execute()
        delay(2000)

        return response.body?.string() ?: throw Exception("Empty response")
    }

    private fun parseGameAvailability(doc: Document, pokemonId: Int): List<GameAvailability> {
        val gameAvailabilityList = mutableListOf<GameAvailability>()

        val locationSection = doc.select("h2:contains(Where to find)").firstOrNull()?.parent()
        val table = locationSection?.select("table.vitals-table tbody tr")

        table?.forEach { row ->
            val gameElements = row.select("th span.igame")
            val locationElement = row.select("td").firstOrNull()

            if (gameElements.isNotEmpty() && locationElement != null) {
                val gameNames = gameElements.map { it.text() }
                val (locations, method) = parseLocationInfo(locationElement)

                gameNames.forEach { gameName ->
                    val gameId = GameMasterData.getGameIdByName(gameName)
                    if (gameId != null) {
                        gameAvailabilityList.add(
                            GameAvailability(
                                pokemonId = pokemonId,
                                gameId = gameId,
                                locations = locations,
                                obtainMethod = method
                            )
                        )
                    } else {
                        println("PokemonScraper: Unknown game: $gameName for Pokemon $pokemonId")
                    }
                }
            }
        }
        return gameAvailabilityList
    }

    private fun parseLocationInfo(locationElement: Element): Pair<String, String> {
        val text = locationElement.text()
        val links = locationElement.select("a")

        val method = when {
            text.contains("Trade", ignoreCase = true) -> "trade"
            text.contains("Evolve", ignoreCase = true) -> "evolve"
            text.contains("gift", ignoreCase = true) -> "breed"
            links.isEmpty() -> "special"
            else -> "wild"
        }

        val locations = when {
            method == "evolve" -> {
                val pokemonLinks = locationElement.select("a[href*='/pokedex/']")
                val immediatePreEvolution = pokemonLinks.last()?.text()
                listOf("Evolve $immediatePreEvolution")
            }
            method == "breed" -> {
                val pokemonLinks = locationElement.select("a[href*='/pokedex/']")
                val breededPokemon = pokemonLinks.first()?.text()
                listOf("Breed $breededPokemon")
            }
            links.isNotEmpty() -> {
                links.map { it.text() }.toList()
            } else -> {
                listOf(text)
            }
        }
        val locationsJson = gson.toJson(locations)

        return Pair(locationsJson, method)
    }

    companion object {
        private val gson = GsonBuilder().create()
    }
}