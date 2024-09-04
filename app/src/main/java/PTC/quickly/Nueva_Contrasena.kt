package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion

class Nueva_Contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_nueva_contrasena)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etxtnewcontra = findViewById<EditText>(R.id.etxtnewcontra)
        val imgvernuevacontra = findViewById<ImageView>(R.id.imgvernuevacontra)
<<<<<<< HEAD
        val etxtcontranuevamente = findViewById<EditText>(R.id.etxtcontranuevamente)
        val imgvercontranuevamente = findViewById<ImageView>(R.id.imgvercontranuevamente)
=======
<<<<<<< HEAD
        val etxtcontranuevamente = findViewById<EditText>(R.id.etxtcontranuevamente)
        val imgvercontranuevamente = findViewById<ImageView>(R.id.imgvercontranuevamente)
=======
>>>>>>> master
>>>>>>> master
        val btnaceptarnuevacontra = findViewById<Button>(R.id.btnaceptarnuevacontra)

        btnaceptarnuevacontra.setOnClickListener {
            val pantallacontrasenarestablecida = Intent(this, Contrasena_Reestablecida::class.java)
            startActivity(pantallacontrasenarestablecida)
            val usuario = Recuperar_contrasena.correoingresado
            val nuevacontrasena = etxtnewcontra.text.toString()
<<<<<<< HEAD
            val contrasenanuevamente = etxtcontranuevamente.text.toString()
=======
<<<<<<< HEAD
            val contrasenanuevamente = etxtcontranuevamente.text.toString()
=======
>>>>>>> master
>>>>>>> master
            var hayErrores = false

            if (nuevacontrasena.length <= 7){
                etxtnewcontra.error = "La contraseña debe contener más de 7 dígitos"
                hayErrores = true
            }

            else {
                etxtnewcontra.error = null
            }

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> master
            if (contrasenanuevamente.length <= 7){
                etxtcontranuevamente.error = "La contraseña debe contener más de 7 dígitos"
                hayErrores = true
            }

            else {
                etxtnewcontra.error = null
            }

            if (!hayErrores) {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val actualizarContrasena = objConexion?.prepareStatement("UPDATE Usuario SET contraseña = ? WHERE correo_electronico = ?")!!
                    actualizarContrasena.setString(1, nuevacontrasena)
                    actualizarContrasena.setString(2, usuario)
                    actualizarContrasena.executeUpdate()
                }

                val pantallacontrasenarestablecida = Intent(this, Contrasena_Reestablecida::class.java)
                startActivity(pantallacontrasenarestablecida)
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden, verifíca si estan escritas de manera correcta", Toast.LENGTH_SHORT).show()
<<<<<<< HEAD
=======
            }

=======
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()
                val actualizarcontrasena = objConexion?.prepareStatement("UPDATE Usuario SET contraseña = ? WHERE correo_electronico = ?")!!
                actualizarcontrasena.setString(1, etxtnewcontra.text.toString())
                actualizarcontrasena.setString(2,usuario)
                actualizarcontrasena.executeUpdate()
>>>>>>> master
            }

>>>>>>> master
        }

        imgvernuevacontra.setOnClickListener {
            if (etxtnewcontra.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etxtnewcontra.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etxtnewcontra.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> master

        imgvercontranuevamente.setOnClickListener {
            if (etxtcontranuevamente.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etxtcontranuevamente.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etxtcontranuevamente.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
<<<<<<< HEAD
=======
=======
>>>>>>> master
>>>>>>> master
    }
}