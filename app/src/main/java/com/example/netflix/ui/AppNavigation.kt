package com.example.netflix.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.netflix.data.repository.FilmeRepository
import com.example.netflix.data.repository.PerfilRepository
import com.example.netflix.viewmodel.FilmeViewModel
import com.example.netflix.viewmodel.FilmeViewModelFactory
import com.example.netflix.viewmodel.PerfilViewModel
import com.example.netflix.viewmodel.PerfilViewModelFactory

// NavHost principal: define todas as rotas do app (como no AppNavigation.kt da Aula 13)
@Composable
fun AppNavigation(perfilRepository: PerfilRepository, filmeRepository: FilmeRepository) {
    val navController = rememberNavController()
    val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(perfilRepository))
    val filmeViewModel: FilmeViewModel = viewModel(factory = FilmeViewModelFactory(filmeRepository))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        NavHost(
            navController = navController,
            startDestination = "perfis"
        ) {

            composable("perfis") {
                TelaPerfis(
                    viewModel = viewModel,
                    irParaInicio = { navController.navigate("inicio") }
                )
            }

            composable("inicio") {
                TelaInicio(
                    viewModel = viewModel,
                    filmeViewModel = filmeViewModel,
                    irParaBuscar = { navController.navigate("buscar") },
                    irParaMenu = { navController.navigate("menu") },
                    irParaFilme = { navController.navigate("filme/$it") }
                )
            }

            composable("buscar") {
                TelaBuscar(
                    viewModel = viewModel,
                    filmeViewModel = filmeViewModel,
                    irParaInicio = { navController.navigate("inicio") },
                    irParaMenu = { navController.navigate("menu") },
                    irParaFilme = { navController.navigate("filme/$it") }
                )
            }

            // Rota com argumento: a tela de filme recebe so o id e acha o filme na lista do FilmeViewModel
            composable("filme/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
                TelaFilme(
                    id = id,
                    viewModel = viewModel,
                    filmeViewModel = filmeViewModel,
                    irParaInicio = { navController.navigate("inicio") },
                    irParaBuscar = { navController.navigate("buscar") },
                    irParaMenu = { navController.navigate("menu") },
                    irParaFilme = { navController.navigate("filme/$it") },
                    voltar = { navController.popBackStack() }
                )
            }

            composable("downloads") {
                TelaDownloads(
                    viewModel = viewModel,
                    filmeViewModel = filmeViewModel,
                    irParaInicio = { navController.navigate("inicio") },
                    irParaBuscar = { navController.navigate("buscar") },
                    irParaMenu = { navController.navigate("menu") },
                    irParaFilme = { navController.navigate("filme/$it") }
                )
            }

            composable("menu") {
                TelaMenu(
                    viewModel = viewModel,
                    voltar = { navController.popBackStack() },
                    sair = { navController.navigate("perfis") },
                    irParaEditar = { navController.navigate("editar") },
                    irParaDownloads = { navController.navigate("downloads") }
                )
            }

            composable("editar") {
                TelaEditarPerfil(
                    viewModel = viewModel,
                    voltar = { navController.popBackStack() },
                    irParaPerfis = { navController.navigate("perfis") }
                )
            }

        }
    }
}
