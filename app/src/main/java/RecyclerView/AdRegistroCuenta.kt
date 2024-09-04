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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class AdRegistroCuenta(var Datos: List<tbUsuario>) : RecyclerView.Adapter<VHRegistroCuenta>() {

    data class Comite(val idComite: Int, val nombre: String)

    // Función para calcular el hash SHA-512 de una contraseña
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val hashedBytes = md.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    fun actualizarPantallaRegistroCuenta(uuid: String, nuevaContraseña: String?, nuevoCorreo: String, nuevoIdComite: Int) {
        val index = Datos.indexOfFirst { it.uuid == uuid }
        val listaDatos = Datos.toMutableList()
        listaDatos[index] = listaDatos[index].copy(
            contraseña = nuevaContraseña ?: listaDatos[index].contraseña, // Usar la contraseña anterior si es nula
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
            val borrarEvento = objConexion?.prepareStatement("DELETE FROM Eventos WHERE UUID_Usuario = ?")!!
            borrarEvento.setString(1, uuid)
            borrarEvento.executeUpdate()
            val borrarReclamo = objConexion?.prepareStatement("DELETE FROM Reclamo WHERE UUID_Usuario = ?")!!
            borrarReclamo.setString(1, uuid)
            borrarReclamo.executeUpdate()
            val borrarExpediente = objConexion?.prepareStatement("DELETE FROM Expediente WHERE UUID_Usuario = ?")!!
            borrarExpediente.setString(1, uuid)
            borrarExpediente.executeUpdate()
            val borrarRegistroCuenta = objConexion?.prepareStatement("DELETE FROM Usuario WHERE UUID_Usuario = ?")!!
            borrarRegistroCuenta.setString(1, uuid)
            borrarRegistroCuenta.executeUpdate()
            val commit = objConexion.prepareStatement("COMMIT")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun editarRegistroUsuario(uuid: String, nuevaContraseña: String?, nuevoCorreo: String, nuevoIdComite: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().cadenaConexion()

            // Si hay nueva contraseña, encriptarla
            val hashedPassword = nuevaContraseña?.let { hashPassword(it) }

            val updateRegistroCuenta = objConexion?.prepareStatement(
                "UPDATE Usuario SET ${if (hashedPassword != null) "contraseña = ?, " else ""}correo_electronico = ?, id_comite = ? WHERE UUID_Usuario = ?"
            )!!

            var paramIndex = 1
            if (hashedPassword != null) {
                updateRegistroCuenta.setString(paramIndex++, hashedPassword)
            }
            updateRegistroCuenta.setString(paramIndex++, nuevoCorreo)
            updateRegistroCuenta.setInt(paramIndex++, nuevoIdComite)
            updateRegistroCuenta.setString(paramIndex, uuid)
            updateRegistroCuenta.executeUpdate()

            val commit = objConexion.prepareStatement("COMMIT")
            commit.executeUpdate()

            withContext(Dispatchers.Main) {
                actualizarPantallaRegistroCuenta(uuid, hashedPassword, nuevoCorreo, nuevoIdComite)
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
            val spinnerComite = dialogView.findViewById<Spinner>(R.id.spCommite)

            // Dejar el campo de contraseña en blanco y establecer el correo
            etContraseña.setText("")
            etCorreo.setText(item.correoElectronico)

            // Obtener la lista de comités y establecerla en el Spinner
            CoroutineScope(Dispatchers.IO).launch {
                val comites = obtenerComites()

                withContext(Dispatchers.Main) {
                    // Configurar el spinner
                    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, comites.map { it.nombre })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerComite.adapter = adapter

                    // Establecer el comité actual
                    val comiteIndex = comites.indexOfFirst { it.idComite == item.idComite }
                    if (comiteIndex != -1) {
                        spinnerComite.setSelection(comiteIndex)
                    }

                    // Establecer el diálogo de edición
                    builder.setView(dialogView)

                    builder.setPositiveButton("Actualizar") { dialog, which ->
                        val nuevaContraseña = etContraseña.text.toString().takeIf { it.isNotEmpty() } // Solo encriptar si no está vacío
                        val nuevoCorreo = etCorreo.text.toString()
                        val nuevoIdComite = comites[spinnerComite.selectedItemPosition].idComite

                        if (nuevoCorreo.isNotEmpty()) {
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
    }
}
