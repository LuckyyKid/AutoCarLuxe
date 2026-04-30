package com.example.autodrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.ClientPresenter
import com.example.autodrive.presenter.contract.ClientContract
import com.example.autodrive.ui.theme.AutoDriveTheme

class ClientActivity : ComponentActivity(), ClientContract.View {

    private lateinit var presenter: ClientPresenter
    private var voituresState by mutableStateOf<List<Voiture>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ClientPresenter(
            this,
            VoitureRepository(applicationContext),
            UserSession(applicationContext)
        )

        setContent {
            AutoDriveTheme {
                var selectedVoitureId by rememberSaveable { mutableStateOf(0L) }
                var recherche by rememberSaveable { mutableStateOf(presenter.chargerDerniereRecherche()) }
                var marqueFiltre by rememberSaveable { mutableStateOf("") }
                var modeleFiltre by rememberSaveable { mutableStateOf("") }
                var prixMinFiltre by rememberSaveable { mutableStateOf("") }
                var prixMaxFiltre by rememberSaveable { mutableStateOf("") }
                var anneeFiltre by rememberSaveable { mutableStateOf("") }
                var showOnlyDisponible by rememberSaveable { mutableStateOf(false) }
                var showFilterDialog by rememberSaveable { mutableStateOf(false) }

                val selectedVoiture = if (selectedVoitureId == 0L) {
                    null
                } else {
                    voituresState.firstOrNull { it.id == selectedVoitureId }
                }

                fun chargerVoitures() {
                    presenter.chargerVoitures(
                        recherche = recherche,
                        marqueFiltre = marqueFiltre,
                        modeleFiltre = modeleFiltre,
                        prixMinFiltre = prixMinFiltre,
                        prixMaxFiltre = prixMaxFiltre,
                        anneeFiltre = anneeFiltre,
                        seulementDisponibles = showOnlyDisponible
                    )
                }

                LaunchedEffect(Unit) {
                    chargerVoitures()
                }

                if (selectedVoiture != null) {
                    VoitureDetailScreen(
                        voiture = selectedVoiture,
                        onBack = {
                            selectedVoitureId = 0L
                            chargerVoitures()
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
                                    "Menu",
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
                                    "Reservations",
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
                                presenter.enregistrerDerniereRecherche(it)
                                chargerVoitures()
                            },
                            placeholder = { Text("Rechercher une voiture", color = Color(0xFFAAAAAA)) },
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
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { showFilterDialog = true },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Filtrer", fontWeight = FontWeight.SemiBold)
                            }

                            if (
                                marqueFiltre.isNotBlank() ||
                                modeleFiltre.isNotBlank() ||
                                prixMinFiltre.isNotBlank() ||
                                prixMaxFiltre.isNotBlank() ||
                                anneeFiltre.isNotBlank() ||
                                showOnlyDisponible
                            ) {
                                TextButton(
                                    onClick = {
                                        marqueFiltre = ""
                                        modeleFiltre = ""
                                        prixMinFiltre = ""
                                        prixMaxFiltre = ""
                                        anneeFiltre = ""
                                        showOnlyDisponible = false
                                        chargerVoitures()
                                    }
                                ) {
                                    Text("Reinitialiser")
                                }
                            }
                        }

                        Text(
                            text = construireResumeFiltres(
                                marqueFiltre = marqueFiltre,
                                modeleFiltre = modeleFiltre,
                                prixMinFiltre = prixMinFiltre,
                                prixMaxFiltre = prixMaxFiltre,
                                anneeFiltre = anneeFiltre,
                                seulementDisponibles = showOnlyDisponible
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )

                        if (showFilterDialog) {
                            FilterDialog(
                                marqueSelectionnee = marqueFiltre,
                                modeleSelectionne = modeleFiltre,
                                prixMinSelectionne = prixMinFiltre,
                                prixMaxSelectionne = prixMaxFiltre,
                                anneeSelectionnee = anneeFiltre,
                                seulementDisponibles = showOnlyDisponible,
                                onDismiss = { showFilterDialog = false },
                                onApply = { marque, modele, prixMin, prixMax, annee, disponibles ->
                                    marqueFiltre = marque
                                    modeleFiltre = modele
                                    prixMinFiltre = prixMin
                                    prixMaxFiltre = prixMax
                                    anneeFiltre = annee
                                    showOnlyDisponible = disponibles
                                    showFilterDialog = false
                                    chargerVoitures()
                                }
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
                            items(voituresState) { voiture ->
                                val firstImage = voiture.imageUrls
                                    ?.split(",")
                                    ?.firstOrNull()
                                    ?.trim()

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    onClick = { selectedVoitureId = voiture.id }
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

    override fun afficherVoitures(voitures: List<Voiture>) {
        voituresState = voitures
    }
}

private fun construireResumeFiltres(
    marqueFiltre: String,
    modeleFiltre: String,
    prixMinFiltre: String,
    prixMaxFiltre: String,
    anneeFiltre: String,
    seulementDisponibles: Boolean
): String {
    val morceaux = mutableListOf<String>()

    if (marqueFiltre.isNotBlank()) morceaux.add("Marque: $marqueFiltre")
    if (modeleFiltre.isNotBlank()) morceaux.add("Modele: $modeleFiltre")
    if (prixMinFiltre.isNotBlank()) morceaux.add("Prix min: $prixMinFiltre$")
    if (prixMaxFiltre.isNotBlank()) morceaux.add("Prix max: $prixMaxFiltre$")
    if (anneeFiltre.isNotBlank()) morceaux.add("Annee: $anneeFiltre")
    if (seulementDisponibles) morceaux.add("Disponibles seulement")

    return if (morceaux.isEmpty()) {
        "Aucun filtre applique"
    } else {
        morceaux.joinToString("  |  ")
    }
}

@Composable
private fun FilterDialog(
    marqueSelectionnee: String,
    modeleSelectionne: String,
    prixMinSelectionne: String,
    prixMaxSelectionne: String,
    anneeSelectionnee: String,
    seulementDisponibles: Boolean,
    onDismiss: () -> Unit,
    onApply: (String, String, String, String, String, Boolean) -> Unit
) {
    var marqueTemp by remember { mutableStateOf(marqueSelectionnee) }
    var modeleTemp by remember { mutableStateOf(modeleSelectionne) }
    var prixMinTemp by remember { mutableStateOf(prixMinSelectionne) }
    var prixMaxTemp by remember { mutableStateOf(prixMaxSelectionne) }
    var anneeTemp by remember { mutableStateOf(anneeSelectionnee) }
    var disponibleTemp by remember { mutableStateOf(seulementDisponibles) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        marqueTemp,
                        modeleTemp,
                        prixMinTemp,
                        prixMaxTemp,
                        anneeTemp,
                        disponibleTemp
                    )
                }
            ) {
                Text("Appliquer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = {
            Text("Filtres")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = marqueTemp,
                    onValueChange = { marqueTemp = it },
                    label = { Text("Marque") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = modeleTemp,
                    onValueChange = { modeleTemp = it },
                    label = { Text("Modele") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = anneeTemp,
                    onValueChange = { anneeTemp = it },
                    label = { Text("Annee") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = prixMinTemp,
                    onValueChange = { prixMinTemp = it },
                    label = { Text("Prix minimum") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = prixMaxTemp,
                    onValueChange = { prixMaxTemp = it },
                    label = { Text("Prix maximum") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = disponibleTemp,
                        onCheckedChange = { disponibleTemp = it }
                    )
                    Text("Disponibles seulement")
                }
            }
        }
    )
}
