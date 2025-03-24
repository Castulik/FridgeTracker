package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.repository.PolozkyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PolozkyViewModel(
    application: Application,
    private val repository: PolozkyRepository
) : AndroidViewModel(application) {

    // Flow s daty z katalogu
    val polozkyFlow: StateFlow<List<PolozkyEntity>> = repository.polozkyFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Při vytvoření viewModelu => zkusíme naplnit tabulku,
        // ale jen pokud je prázdná
        viewModelScope.launch {
            repository.initialSeedIfEmpty()
        }
    }

    // Přidat novou položku do katalogu
    fun pridatPolozkaDoKatalogu(nazev: String, kategorie: String) {
        viewModelScope.launch {
            val newItem = PolozkyEntity(nazev = nazev, kategorie = kategorie)
            repository.vlozitPolozka(newItem)
        }
    }

    // Smazat položku z katalogu
    fun smazatPolozkaZKatalogu(item: PolozkyEntity) {
        viewModelScope.launch {
            repository.smazatPolozku(item)
        }
    }
}
