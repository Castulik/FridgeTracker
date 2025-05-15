package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.SeznamRepository


class SeznamViewModelFactory(
    private val application: Application,
    private val repository: SeznamRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeznamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeznamViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}