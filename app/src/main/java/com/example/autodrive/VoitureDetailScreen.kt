package com.example.autodrive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.autodrive.model.entity.EvaluationWithUser
import com.example.autodrive.model.entity.Voiture

@Composable
fun VoitureDetailScreen(
    voiture: Voiture,
    evaluations: List<EvaluationWithUser>,
    noteMoyenne: Float,
    nombreEvaluations: Int,
    currentUserId: Long,
    userHasEvaluated: Boolean,
    onBack: () -> Unit,
    onReserve: (Voiture) -> Unit,
    onAddEvaluation: () -> Unit
) {

    val images = voiture.imageUrls
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {


        Box(modifier = Modifier.fillMaxWidth()) {

            if (images.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    images.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Image voiture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(400.dp)
                                .height(280.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucune image disponible",
                        color = Color(0xFF999999),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }


            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.White, CircleShape)
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
        }


        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {


            Text(
                "${voiture.marque} ${voiture.modele}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "${voiture.annee}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (nombreEvaluations > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < noteMoyenne.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < noteMoyenne.toInt()) Color(0xFFFFB300) else Color(0xFFCCCCCC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        String.format("%.1f", noteMoyenne),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        " ($nombreEvaluations avis)",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(20.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Prix par jour",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${voiture.prixParJour}$",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "/jour",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF888888),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }


                Box(
                    modifier = Modifier
                        .background(
                            color = if (voiture.estDisponible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (voiture.estDisponible) "Disponible" else "Indisponible",
                        color = if (voiture.estDisponible) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(20.dp))


            if (!voiture.description.isNullOrBlank()) {
                Text(
                    "À propos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    voiture.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF555555),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            Button(
                onClick = { onReserve(voiture) },
                enabled = voiture.estDisponible,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color(0xFFCCCCCC),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    if (voiture.estDisponible) "Réserver cette voiture" else "Non disponible",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onAddEvaluation,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    if (userHasEvaluated) "Modifier mon avis" else "Laisser un avis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (evaluations.isNotEmpty()) {
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    "Avis des clients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                evaluations.forEach { evaluationWithUser ->
                    EvaluationCard(evaluationWithUser = evaluationWithUser)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EvaluationCard(evaluationWithUser: EvaluationWithUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${evaluationWithUser.utilisateur.prenom} ${evaluationWithUser.utilisateur.nom}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        evaluationWithUser.evaluation.dateEvaluation,
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < evaluationWithUser.evaluation.note.toInt()) 
                                Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < evaluationWithUser.evaluation.note.toInt()) 
                                Color(0xFFFFB300) else Color(0xFFCCCCCC),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            if (!evaluationWithUser.evaluation.commentaire.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    evaluationWithUser.evaluation.commentaire,
                    fontSize = 14.sp,
                    color = Color(0xFF555555),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
