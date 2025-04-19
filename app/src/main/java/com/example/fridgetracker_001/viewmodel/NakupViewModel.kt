package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.SortCategoryOption
import com.example.fridgetracker_001.data.SortOption
import com.example.fridgetracker_001.data.ViewTypeNakup
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
                    val novy = NakupEntity(nazev = getCurrentDate())
                    val noveId = repository.vlozitNakup(novy) // POČKEJ až se to vloží
                    val created = repository.getNakupById(noveId.toInt())
                    if (created != null) {
                        _nakupyList.value = listOf(created)
                        _currentNakup.value = created
                    }
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

    fun updateCategoryExpansionState(nakupId: Int, newState: Map<String, Boolean>) {
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

    fun nastavSortPolozky(nakupId: Int, newSort: SortOption) {
        viewModelScope.launch {
            val staryNakup = repository.getNakupById(nakupId) ?: return@launch
            val novyNakup = staryNakup.copy(sortPolozky = newSort)
            repository.aktualizovatNakup(novyNakup)

            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = novyNakup
            }
        }
    }

    fun nastavSortKategorie(nakupId: Int, newSort: SortCategoryOption) {
        viewModelScope.launch {
            val staryNakup = repository.getNakupById(nakupId) ?: return@launch
            val novyNakup = staryNakup.copy(sortKategorie = newSort)
            repository.aktualizovatNakup(novyNakup)

            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = novyNakup
            }
        }
    }

    fun nastavViewType(nakupId: Int, newViewType: ViewTypeNakup) {
        viewModelScope.launch {
            val staryNakup = repository.getNakupById(nakupId) ?: return@launch
            val novyNakup = staryNakup.copy(viewType = newViewType)
            repository.aktualizovatNakup(novyNakup)

            // Lokálně aktualizujeme stav, pokud zrovna máme nahraný tento sklad
            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = novyNakup
            }
        }
    }
}


