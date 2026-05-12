package com.example.autodrive

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.Utilisateur
import com.example.autodrive.model.entity.Voiture
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoomDatabaseInstrumentedTest {

    private lateinit var database: AppDatabase

    @Before
    fun ouvrirBaseEnMemoire() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun fermerBaseEnMemoire() {
        database.close()
    }

    @Test
    fun etantDonneVoitureDejaReservee_quandPeriodeChevauche_alorsDaoDetecteConflit() {
        // Étant donné
        database.utilisateurDao().insert(
            Utilisateur(
                id = 1L,
                nom = "Keita",
                prenom = "Bafing",
                email = "bafing@example.com",
                password = ""
            )
        )
        database.voitureDao().insert(voiture(id = 10L, marque = "BMW", modele = "Serie 3"))
        database.reservationDao().insert(
            Reservation(
                id = 1L,
                utilisateurId = 1L,
                voitureId = 10L,
                dateDebut = "2026-05-10",
                dateFin = "2026-05-15",
                coutTotal = 500.0,
                statut = "ACTIVE"
            )
        )

        // Quand
        val conflits = database.reservationDao().verifierConflit(
            voitureId = 10L,
            dateDebut = "2026-05-12",
            dateFin = "2026-05-18"
        )

        // Alors
        assertEquals(1, conflits.size)
        assertEquals(10L, conflits.first().voitureId)
    }

    @Test
    fun etantDonnePlusieursVoituresEnSqlite_quandFiltreParMarque_alorsRetourneSeulementCetteMarque() {
        // Étant donné
        database.voitureDao().insert(voiture(id = 1L, marque = "BMW", modele = "Serie 3"))
        database.voitureDao().insert(voiture(id = 2L, marque = "Audi", modele = "A4"))
        database.voitureDao().insert(voiture(id = 3L, marque = "BMW", modele = "X5"))

        // Quand
        val voitures = database.voitureDao().filtrerParMarque("BMW")

        // Alors
        assertEquals(2, voitures.size)
        assertTrue(voitures.all { it.marque == "BMW" })
    }

    private fun voiture(
        id: Long,
        marque: String,
        modele: String
    ): Voiture {
        return Voiture(
            id = id,
            marque = marque,
            modele = modele,
            annee = 2024,
            prixParJour = 100.0,
            estDisponible = true,
            imageUrls = null,
            description = null,
            ageMinimum = 18
        )
    }
}
