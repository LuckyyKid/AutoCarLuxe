package com.example.autodrive.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.autodrive.model.dao.ReservationDao
import com.example.autodrive.model.dao.UtilisateurDao
import com.example.autodrive.model.dao.VoitureDao
import com.example.autodrive.model.entity.*

@Database(
    entities = [Voiture::class, Utilisateur::class, Reservation::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voitureDao(): VoitureDao
    abstract fun reservationDao(): ReservationDao
    abstract fun utilisateurDao(): UtilisateurDao
}