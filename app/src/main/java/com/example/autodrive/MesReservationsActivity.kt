package com.example.autodrive

import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
    private lateinit var reservationAdapter: ReservationAdapter
    private var reservationsState by mutableStateOf<List<ReservationWithVoiture>>(emptyList())
    private var totalState by mutableStateOf(0.0)
    private var countState by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MesReservationsPresenter(
            this,
            ReservationRepository(applicationContext),
            UserSession(applicationContext)
        )
        reservationAdapter = ReservationAdapter(::annulerReservation)
        presenter.chargerReservations()

        setContent {
            AutoDriveTheme {
                MesReservationsScreen(
                    reservations = reservationsState,
                    total = totalState,
                    count = countState,
                    adapter = reservationAdapter,
                    onBack = ::finish
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
}

@Composable
private fun MesReservationsScreen(
    reservations: List<ReservationWithVoiture>,
    total: Double,
    count: Int,
    adapter: ReservationAdapter,
    onBack: () -> Unit
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
                    "Retour",
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Text(
                "Mes reservations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.width(64.dp))
        }

        Divider(color = Color(0xFFEEEEEE))

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
                        "Reservations",
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
                        "$total $",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "Total depense",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    this.adapter = adapter
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }
            },
            update = {
                adapter.submitList(reservations)
            }
        )
    }
}
