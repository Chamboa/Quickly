package PTC.quickly

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout


class pantalla_alumno : Fragment() {
    private var ARG_PARAM1: String? = null
    private var ARG_PARAM2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pantalla_alumno, container, false)

        // Referencia al ConstraintLayout BtnChat después de inflar la vista
        val btnChat = view.findViewById<ConstraintLayout>(R.id.btnEventos)
        val btnHistorial = view.findViewById<ImageButton>(R.id.btnHistorial)

        // Configurar el OnClickListener
        btnChat?.setOnClickListener {
            // Crear un Intent para iniciar la actividad elegirChat
            val intent = Intent(activity, elegirChat::class.java)
            startActivity(intent)
        }
        btnHistorial?.setOnClickListener {
            // Crear un Intent para iniciar la actividad elegirChat
            val intent = Intent(activity, VerExpediente::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            pantalla_alumno().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
