package com.example.netflix

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun TelaPerfis(viewModel: PerfilViewModel, irParaInicio: () -> Unit) {

    var novoPerfil by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val perfilDAO = db.perfilDAO()

    // Ao abrir a tela, busca os perfis no banco
    LaunchedEffect(Unit) {
        viewModel.carregar(perfilDAO)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            color = Color(46, 125, 50)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Time("SF", Color(170, 0, 0))
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "VS",
                    color = Color(255, 255, 255),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(20.dp))
                Time("LA", Color(200, 140, 0))
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(229, 9, 20)
            ) {
                Text(
                    text = "NETFLIX",
                    color = Color(255, 255, 255),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Text(
                text = "GAMEDAY",
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escolha o seu perfil",
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier = Modifier.width(220.dp),
                    value = novoPerfil,
                    onValueChange = { novoPerfil = it },
                    placeholder = { Text("Novo perfil") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                BotaoNetflix("Add", VermelhoNetflix, Modifier.width(80.dp)) {
                    if (novoPerfil == "") {
                        Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.adicionar(novoPerfil, perfilDAO)
                        novoPerfil = ""
                    }
                }
            }
        }

        // Cada perfil e um cartao escuro; o perfil atual ganha borda vermelha
        Column(modifier = Modifier.padding(16.dp)) {
            LazyColumn {

                items(viewModel.perfis) {
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        viewModel.selecionar(it)
                                        irParaInicio()
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = CinzaCartao
                            ),
                            border = if (it.nome == viewModel.perfilAtual) BorderStroke(2.dp, VermelhoNetflix) else BorderStroke(1.dp, CinzaBorda)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Avatar(
                                        it.nome,
                                        if (it.nome == viewModel.perfilAtual) Color(232, 176, 4) else Color(74, 107, 138),
                                        Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = it.nome,
                                        color = Color(255, 255, 255),
                                        style = MaterialTheme.typography.titleLarge,
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
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

            }
        }
    }
}

@Composable
fun Time(sigla: String, cor: Color) {
    Surface(
        modifier = Modifier.size(96.dp),
        shape = RoundedCornerShape(48.dp),
        color = cor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = sigla,
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Avatar(nome: String, cor: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = cor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil"
            )
        }
        FotoPerfil(nome)
    }
}

@Composable
fun ItemPerfil(nome: String, cor: Color, modifier: Modifier, aoClicar: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = { aoClicar() })
    ) {
        Avatar(nome, cor, modifier)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = nome,
            color = Color(255, 255, 255),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
