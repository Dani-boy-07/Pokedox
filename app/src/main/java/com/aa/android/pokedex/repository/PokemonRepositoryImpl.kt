package com.aa.android.pokedex.repository

import com.aa.android.pokedex.api.PokemonApi
import com.aa.android.pokedex.api.entity.PokemonDTO
import javax.inject.Inject

class PokemonRepositoryImpl @Inject
constructor(private val pokemonApi: PokemonApi) : PokemonRepository {
    override suspend fun getAllPokemon(): List<String> {
        val response = pokemonApi.getAllPokemon()
        if (response.isSuccessful) {
            response.body()?.let {
                return it.results.map { result ->
                    result.name
                }
            }
        }
        return listOf()
    }

    override suspend fun getPokemon(pokemonName: String): PokemonDTO? {
        val response = pokemonApi.getPokemon(pokemonName)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}