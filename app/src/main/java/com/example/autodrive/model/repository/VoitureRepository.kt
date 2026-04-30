package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Voiture

class VoitureRepository(context: Context) {

    private val voitureDao = AppDatabase.getDatabase(context).voitureDao()

    fun getAll(): List<Voiture> {
        return voitureDao.getAll()
    }

    fun getById(id: Long): Voiture? {
        return voitureDao.getById(id)
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

    fun filtrerVoitures(filter: ClientFilter): List<Voiture> {
        var resultat = if (filter.recherche.isNotBlank()) {
            voitureDao.rechercherVoitures(filter.recherche)
        } else {
            voitureDao.getAllOrderByPrix()
        }

        if (filter.marque.isNotBlank()) {
            resultat = resultat.filter {
                it.marque.contains(filter.marque, ignoreCase = true)
            }
        }

        if (filter.modele.isNotBlank()) {
            resultat = resultat.filter {
                it.modele.contains(filter.modele, ignoreCase = true)
            }
        }

        val prixMin = filter.prixMin.toDoubleOrNull()
        val prixMax = filter.prixMax.toDoubleOrNull()

        if (prixMin != null) {
            resultat = resultat.filter { it.prixParJour >= prixMin }
        }

        if (prixMax != null) {
            resultat = resultat.filter { it.prixParJour <= prixMax }
        }

        if (filter.annee.isNotBlank()) {
            resultat = resultat.filter { it.annee.toString() == filter.annee }
        }

        if (filter.seulementDisponibles) {
            resultat = resultat.filter { it.estDisponible }
        }

        return resultat
    }
}
