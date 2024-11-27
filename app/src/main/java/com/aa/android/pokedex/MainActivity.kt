package com.aa.android.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.aa.android.pokedex.api.entity.PokemonDTO
import com.aa.android.pokedex.model.UiState
import com.aa.android.pokedex.viewmodel.MainViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            NavigationBar(
                mainViewModel = mainViewModel,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun NavigationBar(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        startDestination = "Home",
        navController = navHostController
    ) {
        composable("Home") {
            Screen(mainViewModel.pokemonLiveData) { pokemonName ->
                navHostController.navigate(
                    "Details/${pokemonName}"
                )
            }
        }
        composable(
            route = "Details/{pokemonName}",
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { currentBackStack ->
            val pokemonName = currentBackStack?.arguments?.getString("pokemonName")
            LaunchedEffect(pokemonName) {
                pokemonName?.let {
                    mainViewModel.getPokemon(it)
                }
            }

            val pokemonData by mainViewModel.pokemonData.observeAsState()

            DetailScreen(pokemonData)
        }

    }
}

//- > Written in Kotlin
//- > Adheres to an MVVM architecture
//- > Uses Dependency Injection to supply dependencies
//- > Makes an API call to fetch the details about the selected Pokémon. Endpoint: `/pokemon/{id or name}`
//- > Displays the following items in a well-designed layout (traditional or Compose):
//- > An image of the Pokémon (preferably the Dream World image, JSON key: `sprites.other.dream_world.front_default`)
//- > Name (capitalized)
//- > Height (with appropriate units)
//- > Weight (with appropriate units)
//- > Type(s) (preferably with the appropriate background color as defined in `Type.kt`)
//- > Stats - hp, attack, defense, etc. (name and value)
@Composable
fun DetailScreen(
    pokemon: PokemonDTO?
) {
    Scaffold(topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.primary, title = {
            Image(painter = painterResource(id = R.drawable.pokemon_logo), null)
        })
    }) { it ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pokemon?.name.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = pokemon?.sprites?.defaultFront,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = ""
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Height", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(pokemon?.height.toString() + "cm", fontSize = 18.sp)
                    }
                    Column {
                        Text("Weight", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(pokemon?.weight.toString() + "lb", fontSize = 18.sp)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                ) {
                    Text(text = "Types", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pokemon?.types?.forEach {
                            Text(text = it.type.name)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Stats",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    pokemon?.stats?.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = it.stat.name,
                                color = Color.Black,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .weight(.5f)
                                    .padding(end = 10.dp)
                            )
                            Row (
                                modifier = Modifier
                                    .weight(.5f)
                                    .height(20.dp)
                                    .background(color = Color.LightGray)
                            ){
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((it.baseStat).dp)
                                        .background(color = colorResource(R.color.typeFlying))
                                ) {
                                    Text(
                                        text = it.baseStat.toString(),
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun Screen(pokemon: LiveData<UiState<List<String>>>, onPokemonClick: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.primary, title = {
            Image(painter = painterResource(id = R.drawable.pokemon_logo), null)
        })
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colors.background
        ) {
            PokemonList(pokemon = pokemon) { it ->
                onPokemonClick(it)
            }
        }
    }
}

@Composable
fun PokemonList(pokemon: LiveData<UiState<List<String>>>, onPokemonClick: (String) -> Unit) {
    val uiState: UiState<List<String>>? by pokemon.observeAsState()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        uiState?.let {
            when (it) {
                is UiState.Loading -> {
                    items(20) {
                        PokemonItem(pokemon = "", isLoading = true, onPokemonClick = {})
                    }
                }

                is UiState.Ready -> {
                    items(it.data) { pkmn ->
                        PokemonItem(pokemon = pkmn, isLoading = false) { it ->
                            onPokemonClick(it)
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            textAlign = TextAlign.Center,
                            text = "Error loading list. Please try again later.",
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PokemonItem(pokemon: String, isLoading: Boolean, onPokemonClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            onPokemonClick(pokemon)
        }
    ) {
        Text(
            text = pokemon.capitalize(Locale.current),
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    PokedexTheme {
//        Screen(MutableLiveData(UiState.Ready(listOf("one", "two", "three")))),
//    }{
//
//    }
//}