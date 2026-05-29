package com.example.autodrive

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Voiture
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AutoDriveE2ETest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = AppDatabase.getDatabase(context)
        resetApplicationState()
    }

    @After
    fun tearDown() {
        resetApplicationState()
    }

    @Test
    fun e2e_loginOuvreProfilPuisDeconnexionRetourneALaConnexion() {
        val scenario = lancerApplicationEtCreerSession("e2e-login-profile@example.com")

        composeRule.onNodeWithText("Mon profil").performClick()
        composeRule.onNodeWithText("Mon profil").assertIsDisplayed()
        composeRule.onNodeWithText("Marque favorite").assertIsDisplayed()

        composeRule.onNodeWithText("Se deconnecter").performClick()
        composeRule.onNodeWithText("Connectez-vous pour continuer").assertIsDisplayed()

        scenario.close()
    }

    @Test
    fun e2e_adminAjouteVoiturePuisElleEstPersisteeEtAffichee() {
        val marque = "E2E-BMW"
        val modele = "X1"
        val scenario = lancerApplicationEtCreerSession("e2e-admin-add@example.com")

        composeRule.onNodeWithText("Espace Admin").performClick()
        composeRule.onNodeWithText("+ Ajouter une voiture").performClick()
        attendreNombreDeChamps(6)

        composeRule.onAllNodes(hasSetTextAction())[0].performTextInput(marque)
        composeRule.onAllNodes(hasSetTextAction())[1].performTextInput(modele)
        composeRule.onAllNodes(hasSetTextAction())[2].performTextInput("123")
        composeRule.onAllNodes(hasSetTextAction())[3].performTextInput("2026")
        composeRule.onNodeWithText("Ajouter la voiture").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            database.voitureDao().getAll().any { it.marque == marque && it.modele == modele }
        }

        composeRule.onNodeWithText("$marque $modele").assertIsDisplayed()

        val voiture = database.voitureDao().getAll().firstOrNull {
            it.marque == marque && it.modele == modele
        }
        assertNotNull(voiture)
        assertEquals(123.0, voiture!!.prixParJour, 0.01)
        assertEquals(2026, voiture.annee)

        scenario.close()
    }

    @Test
    fun e2e_clientConsulteVoiturePuisPublieEvaluation() {
        val marque = "E2E-Audi"
        val modele = "A6"
        val commentaire = "Evaluation creee depuis un parcours E2E"
        database.voitureDao().insert(
            Voiture(
                marque = marque,
                modele = modele,
                annee = 2025,
                prixParJour = 140.0,
                estDisponible = true,
                imageUrls = null,
                description = "Voiture de test pour evaluation E2E",
                ageMinimum = 18
            )
        )
        val voiture = database.voitureDao().getAll().first {
            it.marque == marque && it.modele == modele
        }

        val scenario = lancerApplicationEtCreerSession("e2e-evaluation@example.com")

        composeRule.onNodeWithText("Espace Client").performClick()
        composeRule.onNodeWithText("$marque $modele").performClick()
        composeRule.onNodeWithText("Laisser un avis").performClick()
        composeRule.onNodeWithContentDescription("Note 5").performClick()
        composeRule.onAllNodes(hasSetTextAction())[0].performTextInput(commentaire)
        composeRule.onNodeWithText("Publier").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            database.evaluationDao()
                .getEvaluationsByVoiture(voiture.id)
                .any { it.note == 5f && it.commentaire == commentaire }
        }

        val evaluations = database.evaluationDao().getEvaluationsByVoiture(voiture.id)
        assertEquals(1, evaluations.size)
        assertEquals(5f, evaluations.first().note, 0.01f)
        assertEquals(commentaire, evaluations.first().commentaire)

        scenario.close()
    }

    private fun lancerApplicationEtCreerSession(email: String): ActivityScenario<LoginActivity> {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { }
        attendreChampsDeConnexion()

        composeRule.onAllNodes(hasSetTextAction())[0].performTextInput("E2E")
        composeRule.onAllNodes(hasSetTextAction())[1].performTextInput("User")
        composeRule.onAllNodes(hasSetTextAction())[2].performTextInput(email)
        composeRule.onNodeWithText("Entrer").performClick()
        composeRule.onNodeWithText("Espace Client").assertIsDisplayed()

        return scenario
    }

    private fun attendreChampsDeConnexion() {
        attendreNombreDeChamps(3)
    }

    private fun attendreNombreDeChamps(nombre: Int) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size >= nombre
            } catch (_: IllegalStateException) {
                false
            }
        }
    }

    private fun resetApplicationState() {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        database.clearAllTables()
        assertTrue(database.voitureDao().getAll().isEmpty())
    }
}
