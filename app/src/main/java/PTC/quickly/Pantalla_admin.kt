package PTC.quickly

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Pantalla_admin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Pantalla_admin : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
    }
}
