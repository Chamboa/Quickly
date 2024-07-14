package com.example.ptc1.RecyclerViewListAlumnos

import PTC.quickly.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ptc1.modelo.tbAsistencia

class AdaptadorAsistencia (var Datos: List<tbAsistencia>): RecyclerView.Adapter<ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAsistencia {
        //Uno mi recyclerView
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_asistencia, parent, false   )
        return ViewHolderAsistencia(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    //Devuelvo la cantidad de datos que se muestran
    override fun getItemCount() = Datos.size

    fun onBindViewHolder(holder: ViewHolderAsistencia, position: Int) {
        //Control a la Card
        val item = Datos[position]
        holder.txtNombreCard.text = item.Asistencia.toString()
        }
    }



