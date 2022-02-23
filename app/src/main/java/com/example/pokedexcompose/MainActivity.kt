package com.example.pokedexcompose

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokedexcompose.ui.theme.PokedexComposeTheme
import java.net.URL

class MainActivity : ComponentActivity() {
    private val viewModel = ViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexComposeTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Pokedex") },
                        )
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(all = 12.dp),
                    ) {
                        OutlinedTextField(
                            value = viewModel.searchTerm,
                            onValueChange = { viewModel.searchTerm = it },
                            label = { Text("Search by name or id ") },
                            modifier = Modifier.padding(end = 4.dp),
                        )
                        Button(onClick = { viewModel.fetchPokemon() }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }

                }
            }
        }
    }
}

class ViewModel {
    var searchTerm: String by mutableStateOf("")
    var pokemon: Pokemon? by mutableStateOf(null)
    var sprite: Image? by mutableStateOf(null)

    fun fetchPokemon() {

    }
}

data class Pokemon(val name: String, val id: Int, val sprites: SpritePack)
data class SpritePack(val frontDefault: URL)