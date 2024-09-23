package com.example.ptc1.RecyclerView

import PTC.quickly.Login
import PTC.quickly.R
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcChat

class AdMensaje(
    private var listaMensajes: MutableList<dcChat>,
    private val UUID_usuarioLogueado: String // El UUID del usuario que inició sesión
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

        val layoutParams = holder.cardMensaje.layoutParams as ViewGroup.MarginLayoutParams

        // Verificamos si el mensaje fue enviado por el usuario logueado
        if (mensaje.UUID_remitente == UUID_usuarioLogueado) {
            // Mensaje enviado por el usuario (alinear a la derecha)
            holder.containerMensaje.gravity = Gravity.END
            layoutParams.marginEnd = 16 // Margen a la derecha
            layoutParams.marginStart = 10 // Pequeño margen a la izquierda
            holder.cardMensaje.layoutParams = layoutParams
            holder.cardMensaje.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.mensaje_enviado)
            )
        } else {
            // Mensaje recibido (alinear a la izquierda)
            holder.containerMensaje.gravity = Gravity.START
            layoutParams.marginStart = 16 // Margen a la izquierda
            layoutParams.marginEnd = 10 // Pequeño margen a la derecha
            holder.cardMensaje.layoutParams = layoutParams
            holder.cardMensaje.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.mensaje_recibido)
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
