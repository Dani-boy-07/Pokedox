package com.aa.android.pokedex.repository

import com.aa.android.pokedex.api.entity.PokemonDTO

interface PokemonRepository{
    suspend fun getAllPokemon():List<String>
    suspend fun getPokemon(pokemonName:String): PokemonDTO?
}