package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Utilisateur
import com.example.autodrive.model.repository.EvaluationRepository
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.ui.theme.AutoDriveTheme

class ProfilActivity : ComponentActivity() {

    private lateinit var utilisateurRepository: UtilisateurRepository
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var evaluationRepository: EvaluationRepository
    private lateinit var userSession: UserSession
    
    private var utilisateur by mutableStateOf<Utilisateur?>(null)
    private var nombreReservations by mutableStateOf(0)
    private var nombreEvaluations by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        utilisateurRepository = UtilisateurRepository(db.utilisateurDao())
        reservationRepository = ReservationRepository(db.reservationDao())
        evaluationRepository = EvaluationRepository(db.evaluationDao())
        userSession = UserSession(applicationContext)

        chargerProfil()

        setContent {
            AutoDriveTheme {
                ProfilScreen(
                    utilisateur = utilisateur,
                    nombreReservations = nombreReservations,
                    nombreEvaluations = nombreEvaluations,
                    onBack = ::finish,
                    onModifierProfil = ::modifierProfil
                )
            }
        }
    }

    private fun chargerProfil() {
        val userId = userSession.getCurrentUserId()
        utilisateur = utilisateurRepository.getUtilisateurById(userId)
        nombreReservations = reservationRepository.getReservationsByUtilisateur(userId).size
        nombreEvaluations = evaluationRepository.getEvaluationsUtilisateur(userId).size
    }

    private fun ouvrirModification() {
    }
    
    private fun modifierProfil(nom: String, prenom: String, email: String) {
        utilisateur?.let { user ->
            val updatedUser = user.copy(
                nom = nom,
                prenom = prenom,
                email = email
            )
            utilisateurRepository.update(updatedUser)
            chargerProfil()
        }
    }
}

@Composable
private fun ProfilScreen(
    utilisateur: Utilisateur?,
    nombreReservations: Int,
    nombreEvaluations: Int,
    onBack: () -> Unit,
    onModifierProfil: (String, String, String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF0F0F0), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "←",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Text(
                    "Mon Profil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (utilisateur == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "${utilisateur.prenom} ${utilisateur.nom}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            utilisateur.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF888888)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Statistiques",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Réservations",
                        value = nombreReservations.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Avis donnés",
                        value = nombreEvaluations.toString(),
                        color = Color(0xFFFFB300),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Informations du compte",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoRow(label = "Prénom", value = utilisateur.prenom)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Nom", value = utilisateur.nom)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Email", value = utilisateur.email)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "ID Utilisateur", value = "#${utilisateur.id}")
                    }
                }
            }
        }

        if (showEditDialog && utilisateur != null) {
            EditProfilDialog(
                utilisateur = utilisateur,
                onDismiss = { showEditDialog = false },
                onSave = { nom, prenom, email ->
                    onModifierProfil(nom, prenom, email)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF888888)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun EditProfilDialog(
    utilisateur: Utilisateur,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var nom by remember { mutableStateOf(utilisateur.nom) }
    var prenom by remember { mutableStateOf(utilisateur.prenom) }
    var email by remember { mutableStateOf(utilisateur.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le profil") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nom, prenom, email) },
                enabled = nom.isNotBlank() && prenom.isNotBlank() && email.isNotBlank()
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
