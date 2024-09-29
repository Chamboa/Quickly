package PTC.quickly

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout

class pantalla_coordinador : Fragment() {

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
        return inflater.inflate(R.layout.fragment_pantalla_coordinador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHrs = view.findViewById<ConstraintLayout>(R.id.btnAsistenciac)
        val btnEvnts = view.findViewById<ConstraintLayout>(R.id.btnHistorialc)
        val chat = view.findViewById<ConstraintLayout>(R.id.btnChatc)

        chat.setOnClickListener {
            val intent = Intent(activity, elegirChat::class.java)
            startActivity(intent)
        }

        btnEvnts.setOnClickListener {
            val intent = Intent(activity, VerExpedienteAlumnos::class.java)
            startActivity(intent)
        }

        btnHrs.setOnClickListener {
            val intent = Intent(requireContext(), MostrarEventos::class.java)
            startActivity(intent)
        }

    }
}
