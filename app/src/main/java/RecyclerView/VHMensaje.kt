package com.example.ptc1.RecyclerView

import PTC.quickly.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VHMensaje(vista: View) : RecyclerView.ViewHolder(vista){
    val tvMensaje: TextView = vista.findViewById(R.id.textMensaje)
    val tvHora: TextView = vista.findViewById(R.id.horaMensaje)

}