package PTC.quickly

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import java.io.File

class Pantalla_perfil : Fragment() {

    private lateinit var ImgPerfil: ImageView // ImageView para la imagen de perfil
    val UUIDlogueado = Login.userUUID // UUID del usuario logueado

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Pantalla_perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Parámetros si se necesitan
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantalla_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtCorreo = view.findViewById<TextView>(R.id.id_Correo)
        val txtNombre = view.findViewById<TextView>(R.id.Id_Nombre)
        val Cerrar = view.findViewById<Button>(R.id.btncerrarsesion)
        ImgPerfil = view.findViewById(R.id.imageView14) // ImageView para la imagen de perfil

        // Cargar los datos del usuario
        txtNombre.text = Login.userName
        txtCorreo.text = Login.userEmail

        // Cargar la imagen de perfil guardada desde Firebase Storage
        cargarImagenDePerfil()

        // Evento de clic para cerrar sesión
        Cerrar.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarImagenDePerfil() {
        // Referencia al storage de Firebase
        val storageRef = Firebase.storage.reference.child("images/$UUIDlogueado.jpg")
        println("Referencia al storage: $storageRef")
        println("UUID del usuario logueado: $UUIDlogueado")

        // Intentar descargar la URL de la imagen
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            // Si se obtiene la URL, cargarla en el ImageView con Glide
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_perfil) // Imagen por defecto mientras se carga
                .error(R.drawable.ic_perfil) // Imagen en caso de error
                .into(ImgPerfil)
        }.addOnFailureListener { exception ->
            // Manejar el error cuando la imagen no exista
            if (exception is com.google.firebase.storage.StorageException && exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                // Imagen no encontrada, mostrar imagen predeterminada
                Toast.makeText(requireContext(), "Imagen de perfil no encontrada, cargando predeterminada", Toast.LENGTH_SHORT).show()
                ImgPerfil.setImageResource(R.drawable.ic_perfil)
            } else {
                // Otro tipo de error, mostrar mensaje de error
                Toast.makeText(requireContext(), "Error al cargar la imagen de perfil: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
