package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.autodrive.model.entity.Utilisateur

@Dao
interface UtilisateurDao {

    @Insert
    suspend fun insert(utilisateur: Utilisateur)

    @Query("SELECT * FROM utilisateur WHERE email = :email")
    suspend fun getByEmail(email: String): Utilisateur?

}