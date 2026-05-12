package com.example.autodrive.presenter.contract

import com.example.autodrive.model.entity.Evaluation
import com.example.autodrive.model.entity.EvaluationWithUser

data class EvaluationVoitureUiState(
    val evaluations: List<EvaluationWithUser>,
    val noteMoyenne: Float,
    val nombreEvaluations: Int,
    val utilisateurADejaEvalue: Boolean,
    val utilisateurId: Long
)

interface EvaluationContract {

    interface View {
        fun afficherEvaluations(state: EvaluationVoitureUiState)
        fun afficherEvaluationAEditer(evaluation: Evaluation?)
        fun afficherErreur(message: String)
        fun evaluationEnregistree()
    }

    interface Presenter {
        fun chargerEvaluations(voitureId: Long)
        fun chargerEvaluationAEditer(voitureId: Long)
        fun enregistrerEvaluation(voitureId: Long, note: Float, commentaire: String)
    }
}
