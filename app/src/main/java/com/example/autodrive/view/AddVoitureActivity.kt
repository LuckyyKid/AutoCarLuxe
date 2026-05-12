package com.example.autodrive.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.entity.Voiture

@Composable
fun AddVoitureScreen(
    voiture: Voiture? = null,
    onCancel: () -> Unit,
    onSave: (
        id: Long,
        marque: String,
        modele: String,
        annee: String,
        prixParJour: String,
        estDisponible: Boolean,
        imageUrls: String,
        description: String
    ) -> Unit
) {

    var marque by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.marque ?: "")) }
    var modele by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.modele ?: "")) }
    var prix by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.prixParJour?.toString() ?: "")) }
    var annee by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.annee?.toString() ?: "")) }
    var imageUrls by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.imageUrls ?: "")) }
    var description by rememberSaveable(voiture?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(voiture?.description ?: "")) }
    var estDisponible by rememberSaveable(voiture?.id) { mutableStateOf(voiture?.estDisponible ?: true) }


    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color(0xFFDDDDDD),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color(0xFF888888),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = Color(0xFF1A1A1A),
        unfocusedTextColor = Color(0xFF1A1A1A)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    "Retour",
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            if (voiture == null) "Ajouter une voiture" else "Modifier la voiture",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Renseignez les informations du véhicule",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = marque,
            onValueChange = { marque = it },
            label = { Text("Marque") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = modele,
            onValueChange = { modele = it },
            label = { Text("Modèle") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = prix,
                onValueChange = { prix = it },
                label = { Text("Prix/jour ($)") },
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = annee,
                onValueChange = { annee = it },
                label = { Text("Année") },
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = imageUrls,
            onValueChange = { imageUrls = it },
            label = { Text("Images (URLs séparées par des virgules)") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Disponible à la location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1A1A1A)
                )
                Checkbox(
                    checked = estDisponible,
                    onCheckedChange = { estDisponible = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color(0xFF888888)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                onSave(
                    voiture?.id ?: 0L,
                    marque.text,
                    modele.text,
                    annee.text,
                    prix.text,
                    estDisponible,
                    imageUrls.text,
                    description.text
                )
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                if (voiture == null) "Ajouter la voiture" else "Enregistrer les modifications",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onCancel,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                "Annuler",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
