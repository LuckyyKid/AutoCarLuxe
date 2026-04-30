package com.example.autodrive.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autodrive.R
import com.example.autodrive.model.entity.ReservationWithVoiture

class ReservationAdapter(
    private val onAnnuler: (Long) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    private var reservations: List<ReservationWithVoiture> = emptyList()

    fun submitList(nouvellesReservations: List<ReservationWithVoiture>) {
        reservations = nouvellesReservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view, onAnnuler)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    class ReservationViewHolder(
        itemView: View,
        private val onAnnuler: (Long) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textVoiture: TextView = itemView.findViewById(R.id.textVoiture)
        private val textStatut: TextView = itemView.findViewById(R.id.textStatut)
        private val textDates: TextView = itemView.findViewById(R.id.textDates)
        private val textCout: TextView = itemView.findViewById(R.id.textCout)
        private val buttonAnnuler: Button = itemView.findViewById(R.id.buttonAnnuler)

        fun bind(reservation: ReservationWithVoiture) {
            textVoiture.text = "${reservation.marque} ${reservation.modele}"
            textStatut.text = reservation.statut
            textDates.text = "Du ${reservation.dateDebut} au ${reservation.dateFin}"
            textCout.text = "${reservation.coutTotal} $"

            val badge = GradientDrawable()
            badge.cornerRadius = 50f

            when (reservation.statut) {
                "ACTIVE" -> {
                    badge.setColor(Color.parseColor("#E8F5E9"))
                    textStatut.setTextColor(Color.parseColor("#2E7D32"))
                }
                "TERMINEE" -> {
                    badge.setColor(Color.parseColor("#F5F5F5"))
                    textStatut.setTextColor(Color.parseColor("#888888"))
                }
                else -> {
                    badge.setColor(Color.parseColor("#FFEBEE"))
                    textStatut.setTextColor(Color.parseColor("#C62828"))
                }
            }

            textStatut.background = badge

            if (reservation.statut == "ACTIVE") {
                buttonAnnuler.visibility = View.VISIBLE
                buttonAnnuler.setOnClickListener {
                    onAnnuler(reservation.id)
                }
            } else {
                buttonAnnuler.visibility = View.GONE
                buttonAnnuler.setOnClickListener(null)
            }
        }
    }
}
