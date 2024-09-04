package com.example.ptc1.RecyclerView

import PTC.quickly.Asistencia_Alumno
import PTC.quickly.R
import PTC.quickly.VerExpediente
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdMostrarEvento.Companion.selectedNombre
import com.example.ptc1.RecyclerView.AdMostrarEvento.Companion.selectedUUID
import com.example.ptc1.modelo.dcExpedientes
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream

class AdVerExpedienteAlumnos(private var datosExpedientes: List<dcExpedientes>) : RecyclerView.Adapter<VHVerExpedienteAlumnos>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHVerExpedienteAlumnos {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_ver_expediente_alumnos, parent, false)
        return VHVerExpedienteAlumnos(vista)
    }

    override fun getItemCount(): Int {
        return datosExpedientes.size
    }

    override fun onBindViewHolder(holder: VHVerExpedienteAlumnos, position: Int) {
        val item = datosExpedientes[position]
        holder.txtNombreCard.text = item.nombreUsuario
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            UUID_alumno = item.UUID
            nombreAlumno = item.nombreEvento
            val intent = Intent(context, VerExpediente::class.java)
            intent.putExtra("UUID_Usuario", item.UUID)
            intent.putExtra("nombre", item.nombreEvento)
            context.startActivity(intent)
            println("UUID: $UUID_alumno")
        }
    }

    companion object
    {
        var UUID_alumno: String? = null
        var nombreAlumno: String? = null


    }
}