package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.NakupRepository

class NakupViewModelFactory(
    private val application: Application,
    private val repository: NakupRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NakupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NakupViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}