package com.example.netflix

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TelaEditarPerfil(viewModel: PerfilViewModel, voltar: () -> Unit, irParaPerfis: () -> Unit) {

    var campo by remember { mutableStateOf(viewModel.perfilAtual) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val perfilDAO = db.perfilDAO()

    Column(modifier = Modifier.fillMaxSize()) {

        BarraTopo("Editar perfil", voltar)

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Avatar(viewModel.perfilAtual, Color(232, 176, 4), Modifier.size(80.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nome do perfil",
                color = CinzaTexto,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = campo,
                onValueChange = { campo = it },
                placeholder = { Text("Nome") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ItemMenu("Identificador de jogos", {})
            Spacer(modifier = Modifier.height(8.dp))
            ItemMenu("Bloqueio de perfil", {})
            Spacer(modifier = Modifier.height(8.dp))
            ItemMenu("Idiomas de exibicao", {})
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(179, 179, 179)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "  Reproduzir proximo episodio",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Checkbox(
                        checked = viewModel.proximoEpisodio,
                        onCheckedChange = { viewModel.alternarProximoEpisodio() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(179, 179, 179)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "  Reproduzir previas",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Checkbox(
                        checked = viewModel.previas,
                        onCheckedChange = { viewModel.alternarPrevias() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Acao principal em vermelho; excluir fica em cinza para nao ser tocado por engano
            BotaoNetflix("Salvar", VermelhoNetflix, Modifier.fillMaxWidth()) {
                if (campo == "") {
                    Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.renomear(campo, perfilDAO)
                    Toast.makeText(context, "Perfil salvo: $campo", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            BotaoNetflix("Excluir perfil", Color(43, 43, 43), Modifier.fillMaxWidth()) {
                viewModel.excluir(perfilDAO)
                irParaPerfis()
            }
        }
    }
}
