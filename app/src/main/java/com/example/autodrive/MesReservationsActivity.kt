package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.ReservationWithVoiture
import com.example.autodrive.ui.theme.AutoDriveTheme
import java.time.LocalDate

class MesReservationsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "autodrive-db"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val reservationDao = db.reservationDao()

        setContent {
            AutoDriveTheme {

            var reservations by remember { mutableStateOf(emptyList<ReservationWithVoiture>()) }
            var total by remember { mutableStateOf(0.0) }
            var count by remember { mutableStateOf(0) }

            fun charger() {
                val aujourdHui = LocalDate.now()

                val list = reservationDao.getHistoriqueAvecVoiture(1).map { r ->

                    if (r.statut == "ACTIVE" && LocalDate.parse(r.dateFin).isBefore(aujourdHui)) {
                        reservationDao.annulerReservation(r.id)

                        r.copy(statut = "TERMINEE")
                    } else {
                        r
                    }
                }

                reservations = list
                total = reservationDao.totalDepense(1) ?: 0.0
                count = reservationDao.countReservations(1)
            }

            LaunchedEffect(Unit) {
                charger()
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
                    horizontalArrangement = Arrangement.SpaceBetween,
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

                    Text(
                        "Mes réservations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.width(64.dp))
                }

                Divider(color = Color(0xFFEEEEEE))


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$count",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                "Réservations",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF888888)
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp),
                            color = Color(0xFFEEEEEE)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$total $",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                "Total dépensé",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF888888)
                            )
                        }
                    }
                }


                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservations) { r ->

                        val badgeBg = when (r.statut) {
                            "ACTIVE"   -> Color(0xFFE8F5E9)
                            "TERMINEE" -> Color(0xFFF5F5F5)
                            else       -> Color(0xFFFFEBEE)
                        }
                        val badgeText = when (r.statut) {
                            "ACTIVE"   -> Color(0xFF2E7D32)
                            "TERMINEE" -> Color(0xFF888888)
                            else       -> Color(0xFFC62828)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${r.marque} ${r.modele}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(badgeBg, RoundedCornerShape(50))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            r.statut,
                                            color = badgeText,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = Color(0xFFEEEEEE))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Du ${r.dateDebut}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF888888)
                                        )
                                        Text(
                                            "au ${r.dateFin}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF888888)
                                        )
                                    }

                                    Text(
                                        "${r.coutTotal} $",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                }

                                if (r.statut == "ACTIVE") {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {

                                            reservationDao.annulerReservation(r.id)
                                            charger()
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFEBEE),
                                            contentColor = Color(0xFFC62828)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Annuler la réservation",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }
}
