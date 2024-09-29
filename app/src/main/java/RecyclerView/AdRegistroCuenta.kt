package com.example.ptc1.RecyclerViewRegistroCuenta

import PTC.quickly.ActualizarFotodePerfil
import PTC.quickly.R
import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
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

    // Función para calcular el hash SHA-256 de una contraseña
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    // Función para validar la contraseña
    private fun validarContraseña(contraseña: String): Boolean {
        return contraseña.length >= 8 && contraseña.any { it.isDigit() } && contraseña.any { it.isLetter() } && contraseña.any { !it.isLetterOrDigit() }
    }

    // Función para validar el correo electrónico con el dominio @ricaldone.edu.sv
    private fun validarCorreo(correo: String): Boolean {
        val dominioValido = "@ricaldone.edu.sv"
        return correo.endsWith(dominioValido) && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    fun actualizarPantallaRegistroCuenta(uuid: String, nuevaContraseña: String?, nuevoCorreo: String, nuevoIdComite: Int) {
        val index = Datos.indexOfFirst { it.uuid == uuid }
        val listaDatos = Datos.toMutableList()
        listaDatos[index] = listaDatos[index].copy(
            contraseña = nuevaContraseña ?: listaDatos[index].contraseña,
            correoElectronico = nuevoCorreo,
            idComite = nuevoIdComite
        )
        Datos = listaDatos
        notifyDataSetChanged()
    }

    fun eliminarRegistroCuenta(uuid: String, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val listaDatos = Datos.toMutableList()
            val objConexion = ClaseConexion().cadenaConexion()

            // Eliminar las relaciones en la base de datos
            val borrarAsistencia = objConexion?.prepareStatement("DELETE FROM Asistencia WHERE UUID_Evento IN (SELECT UUID_Evento FROM Eventos WHERE UUID_Usuario = ?)")
            borrarAsistencia?.setString(1, uuid)
            borrarAsistencia?.executeUpdate()

            val borrarEvento = objConexion?.prepareStatement("DELETE FROM Eventos WHERE UUID_Usuario = ?")
            borrarEvento?.setString(1, uuid)
            borrarEvento?.executeUpdate()

            val borrarReclamo = objConexion?.prepareStatement("DELETE FROM Reclamo WHERE UUID_Remitente = ? OR UUID_Destinatario = ?")
            borrarReclamo?.setString(1, uuid)
            borrarReclamo?.setString(2, uuid)
            borrarReclamo?.executeUpdate()

            val borrarExpediente = objConexion?.prepareStatement("DELETE FROM Expediente WHERE UUID_Usuario = ?")
            borrarExpediente?.setString(1, uuid)
            borrarExpediente?.executeUpdate()

            val borrarUsuario = objConexion?.prepareStatement("DELETE FROM Usuario WHERE UUID_Usuario = ?")
            borrarUsuario?.setString(1, uuid)
            borrarUsuario?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()

            // Actualizar la lista en la aplicación después de eliminar
            listaDatos.removeAt(position)

            withContext(Dispatchers.Main) {
                Datos = listaDatos
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }
    }

    fun editarRegistroUsuario(uuid: String, nuevaContraseña: String?, nuevoCorreo: String, nuevoIdComite: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().cadenaConexion()
            val hashedPassword = nuevaContraseña?.let { hashPassword(it) }

            val updateRegistroCuenta = objConexion?.prepareStatement(
                "UPDATE Usuario SET ${if (hashedPassword != null) "contraseña = ?, " else ""}correo_electronico = ?, id_comite = ? WHERE UUID_Usuario = ?"
            )

            var paramIndex = 1
            hashedPassword?.let {
                updateRegistroCuenta?.setString(paramIndex++, it)
            }
            updateRegistroCuenta?.setString(paramIndex++, nuevoCorreo)
            updateRegistroCuenta?.setInt(paramIndex++, nuevoIdComite)
            updateRegistroCuenta?.setString(paramIndex, uuid)
            updateRegistroCuenta?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()

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
            val btnActualizarIMG = dialogView.findViewById<ImageButton>(R.id.imgActualizarIMG)

            btnActualizarIMG.setOnClickListener {
                val intent = Intent(context, ActualizarFotodePerfil::class.java)
                intent.putExtra("uuid", item.uuid)
                context.startActivity(intent)
            }

            etContraseña.setText("")
            etCorreo.setText(item.correoElectronico)

            CoroutineScope(Dispatchers.IO).launch {
                val comites = obtenerComites()

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, comites.map { it.nombre })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerComite.adapter = adapter

                    val comiteIndex = comites.indexOfFirst { it.idComite == item.idComite }
                    if (comiteIndex != -1) {
                        spinnerComite.setSelection(comiteIndex)
                    }

                    builder.setView(dialogView)

                    builder.setPositiveButton("Actualizar") { dialog, which ->
                        val nuevaContraseña = etContraseña.text.toString().takeIf { it.isNotEmpty() }
                        val nuevoCorreo = etCorreo.text.toString()
                        val nuevoIdComite = comites[spinnerComite.selectedItemPosition].idComite

                        // Validaciones antes de actualizar
                        if (nuevoCorreo.isNotEmpty() && validarCorreo(nuevoCorreo)) {
                            if (nuevaContraseña == null || validarContraseña(nuevaContraseña)) {
                                editarRegistroUsuario(item.uuid, nuevaContraseña, nuevoCorreo, nuevoIdComite)
                            } else {
                                Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres, incluir letras, números y caracteres especiales", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Por favor, introduce un correo válido que termine en @ricaldone.edu.sv", Toast.LENGTH_SHORT).show()
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
