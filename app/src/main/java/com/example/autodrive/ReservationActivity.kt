package com.example.autodrive

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.Utilisateur
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.ui.theme.AutoDriveTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class ReservationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voiture = intent.getSerializableExtra("voiture") as Voiture

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "autodrive-db"
        )
            .allowMainThreadQueries()
            .build()

        val reservationDao = db.reservationDao()
        val utilisateurDao = db.utilisateurDao()

        val email = "test@test.com"
        var utilisateur = utilisateurDao.getByEmail(email)

        if (utilisateur == null) {
            utilisateurDao.insert(
                Utilisateur(
                    nom = "Test",
                    prenom = "User",
                    email = email,
                    password = "1234"
                )
            )
            utilisateur = utilisateurDao.getByEmail(email)
        }

        val userId = utilisateur!!.id

        setContent {
            AutoDriveTheme {

            val context = LocalContext.current

            var dateDebut by remember { mutableStateOf<LocalDate?>(null) }
            var dateFin by remember { mutableStateOf<LocalDate?>(null) }
            var coutTotal by remember { mutableStateOf(0.0) }
            var message by remember { mutableStateOf("") }

            fun calculer() {
                if (dateDebut != null && dateFin != null) {
                    val jours = ChronoUnit.DAYS.between(dateDebut, dateFin)
                    if (jours > 0) {
                        coutTotal = jours * voiture.prixParJour
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { finish() }) {
                        Text(
                            "← Retour",
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Réservation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                Divider(color = Color(0xFFEEEEEE))


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${voiture.marque} ${voiture.modele}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${voiture.prixParJour} $/jour",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Choisir les dates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dateDebut = LocalDate.of(year, month + 1, day)
                                    calculer()
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            if (dateDebut == null) "Choisir date début" else "Début : $dateDebut",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dateFin = LocalDate.of(year, month + 1, day)
                                    calculer()
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            if (dateFin == null) "Choisir date fin" else "Fin : $dateFin",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Coût total",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF888888)
                            )
                            Text(
                                "$coutTotal $",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Button(
                        onClick = {


                            if (!voiture.estDisponible) {
                                message = "Ce véhicule n'est pas disponible à la location."
                                return@Button
                            }


                            if (dateDebut == null || dateFin == null) {
                                message = "Veuillez sélectionner une date de début et une date de fin."
                                return@Button
                            }


                            val jours = ChronoUnit.DAYS.between(dateDebut, dateFin)
                            if (jours <= 0) {
                                message = "La date de fin doit être après la date de début."
                                return@Button
                            }


                            val conflits = reservationDao.verifierConflit(
                                voiture.id,
                                dateDebut.toString(),
                                dateFin.toString()
                            )

                            if (conflits.isNotEmpty()) {
                                message = "Ce véhicule est déjà réservé pour cette période. Veuillez choisir d'autres dates."
                                return@Button
                            }


                            val reservation = Reservation(
                                utilisateurId = userId,
                                voitureId = voiture.id,
                                dateDebut = dateDebut.toString(),
                                dateFin = dateFin.toString(),
                                coutTotal = jours * voiture.prixParJour,
                                statut = "ACTIVE"
                            )

                            reservationDao.insert(reservation)


                            Toast.makeText(
                                context,
                                "Réservation confirmée",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(context, MesReservationsActivity::class.java)
                            )

                            finish()
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Confirmer la réservation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { finish() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            "Retour",
                            color = Color(0xFF888888),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Text(
                                message,
                                modifier = Modifier.padding(14.dp),
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            }
        }
    }
}
