package com.example.ptc1.RecyclerViewListAlumnos

import PTC.quickly.R
import PTC.quickly.ViewHolderAsistencia
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.tbAsistencia

class AdaptadorAsistencia(var listaAsistencia: List<tbAsistencia>) : RecyclerView.Adapter<ViewHolderAsistencia>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAsistencia {
        TODO("Not yet implemented")
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_asistencia, parent, false)
        return ViewHolderAsistencia(vista)
    }
    override fun getItemCount() = listaAsistencia.size

    override fun onBindViewHolder(holder: ViewHolderAsistencia, position: Int) {
        val asistencia = listaAsistencia[position]
        holder.txtNombreCard.text = asistencia.nombre

    }
    fun


}