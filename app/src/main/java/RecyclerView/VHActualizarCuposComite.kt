package com.example.ptc1.RecyclerView


import PTC.quickly.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VHActualizarCuposComite(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtNombreCard: TextView = itemView.findViewById(R.id.txtNombreCard)
    val txtCuposCard: TextView = itemView.findViewById(R.id.txtCupos)
    val imageButton: ImageView = itemView.findViewById(R.id.imageButton)
    val imageButton2: ImageView = itemView.findViewById(R.id.imageButton2)


}