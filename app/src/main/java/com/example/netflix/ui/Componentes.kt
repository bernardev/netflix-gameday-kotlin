package com.example.netflix

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Cores usadas em todas as telas, para o app ficar com a mesma cara do comeco ao fim
val VermelhoNetflix = Color(229, 9, 20)
val CinzaCartao = Color(28, 28, 28)
val CinzaBorda = Color(60, 60, 60)
val CinzaTexto = Color(179, 179, 179)

// Barra superior com titulo e botao de voltar (mesmo modelo do exemplo de TopAppBar da Aula 13)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTopo(titulo: String, voltar: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = voltar) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color(255, 255, 255),
            navigationIconContentColor = Color(255, 255, 255)
        )
    )
}

// Botao no vermelho da Netflix, feito com Surface + clickable
@Composable
fun BotaoNetflix(texto: String, cor: Color, modifier: Modifier, aoClicar: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = { aoClicar() }),
        shape = RoundedCornerShape(6.dp),
        color = cor,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = texto,
                color = Color(255, 255, 255),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
