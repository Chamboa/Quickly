package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun hashSHA256(contraencriptada: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraencriptada.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        val txtcorreologin = findViewById<EditText>(R.id.txtcorreologin)
        val txtcontralogin = findViewById<EditText>(R.id.txtContralogin)
        val btniniciarsesion = findViewById<Button>(R.id.btniniciarsesion)
        val txtcuentaolvidada = findViewById<TextView>(R.id.txtcuentaolvidada)
        val imgvercontra = findViewById<ImageView>(R.id.idvercontra)


        btniniciarsesion.setOnClickListener {

            val Correo = txtcorreologin.text.toString()
            val Contrasena = txtcontralogin.text.toString()
            var hayErrores = false

            if (!Correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtcorreologin.error = "Ingresa lo datos que se te piden"
                hayErrores = true
            } else {
                txtcorreologin.error = null
            }

            if (Contrasena.length <= 7) {
                txtcontralogin.error = "Ingresa lo datos que se te piden."
                hayErrores = true
            } else {
                txtcontralogin.error = null
            }

            val activity_main = Intent(this, MainActivity::class.java)

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val contraencriptada = hashSHA256(txtcontralogin.text.toString())

                    val validarusuario =
                        objConexion?.prepareStatement("SELECT * FROM Usuario WHERE correo_electronico = ? AND contraseña = ?")
                    validarusuario?.setString(1, txtcorreologin.text.toString())
                    validarusuario?.setString(2, contraencriptada)

                    val resultado = validarusuario?.executeQuery()

                    if (resultado?.next() == true) {
                        startActivity(activity_main)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@Login,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Login, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        txtcuentaolvidada.setOnClickListener {
            val recuperarcontrasena = Intent(this, Recuperar_contrasena::class.java)
            startActivity(recuperarcontrasena)
        }

        imgvercontra.setOnClickListener {
            if (txtcontralogin.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                txtcontralogin.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtcontralogin.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

        }
    }
}