package com.example.autodrive.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.autodrive.model.dao.ReservationDao
import com.example.autodrive.model.dao.UtilisateurDao
import com.example.autodrive.model.dao.VoitureDao
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.Utilisateur
import com.example.autodrive.model.entity.Voiture

@Database(
    entities = [Voiture::class, Utilisateur::class, Reservation::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voitureDao(): VoitureDao
    abstract fun reservationDao(): ReservationDao
    abstract fun utilisateurDao(): UtilisateurDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autodrive-db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
