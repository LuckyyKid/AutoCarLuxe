package com.example.autodrive

import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.ui.theme.AutoDriveTheme

class ClientActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "autodrive-db"
        )
            .allowMainThreadQueries()
            .build()

        val voitureDao = db.voitureDao()

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        setContent {
            AutoDriveTheme {

            var voitures by remember { mutableStateOf(emptyList<Voiture>()) }
            var selectedVoiture by remember { mutableStateOf<Voiture?>(null) }

            var recherche by remember {
                mutableStateOf(prefs.getString("pref_derniere_recherche", "") ?: "")
            }

            var marqueFiltre by remember { mutableStateOf("") }


            var showOnlyDisponible by remember { mutableStateOf(false) }

            fun chargerVoitures() {
                var resultat = if (recherche.isNotBlank()) {
                    voitureDao.rechercherVoitures(recherche)
                } else {
                    voitureDao.getAll()
                }

                if (marqueFiltre.isNotBlank()) {
                    resultat = resultat.filter {
                        it.marque.equals(marqueFiltre, ignoreCase = true)
                    }
                }

                if (showOnlyDisponible) {

                    resultat = resultat.filter { it.estDisponible }
                }

                voitures = resultat
            }

            LaunchedEffect(Unit) {
                chargerVoitures()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
            ) {

                if (selectedVoiture != null) {

                    VoitureDetailScreen(
                        voiture = selectedVoiture!!,
                        onBack = {
                            selectedVoiture = null
                            chargerVoitures()
                        }
                    )

                } else {


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
                                "← Menu",
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            "AutoDrive",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        TextButton(
                            onClick = {
                                startActivity(
                                    Intent(this@ClientActivity, MesReservationsActivity::class.java)
                                )
                            }
                        ) {
                            Text(
                                "Réservations",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE))


                    OutlinedTextField(
                        value = recherche,
                        onValueChange = {
                            recherche = it
                            prefs.edit().putString("pref_derniere_recherche", it).apply()
                            chargerVoitures()
                        },
                        placeholder = { Text("Rechercher une voiture…", color = Color(0xFFAAAAAA)) },
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )


                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val filters = listOf("" to "Tous", "BMW" to "BMW", "Audi" to "Audi")
                        filters.forEach { (value, label) ->
                            val selected = marqueFiltre == value
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selected) Color(0xFF1A1A1A) else Color.White,
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(
                                    onClick = { marqueFiltre = value; chargerVoitures() },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                                        contentColor = if (selected) Color.White else Color(0xFF1A1A1A)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        label,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .padding(bottom = 4.dp)
                    ) {
                        Checkbox(
                            checked = showOnlyDisponible,
                            onCheckedChange = {
                                showOnlyDisponible = it
                                chargerVoitures()
                            }
                        )
                        Text(
                            "Masquer les véhicules hors service",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF444444)
                        )
                    }


                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 4.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(voitures) { voiture ->

                            val firstImage = voiture.imageUrls
                                ?.split(",")
                                ?.firstOrNull()
                                ?.trim()


                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                onClick = { selectedVoiture = voiture }
                            ) {
                                Column {


                                    Box(modifier = Modifier.fillMaxWidth()) {

                                        if (!firstImage.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = firstImage,
                                                contentDescription = "Image voiture",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .background(Color(0xFFE0E0E0)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "Aucune image",
                                                    color = Color(0xFF999999),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }


                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(10.dp)
                                                .background(
                                                    Color(0xCC000000),
                                                    RoundedCornerShape(50)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                "${voiture.prixParJour}$/j",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }


                                        if (!voiture.estDisponible) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(10.dp)
                                                    .background(
                                                        Color(0xFFFFEBEE),
                                                        RoundedCornerShape(50)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    "Hors service",
                                                    color = Color(0xFFC62828),
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }


                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 12.dp
                                        )
                                    ) {
                                        Text(
                                            "${voiture.marque} ${voiture.modele}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1A1A)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))


                                        Text(
                                            voiture.annee.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF888888)
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
