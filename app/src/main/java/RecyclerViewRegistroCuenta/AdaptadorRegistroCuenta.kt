package com.example.ptc1.RecyclerViewRegistroCuenta

import PTC.quickly.R
import PTC.quickly.ViewHolderAsistencia
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.tbUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class AdaptadorRegistroCuenta(var Datos: List<tbUsuario>): RecyclerView.Adapter<ViewHolderRegistroCuenta>() {


    fun actualizarPantallaRegistroCuenta(uuid: String, nuevoNombre: String){
        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombre = nuevoNombre
        notifyDataSetChanged()
    }


    fun eliminarRegistroCuenta(uuid: String, position: Int) {
        val listaDatos = Datos .toMutableList()
        listaDatos.removeAt(position)
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().cadenaConexion()
            val borrarRegistroCuenta = objConexion?.prepareStatement("delete from tbUsuario where uuid = ?")!!
            borrarRegistroCuenta.setString(1, uuid)
            borrarRegistroCuenta.executeUpdate()
            val commit = objConexion.prepareStatement( "commit")!!
            commit.executeUpdate()
        }
        Datos=listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun editarRegistroUsuario(nombreRegistroCuenta: String, uuid: String){
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val updateRegistroCuenta = objConexion?.prepareStatement("update tbUsuario set nombre = ? where uuid = ?")!!
            updateRegistroCuenta.setString(1, nombreRegistroCuenta)
            updateRegistroCuenta.setString(2, uuid)
            updateRegistroCuenta.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()

            withContext(Dispatchers.Main){
                actualizarPantallaRegistroCuenta(uuid, nombreRegistroCuenta)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRegistroCuenta {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_registro_cuenta, parent, false)
        return ViewHolderRegistroCuenta(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderRegistroCuenta, position: Int) {
        val item = Datos[position]
        holder.txtNombreCard.text = item.nombre

        holder.btnBorrarRegistroCuenta.setOnClickListener {
            val contexto = holder.txtNombreCard.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estas seguro que deseas eliminar este usuario?")

            //botonoes de mi alerta
            builder.setPositiveButton("si"){
                dialog, wich ->
                eliminarRegistroCuenta(item.uuid, position)
            }

            builder.setNegativeButton("no"){
                dialog, wich ->
                dialog.dismiss()
            }
            builder.show()
        }
        holder.btnEditarRegistroCuenta.setOnClickListener {
            //codigo para editar
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Desea actualizar el usuario?")

            val cuadroTexto = EditText(context)

            cuadroTexto.setHint(item.nombre)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar") {dialog, wich ->
                editarRegistroUsuario(cuadroTexto.text.toString(), item.uuid)
            }

            builder.setNegativeButton("Cancelar"){dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

    }


}