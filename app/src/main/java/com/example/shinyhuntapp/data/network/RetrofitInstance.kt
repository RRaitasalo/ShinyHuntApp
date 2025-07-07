package com.example.shinyhuntapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitInstance {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // 👈 This tells Moshi how to read Kotlin data classes
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // 👈 Use this Moshi instance
        .build()

    val api = retrofit.create(PokeApiService::class.java)


    /*
    val api: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/") // Base URL for PokeAPI
            .addConverterFactory(MoshiConverterFactory.create()) // JSON parser
            .build()
            .create(PokeApiService::class.java) // Create implementation of API
    }*/
}