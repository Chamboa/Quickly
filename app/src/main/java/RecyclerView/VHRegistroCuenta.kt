package com.example.ptc1.RecyclerViewRegistroCuenta

import PTC.quickly.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VHRegistroCuenta(view: View): RecyclerView.ViewHolder(view) {

    //En el ViewHolder mando a llamar a los elementos de la card
    val txtNombreCard = view.findViewById<TextView>(R.id.txtNombreCard)
    val btnEditarRegistroCuenta = view.findViewById<ImageView>(R.id.btnEditarCuentaRegistro)
    val btnBorrarRegistroCuenta = view.findViewById<ImageView>(R.id.btnBorrarCuentaRegistro)

}