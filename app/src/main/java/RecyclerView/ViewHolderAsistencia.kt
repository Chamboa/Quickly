package PTC.quickly

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class ViewHolderAsistencia(view: View) : RecyclerView.ViewHolder(view) {

    val txtNombreCard = view.findViewById<TextView>(R.id.txtAsistencia)


    val imgAgregar_Horas: ImageView = view.findViewById(R.id.imgAgregarHoras)

    val btncheckBoxsi: CheckBox = view.findViewById(R.id.btnCheck_si)

    val btncheckBoxno: CheckBox = view.findViewById(R.id.btnCheck_no)






}