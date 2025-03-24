package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.obrazovky.toJsonString
import com.example.fridgetracker_001.repository.SeznamRepository
import com.example.fridgetracker_001.repository.SkladRepository
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

    fun pridatNeboZvysitPolozku(nazev: String, kategorie: String, nakupId: Int) {
        viewModelScope.launch {
            // 1) Zkus načíst existující item (pokud máš v DAO nějakou query)
            val existujici = repository.getItemByNazevKategorie(nazev, kategorie, nakupId)
            if (existujici != null) {
                // 2) Zvýšit quantity
                val updatedItem = existujici.copy(quantity = existujici.quantity + 1)
                repository.updatePolozku(updatedItem)
            } else {
                // 3) Vložit novou
                val newItem = SeznamEntity(
                    nazev = nazev,
                    kategorie = kategorie,
                    nakupId = nakupId,
                    quantity = 1
                )
                repository.pridatPolozku(newItem)
            }
        }
    }


    // Smazání položky
    fun smazatPolozku(item: SeznamEntity) {
        viewModelScope.launch {
            repository.smazatPolozku(item)
        }
    }

    fun odebratNeboSnizitPolozku(nazev: String, kategorie: String, nakupId: Int) {
        viewModelScope.launch {
            val existujici = repository.getItemByNazevKategorie(nazev, kategorie, nakupId)
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

    fun updatePolozku(item: SeznamEntity) {
        viewModelScope.launch {
            repository.updatePolozku(item)
        }
    }
}