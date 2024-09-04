package PTC.quickly

import PTC.quickly.R
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ViewHolderAsistencia(view: View) : RecyclerView.ViewHolder(view) {

    val txtNombreCard = view.findViewById<TextView>(R.id.txtAsistencia)

    val imgBuscar: ImageView =view.findViewById(R.id.imgBuscar)

    val imgAgregar_Horas: ImageView = view.findViewById(R.id.imgAgregarHoras)

    val checkBox: CheckBox = view.findViewById(R.id.checkBox)





}