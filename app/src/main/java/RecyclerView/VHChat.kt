package com.example.ptc1.RecyclerView

import PTC.quickly.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VHChat(view: View) : RecyclerView.ViewHolder(view) {
    val tvNombre: TextView = view.findViewById(R.id.txtLlenado)
    val imgPerfil: ImageView = view.findViewById(R.id.imgFotoPerfil)
    val btnChat: ImageView = view.findViewById(R.id.VwChat)

}