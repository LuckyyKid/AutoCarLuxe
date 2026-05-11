package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Evaluation
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.model.repository.EvaluationRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.ui.theme.AutoDriveTheme
import java.text.SimpleDateFormat
import java.util.*

class AddEvaluationActivity : ComponentActivity() {

    private lateinit var evaluationRepository: EvaluationRepository
    private lateinit var userSession: UserSession
    private var voiture: Voiture? = null
    private var existingEvaluation: Evaluation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        evaluationRepository = EvaluationRepository(db.evaluationDao())
        userSession = UserSession(applicationContext)

        voiture = intent.getSerializableExtra("voiture") as? Voiture
        
        if (voiture == null) {
            finish()
            return
        }

        val userId = userSession.getCurrentUserId()
        existingEvaluation = evaluationRepository.getEvaluationUtilisateur(userId, voiture!!.id)

        setContent {
            AutoDriveTheme {
                AddEvaluationScreen(
                    voiture = voiture!!,
                    existingEvaluation = existingEvaluation,
                    onBack = ::finish,
                    onSave = ::saveEvaluation
                )
            }
        }
    }

    private fun saveEvaluation(note: Float, commentaire: String) {
        val userId = userSession.getCurrentUserId()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateEvaluation = dateFormat.format(Date())

        if (existingEvaluation != null) {
            val updatedEvaluation = existingEvaluation!!.copy(
                note = note,
                commentaire = commentaire.ifBlank { null },
                dateEvaluation = dateEvaluation
            )
            evaluationRepository.modifierEvaluation(updatedEvaluation)
        } else {
            val newEvaluation = Evaluation(
                utilisateurId = userId,
                voitureId = voiture!!.id,
                note = note,
                commentaire = commentaire.ifBlank { null },
                dateEvaluation = dateEvaluation
            )
            evaluationRepository.ajouterEvaluation(newEvaluation)
        }
        
        finish()
    }
}

@Composable
private fun AddEvaluationScreen(
    voiture: Voiture,
    existingEvaluation: Evaluation?,
    onBack: () -> Unit,
    onSave: (Float, String) -> Unit
) {
    var note by remember { mutableFloatStateOf(existingEvaluation?.note ?: 0f) }
    var commentaire by remember { mutableStateOf(existingEvaluation?.commentaire ?: "") }

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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    if (existingEvaluation != null) "Modifier mon avis" else "Laisser un avis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "${voiture.marque} ${voiture.modele}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "${voiture.annee}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Note",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < note.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Note ${index + 1}",
                        tint = if (index < note.toInt()) Color(0xFFFFB300) else Color(0xFFCCCCCC),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { note = (index + 1).toFloat() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Commentaire (optionnel)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = commentaire,
                onValueChange = { commentaire = it },
                placeholder = { Text("Partagez votre expérience avec cette voiture...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSave(note, commentaire) },
                enabled = note > 0,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color(0xFFCCCCCC),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    if (existingEvaluation != null) "Modifier" else "Publier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
