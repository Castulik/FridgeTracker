package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.CodeRepository
import cz.filip.fridgetracker_001.repository.PotravinaRepository

class PotravinaViewModelFactory(
    private val application: Application,
    private val repository: PotravinaRepository,
    private val codeRepository: CodeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PotravinaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PotravinaViewModel(application, repository, codeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}