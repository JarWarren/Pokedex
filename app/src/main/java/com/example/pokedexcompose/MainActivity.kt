package com.example.pokedexcompose

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokedexcompose.ui.theme.PokedexComposeTheme
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class MainActivity : ComponentActivity() {
    private val viewModel = ViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexComposeTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppBar(
                        title = { Text("Pokedex") },
                    )
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
                    viewModel.sprite?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Pokemon sprite",
                            modifier = Modifier
                                .size(240.dp)
                                .shadow(2.dp)
                        )
                    }
                    viewModel.pokemon?.let {
                        Text(
                            it.name.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            it.id.toString(),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

class ViewModel {
    var searchTerm: String by mutableStateOf("")
    var pokemon: Pokemon? by mutableStateOf(null)
    var sprite: ImageBitmap? by mutableStateOf(null)

    fun fetchPokemon() {
        if (searchTerm.isEmpty()) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("https://pokeapi.co/api/v2/pokemon/" + searchTerm.lowercase())
            val connection = url.openConnection()
            try {
                connection.connect()
                val inputStreamReader = InputStreamReader(connection.getInputStream())
                val bufferedReader = BufferedReader(inputStreamReader)
                val builder = StringBuilder()
                var line = bufferedReader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = bufferedReader.readLine()
                }
                bufferedReader.close()

                val json = JSONObject(builder.toString())
                val fetchedPokemon = Pokemon(json)

                fetchSprite(fetchedPokemon.spriteURL)
                pokemon = fetchedPokemon
            } catch (exception: Exception) {
                exception.printStackTrace()
                pokemon = null
                sprite = null
            }
        }
    }

    private fun fetchSprite(url: URL) {
        val connection = url.openConnection()
        sprite = try {
            BitmapFactory.decodeStream(connection.getInputStream()).asImageBitmap()
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }
}

data class Pokemon(val json: JSONObject) {
    val name: String = json.getString("name")
    val id: Int = json.getInt("id")
    val spriteURL: URL = URL(
        json
            .getJSONObject("sprites")
            .getString("front_default")
    )
}