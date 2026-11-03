package com.example.netflix

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TelaDownloads(viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaBuscar: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit) {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // Ao abrir a tela, busca no banco so os filmes baixados
    LaunchedEffect(Unit) {
        filmeViewModel.carregarBaixados(filmeDAO)
    }

    Scaffold(
        topBar = { BarraTopo("Downloads", irParaMenu) },
        bottomBar = { BarraInferior("downloads", viewModel.perfilAtual, irParaInicio, irParaBuscar, irParaMenu) }
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
                    text = "Filmes baixados (${filmeViewModel.baixados.size})",
                    color = CinzaTexto,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filmeViewModel.baixados.isEmpty()) {
                    // Lista vazia: explica como baixar em vez de deixar a tela em branco
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = CinzaCartao
                        )
                    ) {
                        Text(
                            text = "Nenhum filme baixado ainda. Abra um filme e toque em Baixar.",
                            color = Color(255, 255, 255),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(0.dp))
                }

                LazyColumn {

                    items(filmeViewModel.baixados) {
                        Column {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Row(
                                        modifier = Modifier.clickable(onClick = { irParaFilme(it.id) }),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.width(110.dp).height(70.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(62, 92, 118)
                                        ) {
                                            Capa(it.nome)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = it.nome,
                                                color = Color(255, 255, 255),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Baixado",
                                                color = Color(46, 158, 91),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable(onClick = { filmeViewModel.excluirDownload(it, filmeDAO) }),
                                        shape = RoundedCornerShape(18.dp),
                                        color = Color(255, 255, 255)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Excluir download"
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                }
            }
        }
    }
}
