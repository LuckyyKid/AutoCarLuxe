package com.example.autodrive.presenter

import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.LoginContract

class LoginPresenter(
    private val view: LoginContract.View,
    private val utilisateurRepository: UtilisateurRepository,
    private val userSession: UserSession
) : LoginContract.Presenter {

    override fun verifierSessionExistante() {
        if (userSession.isLoggedIn()) {
            view.ouvrirMenuPrincipal()
        }
    }

    override fun connecter(nom: String, prenom: String, email: String) {
        when {
            nom.isBlank() -> view.afficherErreur("Veuillez entrer votre nom.")
            prenom.isBlank() -> view.afficherErreur("Veuillez entrer votre prenom.")
            email.isBlank() -> view.afficherErreur("Veuillez entrer votre email.")
            else -> {
                val utilisateur = utilisateurRepository.findOrCreate(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    email = email.trim().lowercase()
                )
                userSession.saveCurrentUserId(utilisateur.id)
                view.ouvrirMenuPrincipal()
            }
        }
    }
}
