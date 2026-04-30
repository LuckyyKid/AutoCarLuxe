package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.model.repository.VoitureRepository
import com.example.autodrive.presenter.VoiturePresenter
import com.example.autodrive.presenter.contract.VoitureContract
import com.example.autodrive.view.AddVoitureScreen
import com.example.autodrive.ui.theme.AutoDriveTheme

class AdminActivity : ComponentActivity(), VoitureContract.View {

    private lateinit var presenter: VoiturePresenter
    private var voituresState by mutableStateOf<List<Voiture>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = VoiturePresenter(this, VoitureRepository(applicationContext))

        setContent {
            AutoDriveTheme {

            var showAddScreen by rememberSaveable { mutableStateOf(false) }
            var voitureToEditId by rememberSaveable { mutableStateOf(0L) }
            val voitureToEdit = if (voitureToEditId == 0L) {
                null
            } else {
                voituresState.firstOrNull { it.id == voitureToEditId }
            }

            LaunchedEffect(Unit) {
                presenter.chargerVoitures()
            }

            if (showAddScreen) {

                AddVoitureScreen(
                    voiture = voitureToEdit,
                    onCancel = {
                        voitureToEditId = 0L
                        showAddScreen = false
                    },
                    onSave = { voiture ->
                        if (voitureToEdit == null) {
                            presenter.ajouterVoiture(voiture)
                        } else {
                            presenter.modifierVoiture(voiture)
                        }
                        voitureToEditId = 0L
                        showAddScreen = false
                    }
                )

            } else {

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
                                "← Menu",
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            "Admin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.width(64.dp))
                    }

                    Divider(color = Color(0xFFEEEEEE))


                    Button(
                        onClick = {
                            voitureToEditId = 0L
                            showAddScreen = true
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            "+ Ajouter une voiture",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }


                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(voituresState) { voiture ->

                            val images = voiture.imageUrls
                                ?.split(",")
                                ?.map { it.trim() }
                                ?.filter { it.isNotEmpty() }
                                ?: emptyList()

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {


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
                                                        .width(320.dp)
                                                        .height(180.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
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
                                            "${voiture.annee} · ${voiture.prixParJour}$/jour · ${if (voiture.estDisponible) "Disponible" else "Indisponible"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF888888)
                                        )

                                        if (!voiture.description.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                voiture.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF888888)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))


                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    voitureToEditId = voiture.id
                                                    showAddScreen = true
                                                },
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Modifier", fontWeight = FontWeight.Medium)
                                            }

                                            Button(
                                                onClick = {
                                                    presenter.supprimerVoiture(voiture.id)
                                                },
                                                shape = RoundedCornerShape(50),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFFFEBEE),
                                                    contentColor = Color(0xFFC62828)
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Supprimer", fontWeight = FontWeight.Medium)
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
    }

    override fun afficherVoitures(voitures: List<Voiture>) {
        voituresState = voitures
    }
}
