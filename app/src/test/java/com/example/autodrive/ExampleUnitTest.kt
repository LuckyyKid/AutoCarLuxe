package com.example.autodrive

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExampleUnitTest {

    // Vérifier que le calcul du coût est correct
    @Test
    fun calculerCout_3jours_retourne450() {
        val debut = LocalDate.parse("2026-05-01")
        val fin   = LocalDate.parse("2026-05-04")
        val jours = ChronoUnit.DAYS.between(debut, fin)
        val cout  = jours * 150.0

        assertEquals(450.0, cout, 0.01)
    }

    // Vérifier que des dates invalides (fin avant début) donnent 0$
    @Test
    fun calculerCout_dateFinAvantDebut_retourneZero() {
        val debut = LocalDate.parse("2026-05-05")
        val fin   = LocalDate.parse("2026-05-01")
        val jours = ChronoUnit.DAYS.between(debut, fin)
        val cout  = if (jours <= 0) 0.0 else jours * 150.0

        assertEquals(0.0, cout, 0.01)
    }

    // Vérifier que la même date début et fin donne 0$
    @Test
    fun calculerCout_memeDateDebutEtFin_retourneZero() {
        val debut = LocalDate.parse("2026-05-01")
        val fin   = LocalDate.parse("2026-05-01")
        val jours = ChronoUnit.DAYS.between(debut, fin)
        val cout  = if (jours <= 0) 0.0 else jours * 150.0

        assertEquals(0.0, cout, 0.01)
    }
}