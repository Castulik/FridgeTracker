package cz.filip.fridgetracker_001.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.filip.fridgetracker_001.data.entities.CodeEntity
import cz.filip.fridgetracker_001.repository.CodeRepository
import kotlinx.coroutines.launch

class CodeViewModel(application: Application, private val repository: CodeRepository) :
    AndroidViewModel(application) {

    fun vlozitAktualizovatCode(codeEntity: CodeEntity) {
        viewModelScope.launch {
            repository.vlozitAktualizovatCode(codeEntity)
        }
    }

    suspend fun getCodeEntityByBarcode(barcode: String): CodeEntity? {
        return repository.getCodeEntityByCode(barcode)
    }
}