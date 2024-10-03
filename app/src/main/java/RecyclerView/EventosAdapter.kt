package com.example.ptc1.RecyclerView

import PTC.quickly.Login
import PTC.quickly.Login.Companion.userRoleId
import PTC.quickly.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.DTEvento

class EventosAdapter(
    private var eventos: List<DTEvento>,  // Cambiamos MutableList por List
    private val onEdit: (DTEvento) -> Unit,
    private val onDelete: (DTEvento) -> Unit
) : RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.bind(evento, onEdit, onDelete)
    }

    override fun getItemCount(): Int = eventos.size

    fun updateList(newEventos: List<DTEvento>) {
        this.eventos = newEventos // Como ahora es List, no necesitamos convertir
        notifyDataSetChanged()
    }

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txNombreEvento: TextView = view.findViewById(R.id.tvNombreEvento)
        private val txDescripcionEvento: TextView = view.findViewById(R.id.tvDescripcionEvento)
        private val txFechaEvento: TextView = view.findViewById(R.id.tvFechaEvento)
        private val txHoraEvento: TextView = view.findViewById(R.id.tvHoraEvento)
        private val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)

        fun bind(evento: DTEvento, onEdit: (DTEvento) -> Unit, onDelete: (DTEvento) -> Unit) {
            txNombreEvento.text = evento.nombre
            txDescripcionEvento.text = evento.descripcion
            txFechaEvento.text = evento.fecha
            txHoraEvento.text = evento.hora

            if (userRoleId == 1) {
                btnEditar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            } else {
                btnEditar.visibility = View.VISIBLE
                btnEliminar.visibility = View.VISIBLE
            }

            btnEditar.setOnClickListener { onEdit(evento) }
            btnEliminar.setOnClickListener { onDelete(evento) }
        }
    }
}
