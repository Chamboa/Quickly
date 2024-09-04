package PTC.quickly

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
<<<<<<< HEAD
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeViewModelStoreOwner
=======
import android.widget.ImageButton
>>>>>>> master
import androidx.navigation.fragment.findNavController

class Pantalla_admin : Fragment() {
<<<<<<< HEAD
    private var param1: String? = null
    private var param2: String? = null
=======
>>>>>>> master

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
<<<<<<< HEAD
        val view = inflater.inflate(R.layout.fragment_pantalla_admin, container, false)

        val btnAgregarUsuario = view.findViewById<ConstraintLayout>(R.id.btnpntagregarusuario)
        val btnAsistenciaHorasSociales = view.findViewById<ConstraintLayout>(R.id.btnAsistencia)
        val btnGestionarUsuarios = view.findViewById<ConstraintLayout>(R.id.btnGestionarUsuario)
        val btnHistorialHoras = view.findViewById<ConstraintLayout>(R.id.btnHistorial)
        val btnChat = view.findViewById<Button>(R.id.chat)

        btnAgregarUsuario?.setOnClickListener {
            // Crear un Intent para iniciar la actividad RegistroCuenta
            val intent = Intent(activity, RegistroCuenta::class.java)
            startActivity(intent)
        }
        btnAsistenciaHorasSociales?.setOnClickListener {
            val intent = Intent(activity, MostrarEventos::class.java)
            startActivity(intent)
        }
        btnChat?.setOnClickListener {
            val intent = Intent(activity, elegirChat::class.java)
            startActivity(intent)
        }
        btnGestionarUsuarios?.setOnClickListener {
            val intent = Intent(activity, EditarCuenta::class.java)
            startActivity(intent)
        }
        btnHistorialHoras?.setOnClickListener {
            val intent = Intent(activity, VerExpedienteAlumnos::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Pantalla_admin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
=======
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
>>>>>>> master
    }
}
