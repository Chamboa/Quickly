package com.example.ptc1.RecyclerView

import PTC.quickly.Bandeja_chat
import PTC.quickly.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcUsuario

class AdChat(var DatosChat: List<dcUsuario>) : RecyclerView.Adapter<VHChat>() {
    companion object {
        var UUID = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHChat {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_chat, parent, false)
        return VHChat(vista)
    }

    override fun getItemCount(): Int {
        return DatosChat.size
    }

    override fun onBindViewHolder(holder: VHChat, position: Int) {
        val item = DatosChat[position]
        holder.tvNombre.text = item.nombre

        holder.itemView.setOnClickListener {
            // Asignar el UUID del ítem clicado a la variable estática
            UUID = item.UUID

            // Crear un Intent para iniciar la actividad Bandeja_chat
            val context = holder.itemView.context
            val intent = Intent(context, Bandeja_chat::class.java)

            // Pasar el UUID como extra en el Intent
            intent.putExtra("UUID", UUID)

            // Iniciar la actividad
            context.startActivity(intent)
        }
    }
}
