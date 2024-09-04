package com.example.ptc1.RecyclerView

import PTC.quickly.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VHMostrarEvento(view: View) : RecyclerView.ViewHolder(view) {
    val txtNombreEvento: TextView = view.findViewById(R.id.txtNombreMostrarEvento)
}