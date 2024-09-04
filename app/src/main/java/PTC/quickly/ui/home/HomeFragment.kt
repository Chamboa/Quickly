package PTC.quickly.ui.home

import PTC.quickly.Login
import PTC.quickly.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val btnCerrarSesion = root.findViewById<Button>(R.id.btncerrarsesion)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        return root
    }

    private fun cerrarSesion() {
        // Limpiar SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Limpiar variables de sesión en Login
        Login.userUUID = ""
        Login.userRoleId = null
        Login.userEmail = null
        Login.userName = null

        // Navegar a la pantalla de login
        val intent = Intent(requireActivity(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}