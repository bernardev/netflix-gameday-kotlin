package com.example.netflix.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.netflix.viewmodel.PerfilViewModel

@Composable
fun TelaMenu(viewModel: PerfilViewModel, voltar: () -> Unit, sair: () -> Unit, irParaEditar: () -> Unit, irParaDownloads: () -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        BarraTopo("Minha Netflix", voltar)

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            // Cartao do perfil atual, com borda vermelha
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CinzaCartao,
                border = BorderStroke(2.dp, VermelhoNetflix)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Avatar(uiState.perfilAtual, Color(232, 176, 4), Modifier.size(80.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.perfilAtual,
                        color = Color(255, 255, 255),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${uiState.perfis.size} perfis nesta conta",
                        color = CinzaTexto,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BotaoNetflix("Editar perfil", VermelhoNetflix, Modifier.fillMaxWidth()) {
                        viewModel.comecarEdicao()
                        irParaEditar()
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ItemMenu("Trocar de perfil", sair)
                ItemMenu("Downloads", irParaDownloads)
                ItemMenu("Configuracoes do aplicativo", {})
                ItemMenu("Ajuda", {})
                ItemMenu("Sair", sair)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Versao: 18.46.1 (17)",
                color = CinzaTexto,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ItemMenu(texto: String, aoClicar: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { aoClicar() }),
        shape = RoundedCornerShape(8.dp),
        color = Color(28, 28, 28)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = ">",
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
