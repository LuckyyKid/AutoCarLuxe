package com.example.autodrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.ProfilPresenter
import com.example.autodrive.presenter.contract.ProfilContract
import com.example.autodrive.presenter.contract.ProfilUiState
import com.example.autodrive.ui.theme.AutoDriveTheme

class ProfilActivity : ComponentActivity(), ProfilContract.View {

    private lateinit var presenter: ProfilPresenter
    private var profilState by mutableStateOf(
        ProfilUiState(
            nomComplet = "",
            email = "",
            nombreReservations = 0,
            totalDepense = 0.0,
            marqueFavorite = "",
            marquesDisponibles = emptyList()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ProfilPresenter(
            this,
            UtilisateurRepository(applicationContext),
            ReservationRepository(applicationContext),
            UserSession(applicationContext)
        )
        presenter.chargerProfil()

        setContent {
            AutoDriveTheme {
                var showMarqueDialog by rememberSaveable { mutableStateOf(false) }

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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    profilState.nomComplet,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    profilState.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF888888)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                        "${profilState.nombreReservations}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text("Reservations", style = MaterialTheme.typography.bodySmall, color = Color(0xFF888888))
                                }

                                Divider(modifier = Modifier.height(36.dp).width(1.dp), color = Color(0xFFEEEEEE))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${profilState.totalDepense} $",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text("Total depense", style = MaterialTheme.typography.bodySmall, color = Color(0xFF888888))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                        if (profilState.marqueFavorite.isBlank()) "Aucune" else profilState.marqueFavorite,
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

                        OutlinedButton(
                            onClick = { presenter.deconnecter() },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Se deconnecter", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC62828))
                        }
                    }

                    if (showMarqueDialog) {
                        AlertDialog(
                            onDismissRequest = { showMarqueDialog = false },
                            title = { Text("Choisir une marque favorite") },
                            text = {
                                Column {
                                    profilState.marquesDisponibles.forEach { marque ->
                                        TextButton(
                                            onClick = {
                                                presenter.changerMarqueFavorite(marque)
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

    override fun afficherProfil(profil: ProfilUiState) {
        profilState = profil
    }

    override fun ouvrirConnexion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
