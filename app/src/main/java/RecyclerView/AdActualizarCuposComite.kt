package com.example.ptc1.RecyclerView

import PTC.quickly.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.dcCuposComite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import PTC.quickly.ActualizarCuposComite
import android.widget.Toast

class AdActualizarCuposComite(var datos: List<dcCuposComite>) : RecyclerView.Adapter<VHActualizarCuposComite>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHActualizarCuposComite {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_actualizar_cupos_comite, parent, false)
        return VHActualizarCuposComite(vista)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: VHActualizarCuposComite, position: Int) {
        val item = datos[position]
        holder.txtNombreCard.text = item.comite
        holder.txtCuposCard.text = item.cupos.toString()

        holder.imageButton.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Actualizar cupos")

            val inflater = LayoutInflater.from(holder.itemView.context)
            val dialogView = inflater.inflate(R.layout.activity_dialog_editar_comite, null)
            builder.setView(dialogView)

            val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
            val editDescripcion = dialogView.findViewById<EditText>(R.id.editDescripcion)
            val editCupos = dialogView.findViewById<EditText>(R.id.editCupos)

            editNombre.setText(item.comite)
            editDescripcion.setText(item.descripcion)
            editCupos.setText(item.cupos.toString())

            builder.setPositiveButton("Actualizar") { _, _ ->
                val nuevoNombre = editNombre.text.toString()
                val nuevaDescripcion = editDescripcion.text.toString()
                val nuevosCupos = editCupos.text.toString().toIntOrNull() ?: item.cupos

                CoroutineScope(Dispatchers.Main).launch {
                    val activity = holder.itemView.context as ActualizarCuposComite
                    val result = activity.actualizarComite(item.id_comite, nuevoNombre, nuevaDescripcion, nuevosCupos)
                    if (result) {
                        item.comite = nuevoNombre
                        item.descripcion = nuevaDescripcion
                        item.cupos = nuevosCupos
                        notifyItemChanged(position)
                        Toast.makeText(holder.itemView.context, "Comité actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Error al actualizar el comité", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        holder.imageButton2.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estás seguro de que quieres eliminar este comité?")
            builder.setPositiveButton("Sí") { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    val activity = holder.itemView.context as ActualizarCuposComite
                    val result = activity.eliminarComite(item.id_comite)
                    if (result) {
                        (datos as MutableList).removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(holder.itemView.context, "Comité eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Error al eliminar el comité", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("No", null)
            builder.show()
        }
    }
}
