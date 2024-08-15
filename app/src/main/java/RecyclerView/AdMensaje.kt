package com.example.ptc1.RecyclerView

import PTC.quickly.Bandeja_chat
import PTC.quickly.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcChat

class AdMensaje(var DatosMensaje: MutableList<dcChat>) : RecyclerView.Adapter<VHMensaje>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHMensaje {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_mensaje, parent, false)
        return VHMensaje(vista)
    }

    override fun getItemCount(): Int {
        return DatosMensaje.size
    }

    override fun onBindViewHolder(holder: VHMensaje, position: Int) {
        val item = DatosMensaje[position]
        holder.tvMensaje.text = item.mensaje
        holder.tvHora.text = item.fecha

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Bandeja_chat::class.java)
            context.startActivity(intent)
        }
    }

    // Método para agregar un mensaje y notificar el cambio
    fun agregarMensaje(nuevoMensaje: dcChat) {
        DatosMensaje.add(nuevoMensaje)
        notifyItemInserted(DatosMensaje.size - 1)
    }
}
