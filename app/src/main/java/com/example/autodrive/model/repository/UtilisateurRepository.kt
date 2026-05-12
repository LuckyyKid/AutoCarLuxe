package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Utilisateur

class UtilisateurRepository(context: Context) {

    private val utilisateurDao = AppDatabase.getDatabase(context).utilisateurDao()

    fun getOrCreateUtilisateurTest(): Utilisateur {
        val email = "test@test.com"
        var utilisateur = utilisateurDao.getByEmail(email)

        if (utilisateur == null) {
            utilisateurDao.insert(
                Utilisateur(
                    nom = "Test",
                    prenom = "User",
                    email = email,
                    password = "1234"
                )
            )
            utilisateur = utilisateurDao.getByEmail(email)
        }

        return utilisateur!!
    }

    fun findOrCreate(nom: String, prenom: String, email: String): Utilisateur {
        var user = utilisateurDao.getByEmail(email)
        if (user == null) {
            utilisateurDao.insert(
                Utilisateur(nom = nom, prenom = prenom, email = email, password = "")
            )
            user = utilisateurDao.getByEmail(email)
        }
        return user!!
    }

    fun getAll(): List<Utilisateur> {
        return utilisateurDao.getAll()
    }

    fun getById(id: Long): Utilisateur? {
        return utilisateurDao.getById(id)
    }

    fun update(utilisateur: Utilisateur) {
        utilisateurDao.update(utilisateur)
    }

    fun delete(utilisateur: Utilisateur) {
        utilisateurDao.delete(utilisateur)
    }
}
