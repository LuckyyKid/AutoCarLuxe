package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.dao.EvaluationDao
import com.example.autodrive.model.entity.Evaluation
import com.example.autodrive.model.entity.EvaluationWithUser

class EvaluationRepository(private val evaluationDao: EvaluationDao) {

    constructor(context: Context) : this(AppDatabase.getDatabase(context).evaluationDao())
    
    fun ajouterEvaluation(evaluation: Evaluation): Long {
        return evaluationDao.insert(evaluation)
    }
    
    fun modifierEvaluation(evaluation: Evaluation) {
        evaluationDao.update(evaluation)
    }
    
    fun supprimerEvaluation(evaluation: Evaluation) {
        evaluationDao.delete(evaluation)
    }
    
    fun getEvaluationsParVoiture(voitureId: Long): List<EvaluationWithUser> {
        return evaluationDao.getEvaluationsWithUserByVoiture(voitureId)
    }
    
    fun getNoteMoyenne(voitureId: Long): Float {
        return evaluationDao.getNoteMoyenne(voitureId) ?: 0f
    }
    
    fun getNombreEvaluations(voitureId: Long): Int {
        return evaluationDao.getNombreEvaluations(voitureId)
    }
    
    fun getEvaluationUtilisateur(utilisateurId: Long, voitureId: Long): Evaluation? {
        return evaluationDao.getEvaluationByUserAndVoiture(utilisateurId, voitureId)
    }
    
    fun getEvaluationsUtilisateur(utilisateurId: Long): List<Evaluation> {
        return evaluationDao.getEvaluationsByUser(utilisateurId)
    }
}
