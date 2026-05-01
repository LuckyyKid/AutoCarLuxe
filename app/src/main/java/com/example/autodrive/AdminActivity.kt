package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.autodrive.presenter.VoiturePresenter
import com.example.autodrive.presenter.contract.VoitureContract
import com.example.autodrive.ui.theme.AutoDriveTheme
import com.example.autodrive.view.AddVoitureScreen

class AdminActivity : ComponentActivity(), VoitureContract.View {

    private lateinit var presenter: VoiturePresenter
    private var voituresState by mutableStateOf<List<Voiture>>(emptyList())
    private var showAddScreen by mutableStateOf(false)
    private var voitureToEditId by mutableStateOf(0L)

    private val voitureToEdit: Voiture?
        get() = voituresState.firstOrNull { it.id == voitureToEditId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = VoiturePresenter(this, VoitureRepository(applicationContext))
        presenter.chargerVoitures()

        setContent {
            AutoDriveTheme {
                AdminScreen(
                    voitures = voituresState,
                    showAddScreen = showAddScreen,
                    voitureToEdit = voitureToEdit,
                    onBack = ::finish,
                    onAddClicked = ::ouvrirAjoutVoiture,
                    onEditClicked = ::ouvrirModificationVoiture,
                    onDeleteClicked = presenter::supprimerVoiture,
                    onCancelEdit = ::fermerFormulaireVoiture,
                    onSaveVoiture = ::enregistrerVoiture
                )
            }
        }
    }

    private fun ouvrirAjoutVoiture() {
        voitureToEditId = 0L
        showAddScreen = true
    }

    private fun ouvrirModificationVoiture(voitureId: Long) {
        voitureToEditId = voitureId
        showAddScreen = true
    }

    private fun fermerFormulaireVoiture() {
        voitureToEditId = 0L
        showAddScreen = false
    }

    private fun enregistrerVoiture(voiture: Voiture) {
        if (voitureToEdit == null) {
            presenter.ajouterVoiture(voiture)
        } else {
            presenter.modifierVoiture(voiture)
        }
        fermerFormulaireVoiture()
    }

    override fun afficherVoitures(voitures: List<Voiture>) {
        voituresState = voitures
    }
}

@Composable
private fun AdminScreen(
    voitures: List<Voiture>,
    showAddScreen: Boolean,
    voitureToEdit: Voiture?,
    onBack: () -> Unit,
    onAddClicked: () -> Unit,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Long) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveVoiture: (Voiture) -> Unit
) {
    if (showAddScreen) {
        AddVoitureScreen(
            voiture = voitureToEdit,
            onCancel = onCancelEdit,
            onSave = onSaveVoiture
        )
    } else {
        AdminListScreen(
            voitures = voitures,
            onBack = onBack,
            onAddClicked = onAddClicked,
            onEditClicked = onEditClicked,
            onDeleteClicked = onDeleteClicked
        )
    }
}

@Composable
private fun AdminListScreen(
    voitures: List<Voiture>,
    onBack: () -> Unit,
    onAddClicked: () -> Unit,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Long) -> Unit
) {
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
            TextButton(onClick = onBack) {
                Text(
                    "<- Menu",
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
            onClick = onAddClicked,
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(voitures) { voiture ->
                AdminVoitureCard(
                    voiture = voiture,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked
                )
            }
        }
    }
}

@Composable
private fun AdminVoitureCard(
    voiture: Voiture,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Long) -> Unit
) {
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

            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    "${voiture.marque} ${voiture.modele}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "${voiture.annee} - ${voiture.prixParJour}$/jour - ${if (voiture.estDisponible) "Disponible" else "Indisponible"}",
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onEditClicked(voiture.id) },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Modifier", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { onDeleteClicked(voiture.id) },
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
