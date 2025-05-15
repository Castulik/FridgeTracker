package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.CodeRepository

class CodeViewModelFactory(
    private val application: Application,
    private val repository: CodeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CodeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}