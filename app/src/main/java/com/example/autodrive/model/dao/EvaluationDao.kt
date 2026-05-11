package com.example.autodrive.model.dao

import androidx.room.*
import com.example.autodrive.model.entity.Evaluation
import com.example.autodrive.model.entity.EvaluationWithUser

@Dao
interface EvaluationDao {
    
    @Insert
    fun insert(evaluation: Evaluation): Long
    
    @Update
    fun update(evaluation: Evaluation)
    
    @Delete
    fun delete(evaluation: Evaluation)
    
    @Query("SELECT * FROM evaluation WHERE voitureId = :voitureId ORDER BY dateEvaluation DESC")
    fun getEvaluationsByVoiture(voitureId: Long): List<Evaluation>
    
    @Transaction
    @Query("SELECT * FROM evaluation WHERE voitureId = :voitureId ORDER BY dateEvaluation DESC")
    fun getEvaluationsWithUserByVoiture(voitureId: Long): List<EvaluationWithUser>
    
    @Query("SELECT AVG(note) FROM evaluation WHERE voitureId = :voitureId")
    fun getNoteMoyenne(voitureId: Long): Float?
    
    @Query("SELECT COUNT(*) FROM evaluation WHERE voitureId = :voitureId")
    fun getNombreEvaluations(voitureId: Long): Int
    
    @Query("SELECT * FROM evaluation WHERE utilisateurId = :utilisateurId AND voitureId = :voitureId LIMIT 1")
    fun getEvaluationByUserAndVoiture(utilisateurId: Long, voitureId: Long): Evaluation?
    
    @Query("SELECT * FROM evaluation WHERE utilisateurId = :utilisateurId ORDER BY dateEvaluation DESC")
    fun getEvaluationsByUser(utilisateurId: Long): List<Evaluation>
}
