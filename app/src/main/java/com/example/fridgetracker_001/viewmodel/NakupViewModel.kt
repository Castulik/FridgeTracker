package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.obrazovky.toJsonString
import com.example.fridgetracker_001.repository.NakupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NakupViewModel(
    application: Application,
    private val repository: NakupRepository
) : AndroidViewModel(application) {

    // Můžeš mít:
    private val _currentNakup = MutableStateFlow<NakupEntity?>(null)
    val currentNakup: StateFlow<NakupEntity?> = _currentNakup

    private val _nakupyList = MutableStateFlow<List<NakupEntity>>(emptyList())
    val nakupyList: StateFlow<List<NakupEntity>> = _nakupyList

    init {
        // 1) Načteme průběžně všechny nákupy
        viewModelScope.launch {
            repository.getAllNakupyFlow().collect { list ->
                _nakupyList.value = list
            }
        }

        // 2) Zkusíme zjistit "poslední modifikovaný nákup"
        //    a nastavíme ho jako current (pokud nějaký existuje)
        viewModelScope.launch {
            val last = repository.getLastUpdatedNakup()
            _currentNakup.value = last
        }
    }

    fun loadNakupById(nakupId: Int) {
        // Jednorázové načtení do _currentNakup
        viewModelScope.launch {
            val entity = repository.getNakupById(nakupId)
            _currentNakup.value = entity
        }
        // Nebo pokud chceš dynamicky reagovat na změny,
        // můžeš použít Flow a .collect {} - podobně jako v init.
    }

    fun vlozitNakup(nakup: NakupEntity) {
        viewModelScope.launch {
            // Při každé změně nastavíme updatedAt na aktuální čas
            nakup.updatedAt = System.currentTimeMillis()
            repository.vlozitNakup(nakup)

            // Po vložení znovu zjistíme "poslední"
            // (anebo rovnou do _currentNakup.value = newlyInserted)
            val last = repository.getLastUpdatedNakup()
            _currentNakup.value = last
        }
    }

    fun smazatNakup(nakup: NakupEntity) {
        viewModelScope.launch {
            repository.smazatNakup(nakup)
            // Pokud jsi smazal zrovna currentNakup, tak buď
            //   1) nastav null,
            //   2) nebo zjisti zase lastUpdated (pokud existuje)
            val last = repository.getLastUpdatedNakup()
            _currentNakup.value = last
        }
    }

    fun aktualizovatNakup(nakup: NakupEntity) {
        viewModelScope.launch {
            // Opět nastavit updatedAt
            nakup.updatedAt = System.currentTimeMillis()
            repository.aktualizovatNakup(nakup)

            // Pokud je to právě currentNakup, přepiš ho i v paměti
            if (_currentNakup.value?.id == nakup.id) {
                _currentNakup.value = nakup
            }
        }
    }

    fun setAsCurrent(nakupId: Int) {
        viewModelScope.launch {
            val nakup = repository.getNakupById(nakupId) ?: return@launch
            nakup.updatedAt = System.currentTimeMillis()
            repository.aktualizovatNakup(nakup)
            _currentNakup.value = nakup
        }
    }

    // Příklad funkce na update categoryExpansionState (collapsible kategorie)
    fun updateCategoryExpansionState(nakupId: Int, newMap: Map<String, Boolean>) {
        viewModelScope.launch {
            val nakup = repository.getNakupById(nakupId) ?: return@launch
            val newJson = newMap.toJsonString()
            val updatedNakup = nakup.copy(categoryExpansionState = newJson)
            repository.aktualizovatNakup(updatedNakup)

            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = updatedNakup
            }
        }
    }

    fun nastavSortKategorie(nakupId: Int, newSort: String) {
        viewModelScope.launch {
            val nakup = repository.getNakupById(nakupId) ?: return@launch
            val updated = nakup.copy(sortKategorie = newSort)
            repository.aktualizovatNakup(updated)
            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = updated
            }
        }
    }

    fun nastavSortPolozky(nakupId: Int, newSort: String) {
        viewModelScope.launch {
            val nakup = repository.getNakupById(nakupId) ?: return@launch
            val updated = nakup.copy(sortPolozky = newSort)
            repository.aktualizovatNakup(updated)
            if (_currentNakup.value?.id == nakupId) {
                _currentNakup.value = updated
            }
        }
    }
}
