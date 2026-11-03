package com.example.netflix

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TelaBuscar(viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit) {

    var busca by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // Ao abrir a tela, busca os filmes no banco
    LaunchedEffect(Unit) {
        filmeViewModel.carregar(filmeDAO)
    }

    Scaffold(
        bottomBar = { BarraInferior("buscar", viewModel.perfilAtual, irParaInicio, {}, irParaMenu) }
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Buscar",
                    color = Color(255, 255, 255),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        modifier = Modifier.width(240.dp),
                        value = busca,
                        onValueChange = { busca = it },
                        placeholder = { Text("Buscar ou adicionar") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BotaoNetflix("Add", VermelhoNetflix, Modifier.width(80.dp)) {
                        if (busca == "") {
                            Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                        } else {
                            filmeViewModel.adicionar(busca, filmeDAO)
                            busca = ""
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filme recomendado em destaque, dentro de um cartao
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CinzaCartao
                    ),
                    border = BorderStroke(1.dp, VermelhoNetflix)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.width(110.dp).height(80.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(122, 122, 122)
                        ) {
                            Capa("A lista de Schindler")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Recomendado para voce",
                                color = CinzaTexto,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "A lista de Schindler",
                                color = Color(255, 255, 255),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Todos os filmes (${filmeViewModel.filmes.size})",
                    color = Color(255, 255, 255),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {

                    items(filmeViewModel.filmes) {
                        Column {
                            ItemFilme(it.nome) {
                                irParaFilme(it.id)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                }
            }
        }
    }
}

// Item da lista de filmes: cartao escuro com a capa, o nome e uma seta
@Composable
fun ItemFilme(nomeFilme: String, aoClicar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { aoClicar() }),
        colors = CardDefaults.cardColors(
            containerColor = CinzaCartao
        ),
        border = BorderStroke(1.dp, CinzaBorda)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.width(110.dp).height(70.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(62, 92, 118)
                ) {
                    Capa(nomeFilme)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = nomeFilme,
                    color = Color(255, 255, 255),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = ">",
                color = CinzaTexto,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
