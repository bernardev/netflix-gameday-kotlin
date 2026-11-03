package com.example.netflix

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaFilme(id: Int, viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaBuscar: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit, voltar: () -> Unit) {

    var novoNome by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // O filme desta tela vem direto do banco, pelo id recebido na rota "filme/{id}"
    var filme by remember { mutableStateOf(Filme(0, "", false)) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            filme = buscarFilmePorId(id, filmeDAO)
        }
        filmeViewModel.carregar(filmeDAO)
    }

    // Descobre se o perfil atual e admin (so admin pode renomear e excluir filmes)
    var ehAdmin = false
    viewModel.nomesAdmin.forEach {
        if (it == viewModel.perfilAtual) {
            ehAdmin = true
        }
    }

    // A tela e dividida em secoes dentro de uma LazyColumn, para o conteudo rolar
    // e nada ficar cortado em telas menores (ex.: perfil admin, que tem mais campos)
    val secoes = listOf("capa", "acoes", "admin", "recomendacoes")

    Scaffold(
        topBar = { BarraTopo("Detalhes do filme", voltar) },
        bottomBar = { BarraInferior("filme", viewModel.perfilAtual, irParaInicio, irParaBuscar, irParaMenu) }
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn {

                    items(secoes) { secao ->

                        if (secao == "capa") {
                            // Capa grande com o nome do filme por cima (mesmo estilo do banner da tela Inicio)
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(220.dp),
                                color = Color(62, 92, 118)
                            ) {
                                Capa(filme.nome)
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0, 0, 0, 120)
                                ) {

                                }
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Column {
                                        Text(
                                            text = filme.nome,
                                            color = Color(255, 255, 255),
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (filme.baixado) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(46, 158, 91)
                                            ) {
                                                Text(
                                                    text = "Baixado",
                                                    color = Color(255, 255, 255),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.height(0.dp))
                                        }
                                    }
                                }
                            }
                        }

                        if (secao == "acoes") {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                BotaoBanner("▶ Assistir", Modifier.fillMaxWidth())

                                if (filme.baixado) {
                                    BotaoNetflix("✓ Ja baixado", Color(43, 43, 43), Modifier.fillMaxWidth()) {
                                        Toast.makeText(context, "Este filme ja esta nos seus downloads", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    BotaoNetflix("↓ Baixar", Color(43, 43, 43), Modifier.fillMaxWidth()) {
                                        filmeViewModel.baixar(filme, filmeDAO)
                                        filme = Filme(filme.id, filme.nome, true)
                                        Toast.makeText(context, "Download concluido", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                Text(
                                    text = "Sinopse",
                                    color = Color(255, 255, 255),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Assista ${filme.nome} na Netflix. Baixe para ver sem internet.",
                                    color = CinzaTexto,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        if (secao == "admin") {
                            if (ehAdmin) {
                                // Area do administrador separada em um cartao com borda vermelha
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = CinzaCartao
                                    ),
                                    border = BorderStroke(1.dp, VermelhoNetflix)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "Area do administrador",
                                            color = Color(255, 255, 255),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TextField(
                                            modifier = Modifier.fillMaxWidth(),
                                            value = novoNome,
                                            onValueChange = { novoNome = it },
                                            placeholder = { Text("Novo nome do filme") }
                                        )
                                        BotaoNetflix("Salvar nome", VermelhoNetflix, Modifier.fillMaxWidth()) {
                                            if (novoNome == "") {
                                                Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                                            } else {
                                                filmeViewModel.renomear(filme, novoNome, filmeDAO)
                                                filme = Filme(filme.id, novoNome, filme.baixado)
                                                novoNome = ""
                                            }
                                        }
                                        BotaoNetflix("Excluir filme", Color(43, 43, 43), Modifier.fillMaxWidth()) {
                                            filmeViewModel.excluir(filme, filmeDAO)
                                            voltar()
                                        }
                                    }
                                }
                            }
                        }

                        if (secao == "recomendacoes") {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    text = "Recomendacoes",
                                    color = Color(255, 255, 255),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Cartaz("O Mentalista", Color(140, 28, 19)) {
                                        abrirFilme("O Mentalista", filmeViewModel, context, irParaFilme)
                                    }
                                    Cartaz("The Vampire Diaries", Color(46, 42, 79)) {
                                        abrirFilme("The Vampire Diaries", filmeViewModel, context, irParaFilme)
                                    }
                                    Cartaz("Bird Box", Color(75, 96, 67)) {
                                        abrirFilme("Bird Box", filmeViewModel, context, irParaFilme)
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}
