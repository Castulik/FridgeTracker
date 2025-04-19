package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.obrazovky.toJsonString
import com.example.fridgetracker_001.repository.SeznamRepository
import com.example.fridgetracker_001.repository.SkladRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeznamViewModel(application: Application, private val repository: SeznamRepository) : AndroidViewModel(application) {

    // Převod Flow na LiveData, pokud chcete ve ViewModelu pozorovat data přes LiveData:
    //val seznamy = seznamRepository.seznamy.asLiveData()

    // Nebo lze přímo nechat jako Flow a pozorovat ve Compose přes collectAsState:
    val seznamyFlow = repository.seznamy

    // Proměnná pro ovládání zobrazení dialogu:
    var showAddDialog by mutableStateOf(false)

    fun onDialogOpen() { showAddDialog = true }

    fun onDialogClose() { showAddDialog = false }

    // Přidání položky
    fun pridatPolozku(polozka: SeznamEntity) {
        viewModelScope.launch {
            repository.pridatPolozku(polozka)
        }
    }

    private val _errorMsg = MutableStateFlow<Boolean>(false)
    val errorMsg: StateFlow<Boolean> = _errorMsg

    fun clearErrorMsg() {
        _errorMsg.value = false
    }

    fun pridavaniRucne(nazev: String, kategorie: String, mnostvi: Int, nakupId: Int) {
        viewModelScope.launch {

            //rucne - podivat na seznam jestli neexistuje polozka s jmenem a kategorii - existuje? vyskoci error. Neexistuje muzu pridat na nakup
            val existujici = repository.getSeznamEntity(nazev, kategorie, nakupId)

            if (existujici != null) {
                _errorMsg.value = true
                delay(1500)
                _errorMsg.value = false
            } else {
                val newItemNakup = SeznamEntity(
                    polozkaId = null,
                    nazev = nazev,
                    kategorie = kategorie,
                    nakupId = nakupId,
                    quantity = mnostvi
                )
                repository.pridatPolozku(newItemNakup)
                onDialogClose()
                _errorMsg.value = false
            }
        }
    }

    fun pridatZKatalogu(polozka: PolozkyEntity, nakupId: Int){
        viewModelScope.launch {
            val existujici = repository.getSeznamEntity(polozka.nazev, polozka.kategorie, nakupId)

            if (existujici != null) {
                val updatedItem = existujici.copy(quantity = existujici.quantity + 1)
                repository.updatePolozku(updatedItem)
            } else {
                val nova = SeznamEntity(
                    nazev = polozka.nazev,
                    kategorie = polozka.kategorie,
                    nakupId = nakupId,
                    quantity = 1
                )
                repository.pridatPolozku(nova)
            }
        }
    }

    fun odebratZKatalogu(polozka: PolozkyEntity, nakupId: Int){
        viewModelScope.launch {
            val existujici = repository.getSeznamEntity(polozka.nazev, polozka.kategorie, nakupId)

            if (existujici != null) {
                val newQuantity = existujici.quantity - 1
                if (newQuantity <= 0) {
                    repository.smazatPolozku(existujici)
                } else {
                    val updatedItem = existujici.copy(quantity = newQuantity)
                    repository.updatePolozku(updatedItem)
                }
            }
        }
    }

    // Smazání položky
    fun smazatPolozku(item: SeznamEntity) {
        viewModelScope.launch {
            repository.smazatPolozku(item)
        }
    }

    private val _edit = MutableStateFlow<SeznamEntity?>(null)
    val edit: StateFlow<SeznamEntity?> = _edit

    fun setEditItem(item: SeznamEntity?) {
        _edit.value = item
    }

    fun updatePolozku(item: SeznamEntity) {
        viewModelScope.launch {

            val existujici = repository.getSeznamEntity(item.nazev, item.kategorie, item.nakupId)

            if (existujici != null && existujici.id != item.id) {
                _errorMsg.value = true
                delay(1500)
                _errorMsg.value = false
            } else {
                repository.updatePolozku(item)
                _errorMsg.value = false
                setEditItem(null)
            }
        }
    }

}