package com.example.autodrive.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.autodrive.model.entity.Voiture

@Composable
fun AddVoitureScreen(
    voiture: Voiture? = null,
    onSave: (Voiture) -> Unit
) {

    var marque by remember { mutableStateOf(TextFieldValue(voiture?.marque ?: "")) }
    var modele by remember { mutableStateOf(TextFieldValue(voiture?.modele ?: "")) }
    var prix by remember { mutableStateOf(TextFieldValue(voiture?.prixParJour?.toString() ?: "")) }
    var annee by remember { mutableStateOf(TextFieldValue(voiture?.annee?.toString() ?: "")) }


    var imageUrls by remember { mutableStateOf(TextFieldValue(voiture?.imageUrls ?: "")) }

    var description by remember { mutableStateOf(TextFieldValue(voiture?.description ?: "")) }

    var estDisponible by remember {
        mutableStateOf(voiture?.estDisponible ?: true)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(
            value = marque,
            onValueChange = { marque = it },
            label = { Text("Marque") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = modele,
            onValueChange = { modele = it },
            label = { Text("Modèle") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = prix,
            onValueChange = { prix = it },
            label = { Text("Prix par jour") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = annee,
            onValueChange = { annee = it },
            label = { Text("Année") },
            modifier = Modifier.fillMaxWidth()
        )


        TextField(
            value = imageUrls,
            onValueChange = { imageUrls = it },
            label = { Text("Images (URLs séparées par des virgules)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = estDisponible,
                onCheckedChange = { estDisponible = it }
            )
            Text("Disponible")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                val voitureFinale = Voiture(
                    id = voiture?.id ?: 0,
                    marque = marque.text,
                    modele = modele.text,
                    annee = annee.text.toInt(),
                    prixParJour = prix.text.toDouble(),
                    estDisponible = estDisponible,
                    imageUrls = imageUrls.text,
                    ageMinimum = 18,
                    description = description.text
                )

                onSave(voitureFinale)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (voiture == null) "Ajouter voiture" else "Modifier voiture")
        }
    }
}