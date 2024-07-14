package com.example.ptc1.RecyclerViewListAlumnos

import PTC.quickly.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ViewHolderAsistencia(view: View) : RecyclerView.ViewHolder(view) {

    val txtNombreCard = view.findViewById<TextView>(R.id.txtAsistencia)

    val imgBuscar: ImageView =view.findViewById(R.id.imgBuscar)

}