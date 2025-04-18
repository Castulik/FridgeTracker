package com.example.fridgetracker_001.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.CodeEntity
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import com.example.fridgetracker_001.repository.CodeRepository
import com.example.fridgetracker_001.repository.PotravinaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PotravinaViewModel(
    application: Application,
    private val repository: PotravinaRepository,
    private val codeRepository: CodeRepository
) : AndroidViewModel(application) {

    // -----------------------------------------
    // 1) "Globalní" věci: ID skladu, seznam potravin, atd.
    // -----------------------------------------

    // Stav - momentálně načtené ID skladu
    private val _currentSkladId = MutableStateFlow<Int>(0)

    // Všechna jídla z celé DB (ze všech skladů):
    val allPotravinyFlow: StateFlow<List<PotravinaEntity>> =
        repository.getAllPotravinyFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Stav seznamu potravin pro aktuální sklad
    @OptIn(ExperimentalCoroutinesApi::class)
    val potravinaList: StateFlow<List<PotravinaEntity>> = _currentSkladId
        .filterNotNull()
        .flatMapLatest { skladId ->
            repository.getPotravinyBySkladId(skladId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Získání Flow jedné konkrétní potraviny (třeba pro editaci podle ID)
    fun getPotravinaFlowById(potravinaId: Int) = repository.getPotravinaFlowById(potravinaId)

    // Nastavení aktuálního ID skladu + aktualizace seznamu potravin
    fun nactiPotravinyPodleIdSkladu(skladId: Int) {
        _currentSkladId.value = skladId
    }

    // -----------------------------------------
    // 2) CRUD metody: Uložit, přidat, aktualizovat, smazat...
    // -----------------------------------------

    fun pridatPotravinu(potravina: PotravinaEntity) {
        val skladId = _currentSkladId.value
        viewModelScope.launch {
            repository.pridatPotravinu(potravina)
            nactiPotravinyPodleIdSkladu(skladId) // Aktualizace seznamu potravin po přidání
        }
    }

    fun aktualizovatPotravinu(potravina: PotravinaEntity) {
        val skladId = _currentSkladId.value
        viewModelScope.launch {
            repository.aktualizovatPotravinu(potravina)
            nactiPotravinyPodleIdSkladu(skladId) // Aktualizace seznamu potravin po úpravě
        }
    }

    fun smazatPotravinu(potravina: PotravinaEntity) {
        val skladId = _currentSkladId.value
        viewModelScope.launch {
            repository.smazatPotravinu(potravina)
            nactiPotravinyPodleIdSkladu(skladId) // Aktualizace seznamu potravin po smazání
        }
    }

    fun presunPotravinuDoJinehoSkladu(potravinaId: Int, novySkladId: Int) {
        viewModelScope.launch {
            val potravinaFlow = repository.getPotravinaFlowById(potravinaId)
            val potravina = potravinaFlow.firstOrNull()  // Tady dostaneme PotravinaEntity? (nebo null)

            if (potravina != null) {
                val upravena = potravina.copy(skladId = novySkladId)
                repository.aktualizovatPotravinu(upravena)
            }
        }
    }

    fun getDaysLeft(potravina: PotravinaEntity): Long? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val expirationDate = LocalDate.parse(potravina.datumSpotreby, formatter)
            ChronoUnit.DAYS.between(LocalDate.now(), expirationDate)
        } catch (e: Exception) {
            null
        }
    }

    // -----------------------------------------
    // 3) Stav "pridávané" potraviny + metody pro manipulaci
    // -----------------------------------------

    // A) Tady držíme "rozpracovanou" potravinu, která se bude přidávat/uložit
    var pridavanaPotravina by mutableStateOf(defaultPotravina())
        private set

    // B) Vlajka, abychom init volali jen 1x
    var isInitialized = false

    // Booleovská proměnná, kterou můžeme ručně přepínat.
    var showDruhDialog by mutableStateOf(false)
        private set
    fun openDruhDialog() { showDruhDialog = true }
    fun closeDruhDialog() { showDruhDialog = false }
    /**
     * Kombinovaná logika pro zobrazení dialogu:
     * - buď je `showDruhDialog == true`,
     * - anebo je `druh` prázdný.
     */
    fun shouldShowDialog(): Boolean { return showDruhDialog /*|| pridavanaPotravina.druh.isBlank() */ }

    // C) Funkce pro návrat "výchozí" potraviny
    private fun defaultPotravina(): PotravinaEntity {
        return PotravinaEntity(
            nazev = "",
            datumSpotreby = "",
            datumPridani = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            potravinaIconaId = R.drawable.kind_ostatni,
            mnozstvi = "",
            vaha = "",
            jednotky = "g",
            skladId = 0,
            poznamka = "",
            druh = null,
            code = ""
        )
    }

    /**
     * Nastaví výchozí stav (jen jednou), např. když poprvé otevíráte obrazovku "Přidat potravinu".
     * Tady si předáte skladId a volitelně názevPolozky (např. z nákupního seznamu).
     */
    fun initPridavanaPotravina(
        skladId: Int,
        nazevPolozky: String,
        barcodePolozky: String
    ) {
        if (!isInitialized) {
            // Pokud rovnou chceme spustit "hledání kódu", můžete zavolat:

            pridavanaPotravina = defaultPotravina().copy(
                skladId = skladId,
                nazev = nazevPolozky,
                code = barcodePolozky
            )
            isInitialized = true

            openDruhDialog()

            if (barcodePolozky.isNotEmpty()) {
                // 1) Nastavit potravinu
                // 2) Zavolat stejné akce jako v update..., tzn. spustit job do DB
                forceLookupPridavanaPotravina()
            }

        } else {
            // Pokud už jednou bylo init..., jen zkontrolujeme, jestli barcode je nový:
            if (barcodePolozky.isNotEmpty() && barcodePolozky != pridavanaPotravina.code) {
                pridavanaPotravina = pridavanaPotravina.copy(code = barcodePolozky)
                forceLookupPridavanaPotravina()
            }
        }
    }

    private fun forceLookupPridavanaPotravina() {
        val current = pridavanaPotravina ?: return
        val code = current.code
        if (code.isEmpty()) return

        // Skočíme do jobu pro vyhledávání
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            val codeEntity = codeRepository.getCodeEntityByCode(code)
            if (codeEntity != null) {
                pridavanaPotravina = pridavanaPotravina.copy(
                    nazev = codeEntity.nazev,
                    druh = codeEntity.druh,
                    potravinaIconaId = codeEntity.potravinaIconaId
                )
                _uiEvent.emit(PotravinaUiEvent.ShowSnackbar("Kód nalezen – parametry doplněny."))
                closeDruhDialog()
            } else {
                _uiEvent.emit(PotravinaUiEvent.ShowSnackbar("Kód nenalezen, vyplňte ručně."))
                if (pridavanaPotravina.druh == null)
                    openDruhDialog()
            }
        }
    }

    sealed class PotravinaUiEvent {
        data class ShowSnackbar(val message: String) : PotravinaUiEvent()
        // případně i jiné jednorázové eventy (Navigate apod.)
    }

    private val _uiEvent = MutableSharedFlow<PotravinaUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private var searchJob: Job? = null

    /**
     * Když se uživatel posouvá v textových polích (název, datum, atd.), voláme to z Composable,
     * abychom synchronizovali ViewModel.
     */

    fun updatePridavanaPotravina(updated: PotravinaEntity) {
        val oldCode = pridavanaPotravina.code
        pridavanaPotravina = updated

        // Pokud se "code" změnil a není prázdný, spustit DB lookup, ale s debounce:
        if (updated.code.isNotEmpty() && updated.code != oldCode) {
            forceLookupPridavanaPotravina()
        }
    }

    /**
     * Pokud se vracíte ze skeneru a máte naskenovaný kód,
     * tady ho nastavíte a zároveň se pokusíte vyhledat v codeRepository (asynchronně).
     */
    fun onScannedCode(scannedCode: String) {
        if (scannedCode.isNotEmpty()) {
            viewModelScope.launch {
                val codeEntity = codeRepository.getCodeEntityByCode(scannedCode)
                pridavanaPotravina = if (codeEntity != null) {
                    pridavanaPotravina.copy(
                        code = scannedCode,
                        nazev = codeEntity.nazev,
                        druh = codeEntity.druh,
                        potravinaIconaId = codeEntity.potravinaIconaId
                    )
                } else {
                    // Kód nebyl v code_table -> nastavíme jen code,
                    // uživatel pak název vyplní ručně
                    pridavanaPotravina.copy(
                        code = scannedCode
                    )
                }
            }
        }
    }

    /**
     * Uložení potraviny (a code) do DB + refresh seznamu.
     * - Pokud potravina.id == 0 -> nová
     * - Pokud potravina.id != 0 -> update
     */
    fun ulozitPridavanouPotravinu() {
        val potravina = pridavanaPotravina  // lokální kopie, ať se nám to nezmění pod rukama
        val skladId = _currentSkladId.value

        viewModelScope.launch {

            repository.pridatPotravinu(potravina)

            showDruhDialog = false

            // 2) Pokud code není prázdný, uložit/aktualizovat i do code_table
            if (potravina.code.isNotEmpty()) {
                val codeEntity = CodeEntity(
                    code = potravina.code,
                    nazev = potravina.nazev,
                    druh = potravina.druh,
                    potravinaIconaId = potravina.potravinaIconaId
                )
                codeRepository.vlozitAktualizovatCode(codeEntity)

                // + update všech potravin se stejným code
                potravina.druh?.let {
                    repository.aktualizovatVsechnySDanymKodem(
                        kod = potravina.code,
                        newNazev = potravina.nazev,
                        newDruh = it,
                        newIcon = potravina.potravinaIconaId
                    )
                }
            }

            // 3) Přečteme seznam potravin pro daný sklad
            nactiPotravinyPodleIdSkladu(skladId)

            // 4) Reset "pridavanaPotravina" (volitelně), pokud chceme být připraveni na další
            resetPridavanaPotravina()
        }
    }

    /**
     * Reset "pridavanaPotravina" do výchozího stavu – třeba po uložení nebo po cancel.
     * Zároveň isInitialized nastavíme zpět na false, takže příště se znova zavolá init, pokud je třeba.
     */
    fun resetPridavanaPotravina() {
        pridavanaPotravina = defaultPotravina()
        isInitialized = false
    }


    // Jedna proměnná, která drží to, co právě editujeme:
    var editedPotravina by mutableStateOf<PotravinaEntity?>(null)
        private set

    // Pro ošetření, abychom nenahrávali z DB pokaždé dokola:
    private var isEditInitialized = false

    /**
     * Načte potravinu z DB (dle `potravinaId`) a uloží do `editedPotravina`.
     * Volá se z Composable v `LaunchedEffect` při otevření obrazovky.
     */

    fun initEditedPotravina(potravinaId: Int, barcode: String) {
        viewModelScope.launch {
            if (!isEditInitialized || (editedPotravina?.id != potravinaId)) {
                // Načíst z DB
                val flow = repository.getPotravinaFlowById(potravinaId)
                val potravina = flow.firstOrNull()
                editedPotravina = potravina
                isEditInitialized = true
            }

            if (barcode.isNotEmpty() && barcode != editedPotravina?.code) {
                // Nastavit code
                editedPotravina = editedPotravina?.copy(code = barcode)
                // Zavolat update -> spustí se debouncing job, snackBar atd.
                forceLookupEditedPotravina()
            }
        }
    }

    private fun forceLookupEditedPotravina() {
        val current = editedPotravina ?: return
        val code = current.code
        if (code.isEmpty()) return

        // Skočíme do jobu pro vyhledávání
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            val codeEntity = codeRepository.getCodeEntityByCode(code)
            if (codeEntity != null) {
                editedPotravina = editedPotravina?.copy(
                    nazev = codeEntity.nazev,
                    druh = codeEntity.druh,
                    potravinaIconaId = codeEntity.potravinaIconaId
                )
                _uiEvent.emit(PotravinaUiEvent.ShowSnackbar("Kód nalezen – parametry doplněny."))
            } else {
                _uiEvent.emit(PotravinaUiEvent.ShowSnackbar("Kód nenalezen, vyplňte ručně."))
                if (editedPotravina?.druh == null)
                    openDruhDialog()
            }
        }
    }

    /**
     * Při psaní do formuláře se volá tahle metoda, abychom
     * aktualizovali `editedPotravina` a udrželi Sync s Compose.
     */
    fun updateEditedPotravina(updated: PotravinaEntity) {
        val oldCode = editedPotravina?.code
        editedPotravina = updated

        if (updated.code.isNotEmpty() && updated.code != oldCode) {
            forceLookupEditedPotravina()
        }
    }

    /**
     * Pokud při editaci naskenujeme kód (po návratu ze skeneru),
     * pokusíme se načíst z code_table a přepsat editedPotravina.
     */
    fun onScannedCodeForEdit(scannedCode: String) {
        if (scannedCode.isNotEmpty()) {
            viewModelScope.launch {
                // codeRepository.getCodeEntityByCode si upravte podle svých metod
                val codeEntity = codeRepository.getCodeEntityByCode(scannedCode)
                val current = editedPotravina
                if (current != null) {
                    editedPotravina = if (codeEntity != null) {
                        current.copy(
                            code = scannedCode,
                            nazev = codeEntity.nazev,
                            druh = codeEntity.druh,
                            potravinaIconaId = codeEntity.potravinaIconaId
                        )
                    } else {
                        current.copy(
                            code = scannedCode
                        )
                    }
                }
            }
        }
    }

    /**
     * Po stisku "Uložit změny".
     * - Aktualizujeme potravinu v DB (protože existuje => id != 0).
     * - Pokud `code` není prázdné, uložíme do code_table.
     */
    fun ulozitZmenyPotravina() {
        val potravinaToUpdate = editedPotravina ?: return
        val skladId = _currentSkladId.value

        viewModelScope.launch {
            // 1) Update v DB
            repository.aktualizovatPotravinu(potravinaToUpdate)

            // 2) Pokud code není prázdný, updatujeme i v code_table
            if (potravinaToUpdate.code.isNotEmpty()) {
                val codeEntity = CodeEntity(
                    code = potravinaToUpdate.code,
                    nazev = potravinaToUpdate.nazev,
                    druh = potravinaToUpdate.druh,
                    potravinaIconaId = potravinaToUpdate.potravinaIconaId
                )
                codeRepository.vlozitAktualizovatCode(codeEntity)

                // A hromadně update potravin se stejným code
                potravinaToUpdate.druh?.let {
                    repository.aktualizovatVsechnySDanymKodem(
                        kod = potravinaToUpdate.code,
                        newNazev = potravinaToUpdate.nazev,
                        newDruh = it,
                        newIcon = potravinaToUpdate.potravinaIconaId
                    )
                }
            }
            // 3) Možná bude lepší i refresh potravinaList (pokud se jedná o stejný sklad)
            nactiPotravinyPodleIdSkladu(skladId)

            // 4) A jestli nechcete po save dále držet editedPotravina, můžete resetnout:
            discardEditedPotravina()
        }
    }

    /**
     * Smazání právě editované potraviny.
     * - Lze volat z "onDelete".
     */
    fun smazatEditedPotravina() {
        val potravinaToDelete = editedPotravina ?: return
        viewModelScope.launch {
            repository.smazatPotravinu(potravinaToDelete)
            // refresh?
            // nactiPotravinyPodleIdSkladu(...)
            discardEditedPotravina()
        }
    }

    /**
     * Zahodí z "editedPotravina" cokoliv, co tam je,
     * a nastaví "isEditInitialized" na false, takže příště se bude muset znovu init.
     */
    fun discardEditedPotravina() {
        editedPotravina = null
        isEditInitialized = false
    }
}