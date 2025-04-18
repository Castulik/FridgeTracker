package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.repository.PolozkyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PolozkyViewModel(
    application: Application,
    private val repository: PolozkyRepository
) : AndroidViewModel(application) {

    var showAddDialog by mutableStateOf(false)
    fun onDialogOpen() { showAddDialog = true }
    fun onDialogClose() { showAddDialog = false }

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

    private val _errorMsg = MutableStateFlow<Boolean>(false)
    val errorMsg: StateFlow<Boolean> = _errorMsg

    fun clearErrorMsg() {
        _errorMsg.value = false
    }

    // Přidat novou položku do katalogu
    fun pridatPolozkaDoKatalogu(nazev: String, kategorie: Int) {
        viewModelScope.launch {

            val existujici = repository.getPolozkaEntity(nazev, kategorie)

            if (existujici != null){
                _errorMsg.value = true
                delay(1500)
                _errorMsg.value = false
            } else {
                val newToKalog = PolozkyEntity(
                    nazev = nazev,
                    kategorie = kategorie,
                )
                repository.vlozitPolozka(newToKalog)
                onDialogClose()
                _errorMsg.value = false
            }
        }
    }

    // Smazat položku z katalogu
    fun smazatPolozkaZKatalogu(item: PolozkyEntity) {
        viewModelScope.launch {
            repository.smazatPolozku(item)
        }
    }

    private val _edit = MutableStateFlow<PolozkyEntity?>(null)
    val edit: StateFlow<PolozkyEntity?> = _edit

    fun setEditItem(item: PolozkyEntity?) {
        _edit.value = item
    }

    fun updatePolozka(polozka: PolozkyEntity) {
        viewModelScope.launch {

            val existujici = polozka.kategorie?.let { repository.getPolozkaEntity(polozka.nazev, it) }

            if (existujici != null && existujici.id != polozka.id) {
                _errorMsg.value = true
                delay(1500)
                _errorMsg.value = false
            } else {
                repository.updatePolozka(polozka)
                _errorMsg.value = false
                setEditItem(null)
            }
        }
    }
}
