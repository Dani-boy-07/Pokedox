package com.aa.android.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.aa.android.pokedex.api.entity.PokemonDTO
import com.aa.android.pokedex.model.UiState
import com.aa.android.pokedex.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val pokemonRepository: PokemonRepository ): ViewModel() {
    private val _pokemonData = MutableLiveData<PokemonDTO?>()
    val pokemonData = _pokemonData

    val pokemonLiveData: LiveData<UiState<List<String>>> = liveData(Dispatchers.IO) {
        emit(UiState.Loading())
        try {
            val data = pokemonRepository.getAllPokemon()
            emit(UiState.Ready(data))
        } catch (e: Exception) {
            Log.e(this@MainViewModel::class.simpleName, e.message, e)
            emit(UiState.Error(e))
        }
    }

    fun getPokemon(pokemonName:String){
        viewModelScope.launch {
            try {
                val pokemon = pokemonRepository.getPokemon(pokemonName)
                _pokemonData.value = pokemon;
                // Handle the retrieved Pokemon data, e.g., update LiveData or StateFlow
            } catch (e: Exception) {
                // Handle the error, e.g., update LiveData or StateFlow with an error message
            }
        }
    }
}