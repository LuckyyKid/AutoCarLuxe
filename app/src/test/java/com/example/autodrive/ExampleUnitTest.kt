package com.example.autodrive

import com.example.autodrive.model.entity.ReservationWithVoiture
import com.example.autodrive.model.entity.Utilisateur
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.model.repository.EvaluationRepository
import com.example.autodrive.model.repository.ReservationCreationResult
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.repository.VoitureRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.ClientPresenter
import com.example.autodrive.presenter.EvaluationPresenter
import com.example.autodrive.presenter.LoginPresenter
import com.example.autodrive.presenter.MesReservationsPresenter
import com.example.autodrive.presenter.ProfilPresenter
import com.example.autodrive.presenter.ReservationPresenter
import com.example.autodrive.presenter.VoiturePresenter
import com.example.autodrive.presenter.contract.ClientContract
import com.example.autodrive.presenter.contract.EvaluationContract
import com.example.autodrive.presenter.contract.EvaluationVoitureUiState
import com.example.autodrive.presenter.contract.LoginContract
import com.example.autodrive.presenter.contract.MesReservationsContract
import com.example.autodrive.presenter.contract.ProfilContract
import com.example.autodrive.presenter.contract.ProfilUiState
import com.example.autodrive.presenter.contract.ReservationContract
import com.example.autodrive.presenter.contract.VoitureContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.collections.emptyList

class ExampleUnitTest {

    @Test
    fun `Étant donné une ReservationContract View factice Quand le Presenter calcule un coût valide Alors la View reçoit le bon coût`() {
        // Étant donné
        val view = FakeReservationView()
        val reservationRepository = mock<ReservationRepository>()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val userSession = mock<UserSession>()
        val presenter = ReservationPresenter(view, reservationRepository, utilisateurRepository, userSession)
        whenever(reservationRepository.calculerCout("2026-05-01", "2026-05-04", 100.0))
            .thenReturn(300.0)

        // Quand
        presenter.calculerCout("2026-05-01", "2026-05-04", 100.0)

        // Alors
        assertEquals(300.0, view.dernierCout, 0.01)
        verify(reservationRepository).calculerCout("2026-05-01", "2026-05-04", 100.0)
    }

    @Test
    fun `Étant donné une demande de réservation valide Quand le Presenter confirme Alors le repository est appelé et la View reçoit un succès`() {
        // Étant donné
        val view = FakeReservationView()
        val reservationRepository = mock<ReservationRepository>()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val userSession = mock<UserSession>()
        val presenter = ReservationPresenter(view, reservationRepository, utilisateurRepository, userSession)
        whenever(userSession.getCurrentUserId()).thenReturn(7L)
        whenever(
            reservationRepository.creerReservation(
                userId = 7L,
                voitureId = 10L,
                disponible = true,
                dateDebut = "2026-05-01",
                dateFin = "2026-05-04",
                prixParJour = 100.0
            )
        ).thenReturn(ReservationCreationResult(succes = true))

        // Quand
        presenter.confirmerReservation(
            voitureId = 10L,
            disponible = true,
            dateDebut = "2026-05-01",
            dateFin = "2026-05-04",
            prixParJour = 100.0
        )

        // Alors
        verify(reservationRepository).creerReservation(
            eq(7L),
            eq(10L),
            eq(true),
            eq("2026-05-01"),
            eq("2026-05-04"),
            eq(100.0)
        )
        assertTrue(view.reservationConfirmeeAppelee)
        assertEquals("", view.dernierMessage)
    }

    @Test
    fun `Étant donné une demande de réservation invalide Quand le Presenter confirme Alors la View reçoit le message d'erreur`() {
        // Étant donné
        val view = FakeReservationView()
        val reservationRepository = mock<ReservationRepository>()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val userSession = mock<UserSession>()
        val presenter = ReservationPresenter(view, reservationRepository, utilisateurRepository, userSession)
        whenever(userSession.getCurrentUserId()).thenReturn(7L)
        whenever(
            reservationRepository.creerReservation(
                userId = 7L,
                voitureId = 10L,
                disponible = false,
                dateDebut = "2026-05-01",
                dateFin = "2026-05-04",
                prixParJour = 100.0
            )
        ).thenReturn(
            ReservationCreationResult(
                succes = false,
                message = "Ce vehicule n'est pas disponible a la location."
            )
        )

        // Quand
        presenter.confirmerReservation(
            voitureId = 10L,
            disponible = false,
            dateDebut = "2026-05-01",
            dateFin = "2026-05-04",
            prixParJour = 100.0
        )

        // Alors
        assertEquals("Ce vehicule n'est pas disponible a la location.", view.dernierMessage)
    }

    @Test
    fun `Étant donné une View factice de voiture Quand le Presenter charge la liste Alors la View reçoit les voitures`() {
        // Étant donné
        val voitures = listOf(
            voiture(id = 1, marque = "BMW", modele = "Serie 3"),
            voiture(id = 2, marque = "Mercedes", modele = "Classe C")
        )
        val view = FakeVoitureView()
        val voitureRepository = mock<VoitureRepository>()
        val presenter = VoiturePresenter(view, voitureRepository)
        whenever(voitureRepository.getAll()).thenReturn(voitures)

        // Quand
        presenter.chargerVoitures()

        // Alors
        assertEquals(voitures, view.voituresAffichees)
        verify(voitureRepository).getAll()
    }

    @Test
    fun `Étant donné une voiture à ajouter Quand le Presenter ajoute la voiture Alors le repository insère puis recharge la liste`() {
        // Étant donné
        val nouvelleVoiture = voiture(id = 0, marque = "Audi", modele = "A4")
        val voituresApresAjout = listOf(voiture(id = 1, marque = "Audi", modele = "A4"))
        val view = FakeVoitureView()
        val voitureRepository = mock<VoitureRepository>()
        val presenter = VoiturePresenter(view, voitureRepository)
        whenever(voitureRepository.getAll()).thenReturn(voituresApresAjout)

        // Quand
        presenter.ajouterVoiture(nouvelleVoiture)

        // Alors
        verify(voitureRepository).insert(nouvelleVoiture)
        assertEquals(voituresApresAjout, view.voituresAffichees)
    }

    @Test
    fun `Étant donné un utilisateur de test Quand le Presenter initialise l'utilisateur Alors la session reçoit l'identifiant`() {
        // Étant donné
        val view = FakeReservationView()
        val reservationRepository = mock<ReservationRepository>()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val userSession = mock<UserSession>()
        val presenter = ReservationPresenter(view, reservationRepository, utilisateurRepository, userSession)
        whenever(utilisateurRepository.getOrCreateUtilisateurTest()).thenReturn(
            Utilisateur(id = 42L, nom = "Test", prenom = "User", email = "test@gmail.com", password = "1234")
        )

        // Quand
        presenter.initialiserUtilisateur()

        // Alors
        verify(userSession).saveCurrentUserId(42L)
    }

    @Test
    fun `Étant donné un formulaire de connexion valide Quand le LoginPresenter connecte Alors il crée la session et ouvre le menu`() {
        // Étant donné
        val view = FakeLoginView()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val userSession = mock<UserSession>()
        val presenter = LoginPresenter(view, utilisateurRepository, userSession)
        whenever(utilisateurRepository.findOrCreate("Bafing", "Keita", "bafing@example.com"))
            .thenReturn(Utilisateur(id = 99L, nom = "Bafing", prenom = "Keita", email = "bafing@example.com", password = ""))

        // Quand
        presenter.connecter(" Bafing ", " Keita ", "BAFING@example.com")

        // Alors
        verify(userSession).saveCurrentUserId(99L)
        assertTrue(view.menuOuvert)
        assertEquals("", view.dernierMessage)
    }

    @Test
    fun `Étant donné une note invalide Quand EvaluationPresenter enregistre Alors la View reçoit une erreur`() {
        // Étant donné
        val view = FakeEvaluationView()
        val evaluationRepository = mock<EvaluationRepository>()
        val userSession = mock<UserSession>()
        val presenter = EvaluationPresenter(view, evaluationRepository, userSession)

        // Quand
        presenter.enregistrerEvaluation(voitureId = 10L, note = 0f, commentaire = "")

        // Alors
        assertEquals("Veuillez selectionner une note.", view.dernierMessage)
    }

    @Test
    fun `Étant donné un formulaire voiture invalide Quand le VoiturePresenter enregistre Alors la View reçoit une erreur`() {
        // Étant donné
        val view = FakeVoitureView()
        val voitureRepository = mock<VoitureRepository>()
        val presenter = VoiturePresenter(view, voitureRepository)

        // Quand
        presenter.enregistrerVoitureDepuisFormulaire(
            id = 0L,
            marque = "",
            modele = "Serie 3",
            annee = "2024",
            prixParJour = "100",
            estDisponible = true,
            imageUrls = "",
            description = ""
        )

        // Alors
        assertEquals("Veuillez entrer une marque.", view.dernierMessage)
    }

    @Test
    fun `Étant donné un profil utilisateur existant Quand le ProfilPresenter charge le profil Alors la View reçoit les informations du profil`() {
        // Étant donné
        val view = FakeProfilView()
        val utilisateurRepository = mock<UtilisateurRepository>()
        val reservationRepository = mock<ReservationRepository>()
        val userSession = mock<UserSession>()
        val presenter = ProfilPresenter(view, utilisateurRepository, reservationRepository, userSession)
        whenever(userSession.getCurrentUserId()).thenReturn(5L)
        whenever(userSession.getMarqueFavorite()).thenReturn("BMW")
        whenever(utilisateurRepository.getById(5L)).thenReturn(
            Utilisateur(id = 5L, nom = "Keita", prenom = "Bafing", email = "bafing@example.com", password = "")
        )
        whenever(reservationRepository.countReservations(5L)).thenReturn(2)
        whenever(reservationRepository.totalDepense(5L)).thenReturn(350.0)

        // Quand
        presenter.chargerProfil()

        // Alors
        assertEquals("Bafing Keita", view.profil?.nomComplet)
        assertEquals("bafing@example.com", view.profil?.email)
        assertEquals(2, view.profil?.nombreReservations)
        assertEquals(350.0, view.profil?.totalDepense ?: 0.0, 0.01)
        assertEquals("BMW", view.profil?.marqueFavorite)
    }

    @Test
    fun `Étant donné une recherche client Quand le ClientPresenter charge les voitures Alors la View reçoit la liste filtrée`() {
        // Étant donné
        val view = FakeClientView()
        val voitureRepository = mock<VoitureRepository>()
        val userSession = mock<UserSession>()
        val presenter = ClientPresenter(view, voitureRepository, userSession)
        val voitures = listOf(voiture(id = 1L, marque = "BMW", modele = "X5"))
        whenever(voitureRepository.filtrerVoitures(any())).thenReturn(voitures)

        // Quand
        presenter.chargerVoitures(
            recherche = "BMW",
            marqueFiltre = "BMW",
            modeleFiltre = "",
            prixMinFiltre = "",
            prixMaxFiltre = "",
            anneeFiltre = "",
            seulementDisponibles = true
        )

        // Alors
        assertEquals(voitures, view.voituresAffichees)
        verify(voitureRepository).filtrerVoitures(any())
    }

    @Test
    fun `Étant donné des réservations avec plusieurs statuts Quand MesReservationsPresenter filtre les actives Alors la View reçoit seulement les actives`() {
        // Étant donné
        val view = FakeMesReservationsView()
        val reservationRepository = mock<ReservationRepository>()
        val userSession = mock<UserSession>()
        val presenter = MesReservationsPresenter(view, reservationRepository, userSession)
        val reservations = listOf(
            reservationAvecVoiture(id = 1L, statut = "ACTIVE"),
            reservationAvecVoiture(id = 2L, statut = "ANNULEE")
        )
        whenever(userSession.getCurrentUserId()).thenReturn(7L)
        whenever(reservationRepository.getHistoriqueAvecStatutsMisAJour(7L)).thenReturn(reservations)
        whenever(reservationRepository.totalDepense(7L)).thenReturn(100.0)
        whenever(reservationRepository.countReservations(7L)).thenReturn(2)

        // Quand
        presenter.chargerReservations()
        presenter.changerFiltre("ACTIVE")

        // Alors
        assertEquals("ACTIVE", view.filtreAffiche)
        assertEquals(1, view.reservationsAffichees.size)
        assertEquals("ACTIVE", view.reservationsAffichees.first().statut)
    }

    private fun voiture(id: Long, marque: String, modele: String): Voiture {
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

    private fun reservationAvecVoiture(id: Long, statut: String): ReservationWithVoiture {
        return ReservationWithVoiture(
            id = id,
            voitureId = 1L,
            marque = "BMW",
            modele = "X5",
            dateDebut = "2026-05-01",
            dateFin = "2026-05-04",
            coutTotal = 300.0,
            statut = statut
        )
    }

    private class FakeReservationView : ReservationContract.View {
        var dernierCout = 0.0
        var dernierMessage = ""
        var reservationConfirmeeAppelee = false

        override fun afficherCout(coutTotal: Double) {
            dernierCout = coutTotal
        }

        override fun afficherMessage(message: String) {
            dernierMessage = message
        }

        override fun reservationConfirmee() {
            reservationConfirmeeAppelee = true
        }
    }

    private class FakeVoitureView : VoitureContract.View {
        var voituresAffichees: List<Voiture> = emptyList()
        var dernierMessage = ""

        override fun afficherVoitures(voitures: List<Voiture>) {
            voituresAffichees = voitures
        }

        override fun afficherMessage(message: String) {
            dernierMessage = message
        }
    }

    private class FakeLoginView : LoginContract.View {
        var dernierMessage = ""
        var menuOuvert = false

        override fun afficherErreur(message: String) {
            dernierMessage = message
        }

        override fun ouvrirMenuPrincipal() {
            menuOuvert = true
        }
    }

    private class FakeEvaluationView : EvaluationContract.View {
        var dernierMessage = ""

        override fun afficherEvaluations(state: EvaluationVoitureUiState) = Unit
        override fun afficherEvaluationAEditer(evaluation: com.example.autodrive.model.entity.Evaluation?) = Unit

        override fun afficherErreur(message: String) {
            dernierMessage = message
        }

        override fun evaluationEnregistree() = Unit
    }

    private class FakeProfilView : ProfilContract.View {
        var profil: ProfilUiState? = null
        var connexionOuverte = false

        override fun afficherProfil(profil: ProfilUiState) {
            this.profil = profil
        }

        override fun ouvrirConnexion() {
            connexionOuverte = true
        }
    }

    private class FakeClientView : ClientContract.View {
        var voituresAffichees: List<Voiture> = emptyList()

        override fun afficherVoitures(voitures: List<Voiture>) {
            voituresAffichees = voitures
        }
    }

    private class FakeMesReservationsView : MesReservationsContract.View {
        var reservationsAffichees: List<ReservationWithVoiture> = emptyList()
        var filtreAffiche = ""
        var totalAffiche = 0.0
        var countAffiche = 0

        override fun afficherReservations(reservations: List<ReservationWithVoiture>) {
            reservationsAffichees = reservations
        }

        override fun afficherStatistiques(total: Double, count: Int) {
            totalAffiche = total
            countAffiche = count
        }

        override fun afficherFiltre(statut: String) {
            filtreAffiche = statut
        }
    }

    // ── Tests de Myguel ─────────────────────────────────────────

    @Test
    fun `Quand le Presenter charge les voitures Alors la View recoit la liste complete`() {
        val mockRepo = mock<VoitureRepository>()
        val mockView = mock<VoitureContract.View>()
        val presenter = VoiturePresenter(mockView, mockRepo)
        val voitures = listOf(
            Voiture(1L, "BMW",  "M3", 2023, 150.0, true, null, null, 18),
            Voiture(2L, "Audi", "A4", 2021, 120.0, true, null, null, 18)
        )
        whenever(mockRepo.getAll()).thenReturn(voitures)

        presenter.chargerVoitures()

        verify(mockRepo).getAll()
        verify(mockView).afficherVoitures(voitures)
    }

    @Test
    fun `Quand le Presenter ajoute une voiture Alors le repository insere et la vue est mise a jour`() {
        val mockRepo = mock<VoitureRepository>()
        val mockView = mock<VoitureContract.View>()
        val presenter = VoiturePresenter(mockView, mockRepo)
        val nouvelleVoiture = Voiture(0L, "BMW", "M3", 2023, 150.0, true, null, null, 18)
        val listeApresAjout = listOf(Voiture(1L, "BMW", "M3", 2023, 150.0, true, null, null, 18))
        whenever(mockRepo.getAll()).thenReturn(listeApresAjout)

        presenter.ajouterVoiture(nouvelleVoiture)

        verify(mockRepo).insert(nouvelleVoiture)
        verify(mockView).afficherVoitures(listeApresAjout)
    }

    @Test
    fun `Quand le Presenter supprime une voiture Alors le repository supprime par identifiant`() {
        val mockRepo = mock<VoitureRepository>()
        val mockView = mock<VoitureContract.View>()
        val presenter = VoiturePresenter(mockView, mockRepo)
        whenever(mockRepo.getAll()).thenReturn(emptyList())

        presenter.supprimerVoiture(42L)

        verify(mockRepo).deleteById(42L)
    }
}
