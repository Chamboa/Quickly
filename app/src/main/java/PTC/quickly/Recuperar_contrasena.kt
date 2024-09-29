package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.sql.ResultSet

class Recuperar_contrasena : AppCompatActivity() {
    companion object varialesglobales {
        lateinit var correoingresado: String
        val Codigorecuperacion = (100000..999999).random()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_recuperar_contrasena)

        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtcorreocontraolvidada = findViewById<EditText>(R.id.txtcorreocontraolvidada)
        val btnsolicitarcorreo = findViewById<Button>(R.id.btnsolicitarcorreo)
        val imgregresar = findViewById<ImageView>(R.id.imgregresar)

        // Leví: Tuve que crear una función para que solo permita enviarle códigos
        // de recuperación de contraseña a correos que han sido registrados en la app
        fun verificarcorreoregistrado(email: String): Boolean {

            val objConexion = ClaseConexion().cadenaConexion()
            var estaregistrado = false

            if (objConexion != null) {
                val query = "SELECT * FROM Usuario WHERE correo_electronico = ?"
                val consultarcorreo = objConexion.prepareStatement(query)
                consultarcorreo.setString(1, email)


                val resultSet: ResultSet = consultarcorreo.executeQuery()
                if (resultSet.next()) {
                    val count = resultSet.getInt(1)
                    estaregistrado = count > 0
                }

                println("Correo ingresado: $email")

                resultSet.close()
                consultarcorreo.close()
                objConexion.close()
            }

            return estaregistrado
        }

        btnsolicitarcorreo.setOnClickListener {

            // Leví: Tuve que crear una función para que solo permita enviarle códigos
            // de recuperación de contraseña a correos que han sido registrados en la app

            fun verificarcorreoregistrado(email: String): Boolean {

                val objConexion = ClaseConexion().cadenaConexion()
                var estaregistrado = false

                if (objConexion != null) {
                    val query = "SELECT COUNT(*) FROM Usuario WHERE correo_electronico = ?"
                    val consultarcorreo = objConexion.prepareStatement(query)
                    consultarcorreo.setString(1, email)

                    val resultSet: ResultSet = consultarcorreo.executeQuery()
                    if (resultSet.next()) {
                        val count = resultSet.getInt(1)
                        estaregistrado = count > 0
                    }

                    resultSet.close()
                    consultarcorreo.close()
                    objConexion.close()
                }

                return estaregistrado
            }

            btnsolicitarcorreo.setOnClickListener {

                val correo = txtcorreocontraolvidada.text.toString()
                var hayerrores = false
                if (!correo.matches(Regex("[a-zA-Z0-9._-]+@ricaldone.edu.sv"))) {
                    txtcorreocontraolvidada.error =
                        "Ingresa el correo con el formato correspondiente"
                    /*if(!correo.matches(Regex("[a-zA-Z0-9._-]+@ricaldone.edu.sv"))){
                txtcorreocontraolvidada.error = "Ingresa el correo con el formato correspondiente"
            if(!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))){
                txtcorreocontraolvidada.error = "Ingresa lo datos que se te piden"
                hayerrores = true
            }
            else{
                txtcorreocontraolvidada.error = null
            }*/

                    if (hayerrores) {
                        return@setOnClickListener
                    }
                    if (hayerrores) {
                        return@setOnClickListener
                    }
                    // Leví: metí la acción de la corrutine a un if else para que verifique si
                    // cumple o no con la funcion de "verificarcorreoregistrado"

                    CoroutineScope(Dispatchers.Main).launch {
                        val correoRegistrado = verificarcorreoregistrado(correo)
                        if (correoRegistrado) {
                            correoingresado = correo

                            enviarCorreo(
                                "${correoingresado}",
                                "Recuperación de contraseña",
                                "¡Hola! aquí está tu código de recuperación $Codigorecuperacion"
                            )

                            val pantallaenviarcorreo =
                                Intent(this@Recuperar_contrasena, Correoconfirmacion::class.java)
                            startActivity(pantallaenviarcorreo)

                        } else {
                            // Mostrar mensaje de error si el correo no está registrado
                            Toast.makeText(
                                this@Recuperar_contrasena,
                                "Este correo no está registrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    imgregresar.setOnClickListener {
                        val pantallalogin = Intent(this, Login::class.java)
                        startActivity(pantallalogin)
                    }
                }
            }
        }
    }
}