package com.example.autodrive.presenter.contract

interface LoginContract {

    interface View {
        fun afficherErreur(message: String)
        fun ouvrirMenuPrincipal()
    }

    interface Presenter {
        fun verifierSessionExistante()
        fun connecter(nom: String, prenom: String, email: String)
    }
}
