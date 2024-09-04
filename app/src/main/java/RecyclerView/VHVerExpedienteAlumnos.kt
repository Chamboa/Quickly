package com.example.ptc1.RecyclerView

import android.view.View
import PTC.quickly.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class VHVerExpedienteAlumnos(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtNombreCard = itemView.findViewById<TextView>(R.id.txtNombreAlumnoExpediente)
}
