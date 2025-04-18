package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.obrazovky.getCurrentDate
import com.example.fridgetracker_001.obrazovky.toJsonString
import com.example.fridgetracker_001.repository.NakupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NakupViewModel(
    application: Application,
    private val repository: NakupRepository
) : AndroidViewModel(application) {

    // Seznam všech nákupů
    private val _nakupyList = MutableStateFlow<List<NakupEntity>>(emptyList())
    val nakupyList: StateFlow<List<NakupEntity>> = _nakupyList

    // Aktuálně vybraný nákup
    private val _currentNakup = MutableStateFlow<NakupEntity?>(null)
    val currentNakup: StateFlow<NakupEntity?> = _currentNakup

    init {
        viewModelScope.launch {
            repository.getAllNakupyFlow().collect { list ->
                if (list.isEmpty()) {
                    // První spuštění - založíme základní nakup s datumem v názvu
                    val novy = NakupEntity(nazev = getCurrentDate())
                    repository.vlozitNakup(novy)

                    // NakupList vrátí pořád empty do příští emise, tak mu zatím můžeme předat jen toho nového
                    _nakupyList.value = listOf(novy)
                    _currentNakup.value = novy
                } else {
                    // Až se objeví první neprázdný seznam, nastav ho
                    _nakupyList.value = list
                    // Pokud ještě nemáme currentNakup, nastavíme ho na první v seznamu
                    if (_currentNakup.value == null) {
                        _currentNakup.value = list.first()
                    }
                }
            }
        }
    }

    // Vložit nový nakup se zadaným názvem
    fun vlozitNakup(datum: String) {
        viewModelScope.launch {
            val novy = NakupEntity(nazev = datum)
            val noveId = repository.vlozitNakup(novy)
            val created = repository.getNakupById(noveId.toInt()) // Pak najdu podle ID
            if (created != null) {
                _currentNakup.value = created
            }
        }
    }

    fun smazatNakup(nakup: NakupEntity) {
        viewModelScope.launch {
            repository.smazatNakup(nakup)
            if (_currentNakup.value?.id == nakup.id) {
                val zbyvajici = _nakupyList.value.filter { it.id != nakup.id }
                _nakupyList.value = zbyvajici
                // Když tam ještě něco zbylo, vezmeme první
                _currentNakup.value = zbyvajici.firstOrNull()
            }
        }
    }

    // Nastavit current
    fun setAsCurrent(nakupId: Int) {
        viewModelScope.launch {
            val nalezeny = repository.getNakupById(nakupId) ?: return@launch
            _currentNakup.value = nalezeny
        }
    }

    fun updateCategoryExpansionState(nakupId: Int, newState: Map<Int, Boolean>) {
        viewModelScope.launch {
            val nakup = repository.getNakupById(nakupId) ?: return@launch

            // převedeme klíče Int na String kvůli JSON
            val newStateJson = newState.mapKeys { it.key.toString() }.toJsonString()

            val updateNakup = nakup.copy(categoryExpansionState = newStateJson)

            repository.aktualizovatNakup(updateNakup)

            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = updateNakup
            }
        }
    }
}


