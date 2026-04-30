package com.example.autodrive

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.autodrive.model.entity.Voiture

@Composable
fun VoitureDetailScreen(
    voiture: Voiture,
    onBack: () -> Unit
) {

    val context = LocalContext.current

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
                onClick = {
                    val intent = Intent(context, ReservationActivity::class.java)
                    intent.putExtra("voiture", voiture)
                    context.startActivity(intent)
                },
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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
