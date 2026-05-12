package com.example.autodrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autodrive.model.entity.ReservationWithVoiture
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.MesReservationsPresenter
import com.example.autodrive.presenter.contract.MesReservationsContract
import com.example.autodrive.ui.theme.AutoDriveTheme
import com.example.autodrive.view.ReservationAdapter

class MesReservationsActivity : ComponentActivity(), MesReservationsContract.View {

    private lateinit var presenter: MesReservationsPresenter
    private var reservationsState by mutableStateOf<List<ReservationWithVoiture>>(emptyList())
    private var totalState by mutableStateOf(0.0)
    private var countState by mutableStateOf(0)
    private var filtreStatut by mutableStateOf("TOUS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filtreSauvegarde = savedInstanceState?.getString("filtre_statut") ?: "TOUS"

        presenter = MesReservationsPresenter(
            this,
            ReservationRepository(applicationContext),
            UserSession(applicationContext)
        )
        presenter.chargerReservations()
        presenter.changerFiltre(filtreSauvegarde)

        setContent {
            AutoDriveTheme {
                MesReservationsScreen(
                    reservations = reservationsState,
                    total = totalState,
                    count = countState,
                    filtreStatut = filtreStatut,
                    onBack = ::finish,
                    onFiltreChange = presenter::changerFiltre,
                    onAnnulerReservation = ::annulerReservation
                )
            }
        }
    }

    private fun annulerReservation(reservationId: Long) {
        presenter.annulerReservation(reservationId)
    }

    override fun afficherReservations(reservations: List<ReservationWithVoiture>) {
        reservationsState = reservations
    }

    override fun afficherStatistiques(total: Double, count: Int) {
        totalState = total
        countState = count
    }

    override fun afficherFiltre(statut: String) {
        filtreStatut = statut
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filtre_statut", filtreStatut)
    }
}

@Composable
private fun MesReservationsScreen(
    reservations: List<ReservationWithVoiture>,
    total: Double,
    count: Int,
    filtreStatut: String,
    onBack: () -> Unit,
    onFiltreChange: (String) -> Unit,
    onAnnulerReservation: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF0F0F0), CircleShape)
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    "Mes réservations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$count",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "Réservations",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp),
                    color = Color(0xFFEEEEEE)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format("%.2f", total) + " $",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "Total dépensé",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltreChip(
                text = "Tous",
                isSelected = filtreStatut == "TOUS",
                onClick = { onFiltreChange("TOUS") },
                modifier = Modifier.weight(1f)
            )
            FiltreChip(
                text = "Actives",
                isSelected = filtreStatut == "ACTIVE",
                onClick = { onFiltreChange("ACTIVE") },
                modifier = Modifier.weight(1f)
            )
            FiltreChip(
                text = "Terminées",
                isSelected = filtreStatut == "TERMINEE",
                onClick = { onFiltreChange("TERMINEE") },
                modifier = Modifier.weight(1f)
            )
            FiltreChip(
                text = "Annulées",
                isSelected = filtreStatut == "ANNULEE",
                onClick = { onFiltreChange("ANNULEE") },
                modifier = Modifier.weight(1f)
            )
        }

        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aucune réservation",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF888888)
                )
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                factory = { context ->
                    RecyclerView(context).apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ReservationAdapter(onAnnulerReservation).also {
                            it.submitList(reservations)
                        }
                    }
                },
                update = { recyclerView ->
                    val reservationAdapter = recyclerView.adapter as? ReservationAdapter
                    if (reservationAdapter == null) {
                        recyclerView.adapter = ReservationAdapter(onAnnulerReservation).also {
                            it.submitList(reservations)
                        }
                    } else {
                        reservationAdapter.submitList(reservations)
                    }
                }
            )
        }
    }
}

@Composable
private fun FiltreChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun ReservationCard(
    reservation: ReservationWithVoiture,
    onAnnuler: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${reservation.marque} ${reservation.modele}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Du ${reservation.dateDebut} au ${reservation.dateFin}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }

                StatutBadge(statut = reservation.statut)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ${String.format("%.2f", reservation.coutTotal)} $",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (reservation.statut == "ACTIVE") {
                    OutlinedButton(
                        onClick = onAnnuler,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFC62828)
                        )
                    ) {
                        Text("Annuler", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatutBadge(statut: String) {
    val (bgColor, textColor, texte) = when (statut) {
        "ACTIVE" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Active")
        "TERMINEE" -> Triple(Color(0xFFF5F5F5), Color(0xFF888888), "Terminée")
        "ANNULEE" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Annulée")
        else -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), statut)
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            texte,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}
