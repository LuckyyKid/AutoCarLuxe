package com.example.autodrive

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test

class ExampleInstrumentedTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun etantDonneUtilisateurNonConnecte_quandIlCreeSaSessionPuisOuvreEspaceClient_alorsListeVoituresEstAccessible() {
        // Etant donne que l'application est lancee sans session utilisateur existante.
        val scenario = lancerApplicationEtCreerSession("bafing-client@example.com")

        // Alors il arrive sur la plateforme principale.
        composeRule.onNodeWithText("Espace Client").assertIsDisplayed()
        composeRule.onNodeWithText("Espace Admin").assertIsDisplayed()

        // Quand il entre dans l'espace client.
        composeRule.onNodeWithText("Espace Client").performClick()

        // Alors l'ecran principal de consultation des voitures est accessible.
        composeRule.onNodeWithText("AutoDrive").assertIsDisplayed()
        composeRule.onNodeWithText("Filtrer").assertIsDisplayed()
        composeRule.onNodeWithText("Aucun filtre applique").assertIsDisplayed()

        scenario.close()
    }

    @Test
    fun etantDonneUtilisateurConnecte_quandIlOuvreEspaceAdmin_alorsGestionVoituresEstAccessible() {
        // Etant donne
        val scenario = lancerApplicationEtCreerSession("bafing-admin@example.com")

        // Quand
        composeRule.onNodeWithText("Espace Admin").performClick()

        // Alors
        composeRule.onNodeWithText("Admin").assertIsDisplayed()
        composeRule.onNodeWithText("+ Ajouter une voiture").assertIsDisplayed()

        scenario.close()
    }

    @Test
    fun etantDonneUtilisateurDansEspaceClient_quandIlOuvreHistorique_alorsReservationsSontAccessibles() {
        // Etant donne
        val scenario = lancerApplicationEtCreerSession("bafing-reservations@example.com")
        composeRule.onNodeWithText("Espace Client").performClick()

        // Quand
        composeRule.onNodeWithText("Reservations").performClick()

        // Alors
        composeRule.onNodeWithText("Tous").assertIsDisplayed()
        composeRule.onNodeWithText("Actives").assertIsDisplayed()

        scenario.close()
    }

    private fun lancerApplicationEtCreerSession(email: String): ActivityScenario<LoginActivity> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        // Quand l'utilisateur saisit son nom, son prenom et son email pour creer sa session.
        composeRule.onAllNodes(hasSetTextAction())[0].performTextInput("Bafing")
        composeRule.onAllNodes(hasSetTextAction())[1].performTextInput("Keita")
        composeRule.onAllNodes(hasSetTextAction())[2].performTextInput(email)
        composeRule.onNodeWithText("Entrer").performClick()

        return scenario
    }
}
