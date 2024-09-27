package br.edu.up

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.up.ui.theme.MuchikinGameTheme
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain();
        }
    }
}

val jogadores = mutableStateListOf(Jogador(0, "Jogador 1"))

@Composable
fun LayoutMain() {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = "todos"){
        composable("todos") { TelaTodosJogadores(navController, jogadores) }
        composable("status/{jogadorJson}") { backStackEntry ->
            val jogadorJson = backStackEntry.arguments?.getString("jogadorJson");
            val jogador = Gson().fromJson(jogadorJson, Jogador::class.java)

            TelaStatusJogador(navController, jogador)
        }
        composable("salvar/{jogadorJson}") {
            backStackEntry ->
                val jogadorJson = backStackEntry.arguments?.getString("jogadorJson");
                val jogador = Gson().fromJson(jogadorJson, Jogador::class.java)


        }
    }
}

@Composable
fun TelaTodosJogadores(navController: NavController, jogadores: MutableList<Jogador>){

    val context = LocalContext.current;

    Column(
        modifier = Modifier.fillMaxSize().padding(15.dp).padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ){
            items(jogadores) {
                jogador ->

                JogadorCard(
                    jogador = jogador,
                    onLevelChange = { jogador.level = it.coerceIn(1..10) },
                    onClick = {
                        val jogadorJson = Gson().toJson(jogador);
                        navController.navigate("status/${jogadorJson}")
                    }
                )

            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp).padding(bottom = 60.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ){
        FloatingActionButton (
            onClick = {
                when{
                    jogadores.size < 6 -> {
                        jogadores += Jogador(jogadores.size,"Jogador ${jogadores.size + 1}")
                    }
                    jogadores.size == 6 ->{
                        Toast.makeText(context, "Não é possível ter mais de 6 jogadores", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        ) {
            Icon(Icons.Filled.Add, "Adicionar jogador.")
        }
    }

}

@Composable
fun JogadorCard(jogador: Jogador, onLevelChange: (Int) -> Unit, onClick: () -> Unit) {
    Card (
        onClick = {onClick()}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).padding(horizontal = 20.dp).height(35.dp)
        ){
            Text(text = "${jogador.nome}", fontSize = 25.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)

            Spacer(modifier = Modifier.weight(1f))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.size(150.dp, 30.dp)
            ){
                Column (
                    modifier = Modifier.fillMaxSize().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(text = "Poder Total: ${jogador.poderTotal}", fontSize = 20.sp, color = Color.Blue)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        val textSize = 15.sp;
        val textNumber = 17.sp;
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).padding(horizontal = 20.dp)
        ) {
            Text(text = "Level: ", fontSize = textSize)
            Text(text = jogador.level.toString(), fontSize = textNumber)

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Equipamento: ", fontSize = textSize)
            Text(text = jogador.bonusEquipamento.toString(), fontSize = textNumber)

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Modificadores: ", fontSize = textSize)
            Text(text = jogador.modificadores.toString(), fontSize = textNumber)
        }


    }
}

@Composable
fun TelaStatusJogador(navController: NavController, jogador: Jogador){
    val (nome, setNome) = remember { mutableStateOf(jogador.nome) }
    val (level, setLevel) = remember { mutableStateOf(jogador.level) }
    val (equipamento, setEquipamento) = remember { mutableStateOf(jogador.bonusEquipamento) }
    val (modificadores, setModificadores) = remember { mutableStateOf(jogador.modificadores) }
    val (poderTotal, setPoderTotal) = remember { mutableStateOf(level + equipamento + modificadores) }

    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    )
    {
        Spacer(modifier = Modifier.weight(0.5f))
        Row (modifier = Modifier.height(40.dp)){

            Button(onClick = {navController.popBackStack()}) {
                Text("Voltar")
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.size(150.dp, 40.dp)
            ){
                Column (
                    modifier = Modifier.fillMaxSize().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(text = "Poder Total: ${poderTotal}", fontSize = 20.sp, color = Color.Blue)
                }
            }

        }
        Spacer(modifier = Modifier.weight(2.5f))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = nome,
            onValueChange = {
                setNome(it)
                jogadores.set(jogador.id, jogadores.get(jogador.id).copy(nome = it))
            },
            label = { Text("Nome do Jogador") }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(text = "Level: $level", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = level.toFloat(),
            onValueChange = {
                setLevel(it.toInt())
                jogadores.set(jogador.id, jogadores.get(jogador.id).copy(level = it.toInt()))
                setPoderTotal(level + equipamento + modificadores)
            },
            valueRange = 0f..10f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(text = "Bônus de Equipamento: $equipamento", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = equipamento.toFloat(),
            onValueChange = {
                setEquipamento(it.toInt())
                jogadores.set(jogador.id, jogadores.get(jogador.id).copy(bonusEquipamento = it.toInt()))
                setPoderTotal(level + equipamento + modificadores)
            },
            valueRange = 0f..40f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(text = "Modificadores: $modificadores", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = modificadores.toFloat(),
            onValueChange = {
                setModificadores(it.toInt())
                jogadores.set(jogador.id, jogadores.get(jogador.id).copy(modificadores = it.toInt()))
                setPoderTotal(level + equipamento + modificadores)
            },
            valueRange = -5f..10f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
        Spacer(modifier = Modifier.weight(6f))
    }
}