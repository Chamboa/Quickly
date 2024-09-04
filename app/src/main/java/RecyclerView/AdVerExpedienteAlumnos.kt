package com.example.ptc1.RecyclerView

import PTC.quickly.R
import PTC.quickly.VerExpediente
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcExpedientes

class AdVerExpedienteAlumnos(private var datosExpedientes: List<dcExpedientes>) :
    RecyclerView.Adapter<VHVerExpedienteAlumnos>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHVerExpedienteAlumnos {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_ver_expediente_alumnos, parent, false)
        return VHVerExpedienteAlumnos(vista)
    }

    override fun getItemCount(): Int = datosExpedientes.size

    override fun onBindViewHolder(holder: VHVerExpedienteAlumnos, position: Int) {
        val item = datosExpedientes[position]
        holder.txtNombreCard.text = item.nombreUsuario

        holder.itemView.setOnClickListener {
            // Asignar el UUID del ítem clicado a la variable estática
            UUID_alumno = item.UUID_Alumno

            // También asignamos el UUID al companion object de VerExpediente
            VerExpediente.UUID_USUARIO = item.UUID_Alumno

            // Crear un Intent para iniciar la actividad VerExpediente
            val context = holder.itemView.context
            val intent = Intent(context, VerExpediente::class.java)

            // Pasar el UUID como extra en el Intent (por si acaso)
            intent.putExtra("UUID_Usuario", UUID_alumno)

            // Log para debugging
            Log.d("AdVerExpedienteAlumnos", "UUID seleccionado: $UUID_alumno")

            // Iniciar la actividad
            context.startActivity(intent)
        }
    }

    companion object {
        var UUID_alumno: String? = null
    }
}