package com.example.autodrive.presenter

import com.example.autodrive.model.entity.Evaluation
import com.example.autodrive.model.repository.EvaluationRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.EvaluationContract
import com.example.autodrive.presenter.contract.EvaluationVoitureUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvaluationPresenter(
    private val view: EvaluationContract.View,
    private val evaluationRepository: EvaluationRepository,
    private val userSession: UserSession
) : EvaluationContract.Presenter {

    override fun chargerEvaluations(voitureId: Long) {
        val userId = userSession.getCurrentUserId()
        view.afficherEvaluations(
            EvaluationVoitureUiState(
                evaluations = evaluationRepository.getEvaluationsParVoiture(voitureId),
                noteMoyenne = evaluationRepository.getNoteMoyenne(voitureId),
                nombreEvaluations = evaluationRepository.getNombreEvaluations(voitureId),
                utilisateurADejaEvalue = evaluationRepository.getEvaluationUtilisateur(userId, voitureId) != null,
                utilisateurId = userId
            )
        )
    }

    override fun chargerEvaluationAEditer(voitureId: Long) {
        view.afficherEvaluationAEditer(
            evaluationRepository.getEvaluationUtilisateur(
                userSession.getCurrentUserId(),
                voitureId
            )
        )
    }

    override fun enregistrerEvaluation(voitureId: Long, note: Float, commentaire: String) {
        if (note <= 0f) {
            view.afficherErreur("Veuillez selectionner une note.")
            return
        }

        val utilisateurId = userSession.getCurrentUserId()
        val dateEvaluation = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val evaluationExistante = evaluationRepository.getEvaluationUtilisateur(utilisateurId, voitureId)

        if (evaluationExistante == null) {
            evaluationRepository.ajouterEvaluation(
                Evaluation(
                    utilisateurId = utilisateurId,
                    voitureId = voitureId,
                    note = note,
                    commentaire = commentaire.ifBlank { null },
                    dateEvaluation = dateEvaluation
                )
            )
        } else {
            evaluationRepository.modifierEvaluation(
                evaluationExistante.copy(
                    note = note,
                    commentaire = commentaire.ifBlank { null },
                    dateEvaluation = dateEvaluation
                )
            )
        }

        view.evaluationEnregistree()
    }
}
