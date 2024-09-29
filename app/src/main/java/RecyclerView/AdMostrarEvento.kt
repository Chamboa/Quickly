package com.example.ptc1.RecyclerView

import PTC.quickly.Asistencia_Alumno
import com.example.ptc1.modelo.DTEvento
import PTC.quickly.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdMostrarEvento(private var datosEvento: List<DTEvento>) : RecyclerView.Adapter<VHMostrarEvento>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHMostrarEvento {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_mostrarevento, parent, false)
        return VHMostrarEvento(vista)
    }

    override fun getItemCount(): Int {
        return datosEvento.size
    }

    override fun onBindViewHolder(holder: VHMostrarEvento, position: Int) {
        val item = datosEvento[position]
        holder.txtNombreEvento.text = item.nombre
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            selectedUUID = item.UUID
            selectedNombre = item.nombre
            val intent = Intent(context, Asistencia_Alumno::class.java)
            intent.putExtra("UUID_Evento", item.UUID)
            intent.putExtra("nombre", item.nombre)
            context.startActivity(intent)
        }
    }

    companion object {
        var selectedUUID: String? = null
        var selectedNombre: String? = null
    }
}
