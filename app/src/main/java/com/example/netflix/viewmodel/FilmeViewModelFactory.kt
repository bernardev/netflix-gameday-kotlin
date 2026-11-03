package com.example.netflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.netflix.data.repository.FilmeRepository

class FilmeViewModelFactory(private val repository: FilmeRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilmeViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }

}
