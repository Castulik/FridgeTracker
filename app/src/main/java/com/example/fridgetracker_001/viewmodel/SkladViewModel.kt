package com.example.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.obrazovky.deleteFilePath
import com.example.fridgetracker_001.obrazovky.toJsonString
import com.example.fridgetracker_001.repository.SkladRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class  SkladViewModel(application: Application, private val repository: SkladRepository) : AndroidViewModel(application) {

    // Stav aktuálního skladu
    private val _skladState = MutableStateFlow<SkladEntity?>(null)
    val skladState: StateFlow<SkladEntity?> = _skladState

    // Stav seznamu všech skladů
    private val _skladList = MutableStateFlow<List<SkladEntity>>(emptyList())
    val skladList: StateFlow<List<SkladEntity>> = _skladList

    init {
        nactiListSklad() // Načtení seznamu skladů při inicializaci ViewModelu
    }

    // Načtení všech skladů z databáze
    private fun nactiListSklad() {
        viewModelScope.launch {
            val sklady = repository.ziskejSklady()
            val serazene = sklady.sortedBy { it.poradi }
            _skladList.value = serazene
        }
    }

    // Načtení konkrétního skladu podle ID
    fun nactiSkladPodleId(skladId: Int) {
        viewModelScope.launch {
            _skladState.value = null
            val sklad = repository.ziskejSkladPodleId(skladId)
            _skladState.value = sklad
        }
    }

    // Přidání nového skladu
    fun pridatSklad(sklad: SkladEntity) {
        viewModelScope.launch {
            repository.vlozitSklad(sklad)
            nactiListSklad() // Aktualizace seznamu skladů po přidání
        }
    }

    // Aktualizace existujícího skladu
    fun aktualizovatSklad(sklad: SkladEntity) {
        viewModelScope.launch {
            repository.aktualizovatSklad(sklad)
            nactiListSklad() // Aktualizace seznamu skladů po úpravě
            _skladState.value = sklad // Aktualizace aktuálního skladu
        }
    }

    // Smazání konkrétního skladu
    fun smazatSklad(sklad: SkladEntity) {
        viewModelScope.launch {

            sklad.iconPath?.let { path ->
                deleteFilePath(path)
            }
            // 1) Nejdřív ho odstraníme z DB
            repository.smazatSklad(sklad)

            // 2) Načteme čerstvý seznam z DB, seřadíme podle poradi
            val currentList = repository.ziskejSklady()
                .sortedBy { it.poradi }

            // 3) Přepočítáme poradi od 1 do n
            val reindexed = currentList.mapIndexed { index, s ->
                s.copy(poradi = index + 1)
            }

            // 4) Uložíme reindexované sklady zpátky do DB
            reindexed.forEach { repository.aktualizovatSklad(it) }

            // 5) Znovu načteme do _skladList.value
            nactiListSklad()
        }
    }



    fun nastavViewType(skladId: Int, newViewType: String) {
        viewModelScope.launch {
            val stary = repository.ziskejSkladPodleId(skladId) ?: return@launch
            val novy = stary.copy(viewType = newViewType)
            repository.aktualizovatSklad(novy)
            // Lokálně aktualizujeme stav, pokud zrovna máme nahraný tento sklad
            if (_skladState.value?.id == skladId) {
                _skladState.value = novy
            }
        }
    }

    fun updateCategoryExpansionState(skladId: Int, newState: Map<String, Boolean>) {
        viewModelScope.launch {
            val sklad = repository.ziskejSkladPodleId(skladId) ?: return@launch

            // převedeme klíče Int na String kvůli JSON
            val newStateJson = newState.mapKeys { it.key }.toJsonString()

            val updatedSklad = sklad.copy(categoryExpansionState = newStateJson)

            repository.aktualizovatSklad(updatedSklad)

            if (_skladState.value?.id == skladId) {
                _skladState.value = updatedSklad
            }
        }
    }

    fun nastavSortPotraviny(skladId: Int, newSort: String) {
        viewModelScope.launch {
            val starySklad = repository.ziskejSkladPodleId(skladId) ?: return@launch
            val novySklad = starySklad.copy(sortPotraviny = newSort)
            repository.aktualizovatSklad(novySklad)

            if (_skladState.value?.id == skladId) {
                _skladState.value = novySklad
            }
        }
    }

    fun nastavSortKategorie(skladId: Int, newSort: String) {
        viewModelScope.launch {
            val starySklad = repository.ziskejSkladPodleId(skladId) ?: return@launch
            val novySklad = starySklad.copy(sortKategorie = newSort)
            repository.aktualizovatSklad(novySklad)

            if (_skladState.value?.id == skladId) {
                _skladState.value = novySklad
            }
        }
    }

    fun getSkladFlowById(skladId: Int) = repository.getSkladFlowById(skladId)

    fun insertSkladWithReorder(sklad: SkladEntity, newPosition: Int) {
        viewModelScope.launch {
            // 1) Načteme současný seznam skladů (z _skladList)
            val currentList = _skladList.value.toMutableList()

            // 2) Seřadíme je podle poradi (1-based)
            currentList.sortBy { it.poradi }

            // 3) Ořízneme newPosition do [1..(currentList.size+1)]
            //    - Pokud v DB nebyl žádný sklad, currentList.size=0 => safePos = 1
            //    - Pokud je plný, necháme max na (size+1) => vkládáme i na konec
            val safePos = newPosition.coerceIn(1, currentList.size + 1)

            // 4) Posuneme každého, kdo měl poradi >= safePos
            //    Tím uvolníme místo pro nový sklad
            val updatedList = currentList.map { old ->
                if (old.poradi >= safePos) {
                    old.copy(poradi = old.poradi + 1)  // posun o +1
                } else {
                    old
                }
            }

            // 5) Uložíme posunuté sklady do DB
            updatedList.forEach { repository.aktualizovatSklad(it) }

            // 6) Vložíme nový sklad s poradi = safePos (1-based)
            val novySklad = sklad.copy(poradi = safePos)
            repository.vlozitSklad(novySklad)

            // 7) Znovu načteme z DB (čímž se i setřídí a uloží do _skladList.value)
            nactiListSklad()
        }
    }

    fun updateSkladFullWithReorder(updatedSklad: SkladEntity, newPosition: Int) {
        viewModelScope.launch {
            // 1) Načti list a seřaď podle poradi (1-based)
            val currentList = _skladList.value.sortedBy { it.poradi }.toMutableList()

            // 2) Najdi sklad, který chceme posunout
            val indexInList = currentList.indexOfFirst { it.id == updatedSklad.id }
            if (indexInList == -1) return@launch  // Sklad nenalezen

            // 3) Odeber ho z listu
            currentList.removeAt(indexInList)

            // 4) Určíme safePos = [1..(currentList.size+1)]
            //    (teď je list o 1 kratší, proto +1)
            val safePos = newPosition.coerceIn(1, currentList.size + 1)

            // 5) Vloží aktualizovaný sklad do currentList na pozici (safePos - 1),
            //    protože currentList je 0-based, ale poradi chceme 1-based
            val newEntity = updatedSklad.copy(poradi = safePos)
            currentList.add(safePos - 1, newEntity)

            // 6) Následně přepočteme poradi od 1 do n
            currentList.forEachIndexed { idx, skladEntity ->
                // idx je 0-based, takže 1-based = idx + 1
                currentList[idx] = skladEntity.copy(poradi = idx + 1)
            }

            // 7) Uložíme přepočítané sklady do DB
            currentList.forEach { repository.aktualizovatSklad(it) }

            // 8) Znovu načteme seznam do ViewModelu
            nactiListSklad()
        }
    }
}
