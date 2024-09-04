package com.example.ptc1.RecyclerViewRegistroCuenta

import PTC.quickly.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.tbUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class AdRegistroCuenta(var Datos: List<tbUsuario>) : RecyclerView.Adapter<VHRegistroCuenta>() {

    data class Comite(val idComite: Int, val nombre: String)

    fun actualizarPantallaRegistroCuenta(uuid: String, nuevaContraseña: String, nuevoCorreo: String, nuevoIdComite: Int) {
        val index = Datos.indexOfFirst { it.uuid == uuid }
        val listaDatos = Datos.toMutableList()
        listaDatos[index] = listaDatos[index].copy(
            contraseña = nuevaContraseña,
            correoElectronico = nuevoCorreo,
            idComite = nuevoIdComite
        )
        Datos = listaDatos
        notifyDataSetChanged()
    }

    fun eliminarRegistroCuenta(uuid: String, position: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(position)
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().cadenaConexion()
            val borrarRegistroCuenta = objConexion?.prepareStatement("DELETE FROM Usuario WHERE uuid = ?")!!
            borrarRegistroCuenta.setString(1, uuid)
            borrarRegistroCuenta.executeUpdate()
            val commit = objConexion.prepareStatement("COMMIT")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun editarRegistroUsuario(uuid: String, nuevaContraseña: String, nuevoCorreo: String, nuevoIdComite: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val updateRegistroCuenta = objConexion?.prepareStatement(
                "UPDATE Usuario SET contraseña = ?, correo_electronico = ?, id_comite = ? WHERE uuid = ?"
            )!!
            updateRegistroCuenta.setString(1, nuevaContraseña)
            updateRegistroCuenta.setString(2, nuevoCorreo)
            updateRegistroCuenta.setInt(3, nuevoIdComite)
            updateRegistroCuenta.setString(4, uuid)
            updateRegistroCuenta.executeUpdate()

            val commit = objConexion.prepareStatement("COMMIT")
            commit.executeUpdate()

            withContext(Dispatchers.Main) {
                actualizarPantallaRegistroCuenta(uuid, nuevaContraseña, nuevoCorreo, nuevoIdComite)
            }
        }
    }

    fun obtenerComites(): List<Comite> {
        val comites = mutableListOf<Comite>()
        val objConexion = ClaseConexion().cadenaConexion()
        val query = objConexion?.prepareStatement("SELECT id_comite, comite FROM Comite")
        val resultSet = query?.executeQuery()

        while (resultSet?.next() == true) {
            val idComite = resultSet.getInt("id_comite")
            val nombreComite = resultSet.getString("comite")
            comites.add(Comite(idComite, nombreComite))
        }

        return comites
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHRegistroCuenta {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_registro_cuenta, parent, false)
        return VHRegistroCuenta(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: VHRegistroCuenta, position: Int) {
        val item = Datos[position]
        holder.txtNombreCard.text = item.nombre

        holder.btnBorrarRegistroCuenta.setOnClickListener {
            val contexto = holder.txtNombreCard.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estás seguro que deseas eliminar este usuario?")

            builder.setPositiveButton("Sí") { dialog, which ->
                eliminarRegistroCuenta(item.uuid, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        holder.btnEditarRegistroCuenta.setOnClickListener {
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Usuario")

            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.activity_alert_dialog, null)

            val etContraseña = dialogView.findViewById<EditText>(R.id.etContraseña)
            val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
            val spinnerComite = dialogView.findViewById<Spinner>(R.id.spinnerComite)

            // Obtener la lista de comités
            val comites = obtenerComites()

            // Configurar el spinner
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, comites.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerComite.adapter = adapter

            // Establecer valores actuales
            etContraseña.setText(item.contraseña)
            etCorreo.setText(item.correoElectronico)
            val comiteIndex = comites.indexOfFirst { it.idComite == item.idComite }
            if (comiteIndex != -1) {
                spinnerComite.setSelection(comiteIndex)
            }

            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar") { dialog, which ->
                val nuevaContraseña = etContraseña.text.toString()
                val nuevoCorreo = etCorreo.text.toString()
                val nuevoIdComite = comites[spinnerComite.selectedItemPosition].idComite

                if (nuevaContraseña.isNotEmpty() && nuevoCorreo.isNotEmpty()) {
                    editarRegistroUsuario(item.uuid, nuevaContraseña, nuevoCorreo, nuevoIdComite)
                } else {
                    // Mostrar un mensaje de error si los campos están vacíos
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}