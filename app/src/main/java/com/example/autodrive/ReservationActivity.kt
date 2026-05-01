package com.example.autodrive

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.ReservationPresenter
import com.example.autodrive.presenter.contract.ReservationContract
import com.example.autodrive.ui.theme.AutoDriveTheme
import java.time.LocalDate
import java.util.Calendar

class ReservationActivity : ComponentActivity(), ReservationContract.View {

    private lateinit var presenter: ReservationPresenter
    private lateinit var voiture: Voiture
    private var coutTotalState by mutableStateOf(0.0)
    private var messageState by mutableStateOf("")
    private var dateDebutState by mutableStateOf("")
    private var dateFinState by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coutTotalState = savedInstanceState?.getDouble("cout_total") ?: 0.0
        messageState = savedInstanceState?.getString("message_state") ?: ""
        dateDebutState = savedInstanceState?.getString("date_debut") ?: ""
        dateFinState = savedInstanceState?.getString("date_fin") ?: ""

        @Suppress("DEPRECATION")
        voiture = intent.getSerializableExtra("voiture") as Voiture
        presenter = ReservationPresenter(
            this,
            ReservationRepository(applicationContext),
            UtilisateurRepository(applicationContext),
            UserSession(applicationContext)
        )
        presenter.initialiserUtilisateur()

        setContent {
            AutoDriveTheme {
                ReservationScreen(
                    voiture = voiture,
                    dateDebut = dateDebutState,
                    dateFin = dateFinState,
                    coutTotal = coutTotalState,
                    message = messageState,
                    onBack = ::finish,
                    onChooseDateDebut = ::ouvrirDateDebut,
                    onChooseDateFin = ::ouvrirDateFin,
                    onConfirm = ::confirmerReservation
                )
            }
        }
    }

    private fun ouvrirDateDebut() {
        ouvrirDatePicker { date ->
            dateDebutState = date
            calculerCout()
        }
    }

    private fun ouvrirDateFin() {
        ouvrirDatePicker { date ->
            dateFinState = date
            calculerCout()
        }
    }

    private fun ouvrirDatePicker(onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                onDateSelected(LocalDate.of(year, month + 1, day).toString())
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun calculerCout() {
        presenter.calculerCout(
            dateDebutState.ifBlank { null },
            dateFinState.ifBlank { null },
            voiture.prixParJour
        )
    }

    private fun confirmerReservation() {
        messageState = ""
        presenter.confirmerReservation(
            voitureId = voiture.id,
            disponible = voiture.estDisponible,
            dateDebut = dateDebutState.ifBlank { null },
            dateFin = dateFinState.ifBlank { null },
            prixParJour = voiture.prixParJour
        )
    }

    override fun afficherCout(coutTotal: Double) {
        coutTotalState = coutTotal
    }

    override fun afficherMessage(message: String) {
        messageState = message
    }

    override fun reservationConfirmee() {
        messageState = ""
        Toast.makeText(this, "Reservation confirmee", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MesReservationsActivity::class.java))
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("cout_total", coutTotalState)
        outState.putString("message_state", messageState)
        outState.putString("date_debut", dateDebutState)
        outState.putString("date_fin", dateFinState)
    }
}

@Composable
private fun ReservationScreen(
    voiture: Voiture,
    dateDebut: String,
    dateFin: String,
    coutTotal: Double,
    message: String,
    onBack: () -> Unit,
    onChooseDateDebut: () -> Unit,
    onChooseDateFin: () -> Unit,
    onConfirm: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    "Retour",
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Reservation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        Divider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "${voiture.marque} ${voiture.modele}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${voiture.prixParJour} $/jour",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Choisir les dates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onChooseDateDebut,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    if (dateDebut.isBlank()) "Choisir date debut" else "Debut : $dateDebut",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onChooseDateFin,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    if (dateFin.isBlank()) "Choisir date fin" else "Fin : $dateFin",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Cout total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888)
                    )
                    Text(
                        "$coutTotal $",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Confirmer la reservation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Retour",
                    color = Color(0xFF888888),
                    fontWeight = FontWeight.Medium
                )
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        message,
                        modifier = Modifier.padding(14.dp),
                        color = Color(0xFFC62828),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
