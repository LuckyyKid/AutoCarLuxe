package com.example.autodrive.presenter

import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.ProfilContract
import com.example.autodrive.presenter.contract.ProfilUiState

class ProfilPresenter(
    private val view: ProfilContract.View,
    private val utilisateurRepository: UtilisateurRepository,
    private val reservationRepository: ReservationRepository,
    private val userSession: UserSession
) : ProfilContract.Presenter {

    private val marquesDisponibles = listOf("", "BMW", "Mercedes", "Audi", "Volkswagen")

    override fun chargerProfil() {
        val userId = userSession.getCurrentUserId()
        val utilisateur = utilisateurRepository.getById(userId)
        view.afficherProfil(
            ProfilUiState(
                nomComplet = listOfNotNull(utilisateur?.prenom, utilisateur?.nom)
                    .joinToString(" ")
                    .trim(),
                email = utilisateur?.email ?: "",
                nombreReservations = reservationRepository.countReservations(userId),
                totalDepense = reservationRepository.totalDepense(userId),
                marqueFavorite = userSession.getMarqueFavorite(),
                marquesDisponibles = marquesDisponibles
            )
        )
    }

    override fun changerMarqueFavorite(marque: String) {
        userSession.saveMarqueFavorite(marque)
        chargerProfil()
    }

    override fun deconnecter() {
        userSession.logout()
        view.ouvrirConnexion()
    }
}
