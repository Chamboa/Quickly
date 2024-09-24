package com.example.ptc1.RecyclerView

import PTC.quickly.Login
import PTC.quickly.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcChat
import android.view.Gravity

class AdMensaje(
    private var listaMensajes: MutableList<dcChat>,
    val UUID_usuarioLogueado: String = ""
) : RecyclerView.Adapter<AdMensaje.MensajeViewHolder>() {

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMensaje: TextView = itemView.findViewById(R.id.tvMensaje)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val cardMensaje: CardView = itemView.findViewById(R.id.cardMensaje)
        val containerMensaje: LinearLayout = itemView.findViewById(R.id.containerMensaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = listaMensajes[position]
        holder.tvMensaje.text = mensaje.mensaje
        holder.tvFecha.text = mensaje.fecha

        // Obtén el UUID del remitente de este mensaje
        val UUID_remitenteMensaje = mensaje.UUID_remitente

        println("UUID del remitente del mensaje: $UUID_remitenteMensaje")
        println("UUID del usuario logueado: $UUID_usuarioLogueado")

        // Ajuste de márgenes y alineación de la CardView completa
        val layoutParams = holder.cardMensaje.layoutParams as RecyclerView.LayoutParams

        if (UUID_remitenteMensaje == UUID_usuarioLogueado) {
            // Mensaje enviado por el usuario (alinear CardView a la derecha)
            holder.containerMensaje.gravity = Gravity.END
            layoutParams.marginEnd = 16 // Margen a la derecha
            layoutParams.marginStart = 850 // Margen a la izquierda
            holder.cardMensaje.layoutParams = layoutParams

            // Cambiar el color de fondo para los mensajes enviados
            holder.cardMensaje.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_gren)
            )
        } else {
            // Mensaje recibido (alinear CardView a la izquierda)
            holder.containerMensaje.gravity = Gravity.START
            layoutParams.marginStart = 16 // Margen a la izquierda
            layoutParams.marginEnd = 500 // Margen a la derecha
            holder.cardMensaje.layoutParams = layoutParams

            // Cambiar el color de fondo para los mensajes recibidos
            holder.cardMensaje.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_blue)
            )
        }
    }

    override fun getItemCount(): Int {
        return listaMensajes.size
    }

    fun actualizarMensajes(nuevaLista: List<dcChat>) {
        listaMensajes.clear()
        listaMensajes.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    fun agregarMensaje(mensaje: dcChat) {
        listaMensajes.add(mensaje)
        notifyItemInserted(listaMensajes.size - 1)
    }
}
