package com.example.autodrive

import com.example.autodrive.model.repository.ReservationRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class ModelUnitTest {

    private val reservationRepository = Mockito.mock(
        ReservationRepository::class.java,
        Mockito.CALLS_REAL_METHODS
    )

    @Test
    fun `Quand les dates de reservation sont valides Alors le cout total est calcule`() {
        val cout = reservationRepository.calculerCout(
            dateDebut = "2026-05-01",
            dateFin = "2026-05-04",
            prixParJour = 100.0
        )

        assertEquals(300.0, cout, 0.01)
    }

    @Test
    fun `Quand une date de reservation est manquante Alors le cout est zero`() {
        val cout = reservationRepository.calculerCout(
            dateDebut = null,
            dateFin = "2026-05-04",
            prixParJour = 100.0
        )

        assertEquals(0.0, cout, 0.01)
    }

    @Test
    fun `Quand la date de fin est avant la date de debut Alors le cout est zero`() {
        val cout = reservationRepository.calculerCout(
            dateDebut = "2026-05-04",
            dateFin = "2026-05-01",
            prixParJour = 100.0
        )

        assertEquals(0.0, cout, 0.01)
    }
}
