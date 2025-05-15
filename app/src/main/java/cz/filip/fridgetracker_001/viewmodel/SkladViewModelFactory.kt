package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.SkladRepository

class SkladViewModelFactory(
    private val application: Application,
    private val repository: SkladRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkladViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkladViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}