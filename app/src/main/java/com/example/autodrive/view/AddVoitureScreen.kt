package com.example.autodrive.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
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
    var imageUrl by remember { mutableStateOf(TextFieldValue(voiture?.imageUrl ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(voiture?.description ?: "")) }

    Column {

        TextField(value = marque, onValueChange = { marque = it }, label = { Text("Marque") })
        TextField(value = modele, onValueChange = { modele = it }, label = { Text("Modèle") })
        TextField(value = prix, onValueChange = { prix = it }, label = { Text("Prix par jour") })
        TextField(value = annee, onValueChange = { annee = it }, label = { Text("Année") })
        TextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

        Button(onClick = {

            val voitureFinale = Voiture(
                id = voiture?.id ?: 0,
                marque = marque.text,
                modele = modele.text,
                annee = annee.text.toInt(),
                prixParJour = prix.text.toDouble(),
                estDisponible = true,
                imageUrl = imageUrl.text,
                ageMinimum = 18,
                description = description.text
            )

            onSave(voitureFinale)

        }) {
            Text(if (voiture == null) "Ajouter voiture" else "Modifier voiture")
        }
    }
}