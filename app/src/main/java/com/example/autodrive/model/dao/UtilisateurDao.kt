package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.autodrive.model.entity.Utilisateur

@Dao
interface UtilisateurDao {

    @Insert
    fun insert(utilisateur: Utilisateur)

    @Update
    fun update(utilisateur: Utilisateur)

    @Delete
    fun delete(utilisateur: Utilisateur)

    @Query("SELECT * FROM utilisateur")
    fun getAll(): List<Utilisateur>

    @Query("SELECT * FROM utilisateur WHERE id = :id LIMIT 1")
    fun getById(id: Long): Utilisateur?

    @Query("SELECT * FROM utilisateur WHERE email = :email LIMIT 1")
    fun getByEmail(email: String): Utilisateur?
}
