package com.example.fridgetracker_001.repository

import com.example.fridgetracker_001.data.dao.CodeDao
import com.example.fridgetracker_001.data.entities.CodeEntity

class CodeRepository(private val codeDao: CodeDao) {

    suspend fun vlozitAktualizovatCode(codeEntity: CodeEntity) {
        codeDao.vlozitAktualizovat(codeEntity)
    }

    suspend fun getCodeEntityByCode(barcode: String): CodeEntity? {
        return codeDao.getCodeEntityByCode(barcode)
    }
}