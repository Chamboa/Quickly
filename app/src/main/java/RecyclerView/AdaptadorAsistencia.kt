package com.example.ptc1.RecyclerViewListAlumnos

import PTC.quickly.Agregar_Horas
import PTC.quickly.R
import PTC.quickly.ViewHolderAsistencia
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.tbAsistencia
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement

class AdaptadorAsistencia(var listaAsistencia: List<tbAsistencia>) : RecyclerView.Adapter<ViewHolderAsistencia>() {

    companion object {
        var selectedUUIDA: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAsistencia {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_card_asistencia, parent, false)
        return ViewHolderAsistencia(vista)
    }

    override fun getItemCount() = listaAsistencia.size

    override fun onBindViewHolder(holder: ViewHolderAsistencia, position: Int) {
        val asistencia = listaAsistencia[position]
        holder.txtNombreCard.text = asistencia.nombre

        // Deshabilita el botón imgAgregar_Horas inicialmente
        holder.imgAgregar_Horas.isEnabled = false

        holder.btncheckBoxsi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.btncheckBoxno.isChecked = false
                holder.imgAgregar_Horas.isEnabled = true // Habilita el botón si se selecciona btncheckBoxsi
                updateAsistencia(holder.itemView.context, asistencia.id_comite, true)
            } else {
                holder.imgAgregar_Horas.isEnabled = false // Deshabilita el botón si se deselecciona btncheckBoxsi
                updateAsistencia(holder.itemView.context, asistencia.id_comite, false)
            }
        }

        holder.btncheckBoxno.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.btncheckBoxsi.isChecked = false
                holder.imgAgregar_Horas.isEnabled = false // Deshabilita el botón si se selecciona btncheckBoxno
                updateAsistencia(holder.itemView.context, asistencia.id_comite, false)
            } else if (!holder.btncheckBoxsi.isChecked) {
                holder.imgAgregar_Horas.isEnabled = false // Asegúrate de que el botón siga deshabilitado si ambos checkbox están deseleccionados
            }
        }

        holder.imgAgregar_Horas.setOnClickListener {
            if (holder.imgAgregar_Horas.isEnabled) {
                selectedUUIDA = asistencia.UUIDxd // Guarda el UUID del usuario seleccionado
                val intent = Intent(holder.itemView.context, Agregar_Horas::class.java)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    private fun updateAsistencia(context: Context, idComite: Int, asistio: Boolean) {
        var objConexion: Connection? = null
        var statement: PreparedStatement? = null
        try {
            // Aquí se implementa la lógica para actualizar la base de datos Oracle
            objConexion = ClaseConexion().cadenaConexion()

            // Preparar la consulta SQL
            statement = objConexion?.prepareStatement(
                "UPDATE Asistencia SET asistio = ? WHERE id_comite = ?"
            )

            // Establecer los parámetros de la consulta
            statement?.setBoolean(1, asistio)
            statement?.setInt(2, idComite)

            // Ejecutar la actualización
            statement?.executeUpdate()

            // Mostrar un mensaje de confirmación
            val mensaje = if (asistio) "Marcado como asistió" else "Marcado como no asistió"
            Toast.makeText(context, "Comité $idComite: $mensaje", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            objConexion?.rollback()
        } finally {
            // Cerrar la declaración y la conexión
            statement?.close()
            objConexion?.close()
        }
    }
}
