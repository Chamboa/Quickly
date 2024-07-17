package PTC.quickly

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

class Pantalla_admin : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Puedes manejar los argumentos aquí si es necesario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantalla_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAgregarUsuario = view.findViewById<ImageButton>(R.id.btnAgregarUsuario)
        val btnAsistenciaHorasSociales = view.findViewById<ImageButton>(R.id.btnAsistenciaHoras)
        val btnGestionarUsuarios = view.findViewById<ImageButton>(R.id.btnGestionarUsuarios)
        val btnHistorialHoras = view.findViewById<ImageButton>(R.id.btnHistorialHoras)

        btnAgregarUsuario.setOnClickListener {
            val intent = Intent(requireContext(), RegistroCuenta::class.java)
            startActivity(intent)
        }


        btnAsistenciaHorasSociales.setOnClickListener {
            val intent = Intent(requireContext(), AsistenciaActivity::class.java)
            startActivity(intent)


        }

        btnGestionarUsuarios.setOnClickListener {

        }

        btnHistorialHoras.setOnClickListener {

        }
    }
}
