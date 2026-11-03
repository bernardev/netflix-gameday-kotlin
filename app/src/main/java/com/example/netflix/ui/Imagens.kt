package com.example.netflix

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// EXCECAO COMBINADA: Image, painterResource e ContentScale nao foram apresentados nas aulas.
// Sao usados so neste arquivo, para mostrar as fotos da pasta res/drawable.
@Composable
fun Foto(imagem: Int) {
    Image(
        painter = painterResource(imagem),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

// Filme sem capa cadastrada (novo ou renomeado) nao desenha nada e aparece o bloco de cor que esta por baixo.
@Composable
fun Capa(nome: String) {
    if (nome == "Outer Banks") { Foto(R.drawable.capa_outer_banks) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "O Mentalista") { Foto(R.drawable.capa_mentalista) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "The Vampire Diaries") { Foto(R.drawable.capa_vampire_diaries) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Bird Box") { Foto(R.drawable.capa_bird_box) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Seven") { Foto(R.drawable.capa_seven) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Clube da luta") { Foto(R.drawable.capa_clube_da_luta) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Interestelar") { Foto(R.drawable.capa_interestelar) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Matrix") { Foto(R.drawable.capa_matrix) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "GRAND THEFT AUTO") { Foto(R.drawable.capa_gta) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "A lista de Schindler") { Foto(R.drawable.capa_schindler) } else { Spacer(modifier = Modifier.height(0.dp)) }
}

// Perfil sem foto cadastrada (novo ou renomeado) nao desenha nada e aparece o icone padrao que esta por baixo.
@Composable
fun FotoPerfil(nome: String) {
    if (nome == "Manu") { Foto(R.drawable.perfil_manu) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Rsjr") { Foto(R.drawable.perfil_rsjr) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "buarda") { Foto(R.drawable.perfil_buarda) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "lau") { Foto(R.drawable.perfil_lau) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Gabi") { Foto(R.drawable.perfil_gabi) } else { Spacer(modifier = Modifier.height(0.dp)) }
    if (nome == "Admin") { Foto(R.drawable.perfil_admin) } else { Spacer(modifier = Modifier.height(0.dp)) }
}
