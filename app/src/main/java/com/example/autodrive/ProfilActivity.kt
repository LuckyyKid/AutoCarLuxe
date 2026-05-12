package com.example.autodrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.ui.theme.AutoDriveTheme

class ProfilActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoDriveTheme {

                val session         = UserSession(applicationContext)
                val userRepo        = UtilisateurRepository(applicationContext)
                val reservationRepo = ReservationRepository(applicationContext)

                val userId  = session.getCurrentUserId()
                val user    = userRepo.getById(userId)
                val count   = reservationRepo.countReservations(userId)
                val total   = reservationRepo.totalDepense(userId)

                val marques = listOf("", "BMW", "Mercedes", "Audi", "Volkswagen")
                var marqueFavorite   by remember { mutableStateOf(session.getMarqueFavorite()) }
                var showMarqueDialog by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F7F7))
                ) {
                    // ── En-tête ──────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { finish() }) {
                            Text("Retour", color = Color(0xFF1A1A1A), fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Mon profil",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    Divider(color = Color(0xFFEEEEEE))

                    Column(modifier = Modifier.padding(16.dp)) {

                        // ── Infos utilisateur ─────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "${user?.prenom ?: ""} ${user?.nom ?: ""}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    user?.email ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF888888)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Statistiques ──────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$count",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text("Réservations", style = MaterialTheme.typography.bodySmall, color = Color(0xFF888888))
                                }

                                Divider(modifier = Modifier.height(36.dp).width(1.dp), color = Color(0xFFEEEEEE))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$total $",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text("Total dépensé", style = MaterialTheme.typography.bodySmall, color = Color(0xFF888888))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Marque favorite ───────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Marque favorite", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                                    Text(
                                        if (marqueFavorite.isBlank()) "Aucune" else marqueFavorite,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF888888)
                                    )
                                }
                                TextButton(onClick = { showMarqueDialog = true }) {
                                    Text("Changer", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // ── Déconnexion ───────────────────────
                        OutlinedButton(
                            onClick = {
                                session.logout()
                                val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Se déconnecter", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC62828))
                        }
                    }

                    // ── Dialog choix marque ───────────────────
                    if (showMarqueDialog) {
                        AlertDialog(
                            onDismissRequest = { showMarqueDialog = false },
                            title = { Text("Choisir une marque favorite") },
                            text = {
                                Column {
                                    marques.forEach { marque ->
                                        TextButton(
                                            onClick = {
                                                marqueFavorite = marque
                                                session.saveMarqueFavorite(marque)
                                                showMarqueDialog = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (marque.isBlank()) "Aucune" else marque)
                                        }
                                    }
                                }
                            },
                            confirmButton = {}
                        )
                    }
                }
            }
        }
    }
}