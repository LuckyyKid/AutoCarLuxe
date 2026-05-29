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

class ProfilInstrumentedTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun etantDonneUtilisateurConnecte_quandIlOuvreMonProfil_alorsInformationsEtPreferencesSontAffichees() {
        // Etant donne — on repart d'une session vide
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        // L'utilisateur se connecte via LoginActivity (créée par moi)
        composeRule.onAllNodes(hasSetTextAction())[0].performTextInput("Saintil")
        composeRule.onAllNodes(hasSetTextAction())[1].performTextInput("Ariel-Wilkins")
        composeRule.onAllNodes(hasSetTextAction())[2].performTextInput("wilkins@example.com")
        composeRule.onNodeWithText("Entrer").performClick()

        // Il est maintenant sur MainActivity
        composeRule.onNodeWithText("Espace Client").assertIsDisplayed()

        // Quand — il clique sur Mon profil (bouton ajouté par moi)
        composeRule.onNodeWithText("Mon profil").performClick()

        // Alors — ProfilActivity (créée par moi) est affichée avec les bons éléments
        composeRule.onNodeWithText("Mon profil").assertIsDisplayed()
        composeRule.onNodeWithText("Marque favorite").assertIsDisplayed()
        composeRule.onNodeWithText("Se déconnecter").assertIsDisplayed()

        scenario.close()
    }
}