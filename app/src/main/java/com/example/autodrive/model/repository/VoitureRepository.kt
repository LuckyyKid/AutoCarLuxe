package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Voiture

class VoitureRepository(context: Context) {

    private val voitureDao = AppDatabase.getDatabase(context).voitureDao()

    fun getAll(): List<Voiture> {
        return voitureDao.getAll()
    }

    fun insert(voiture: Voiture) {
        voitureDao.insert(voiture)
    }

    fun update(voiture: Voiture) {
        voitureDao.update(voiture)
    }

    fun deleteById(id: Long) {
        voitureDao.deleteById(id)
    }

    fun rechercherVoitures(recherche: String): List<Voiture> {
        return voitureDao.rechercherVoitures(recherche)
    }
}
