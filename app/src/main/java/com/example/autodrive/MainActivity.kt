package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.compose.AsyncImage

import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.presenter.VoiturePresenter
import com.example.autodrive.presenter.contract.VoitureContract
import com.example.autodrive.view.AddVoitureScreen

class MainActivity : ComponentActivity(), VoitureContract.View {

    private lateinit var presenter: VoiturePresenter
    private var voituresState by mutableStateOf<List<Voiture>>(emptyList())

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
        presenter = VoiturePresenter(this, voitureDao)

        setContent {

            var showAddScreen by remember { mutableStateOf(false) }
            var voitureToEdit by remember { mutableStateOf<Voiture?>(null) }

            LaunchedEffect(Unit) {
                presenter.chargerVoitures()
            }

            if (showAddScreen) {

                AddVoitureScreen(voitureToEdit) { voiture ->

                    if (voitureToEdit == null) {
                        presenter.ajouterVoiture(voiture)
                    } else {
                        presenter.modifierVoiture(voiture)
                    }

                    voitureToEdit = null
                    showAddScreen = false
                }

            } else {

                Column(modifier = Modifier.fillMaxSize()) {

                    Button(
                        onClick = {
                            voitureToEdit = null
                            showAddScreen = true
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Ajouter une voiture")
                    }

                    LazyColumn {

                        items(voituresState) { voiture ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {

                                Column(modifier = Modifier.padding(12.dp)) {


                                    val images = voiture.imageUrls
                                        ?.split(",")
                                        ?.map { it.trim() }
                                        ?.filter { it.isNotEmpty() }
                                        ?: emptyList()

                                    if (images.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState())
                                        ) {
                                            images.forEach { url ->
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = "Image voiture",
                                                    modifier = Modifier
                                                        .width(200.dp)
                                                        .height(120.dp)
                                                        .padding(4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        "${voiture.marque} ${voiture.modele}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text("Année : ${voiture.annee}")
                                    Text("Prix : ${voiture.prixParJour}$")
                                    Text("Description : ${voiture.description}")

                                    Row {

                                        Button(
                                            onClick = {
                                                voitureToEdit = voiture
                                                showAddScreen = true
                                            },
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Text("Modifier")
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                presenter.supprimerVoiture(voiture.id)
                                            },
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Text("Supprimer")
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