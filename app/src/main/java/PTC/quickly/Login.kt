package PTC.quickly

import android.content.Context
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class Login : AppCompatActivity() {

    companion object {
        var userUUID = ""
        var userRoleId: Int? = null
        var userEmail: String? = null
        var userName: String? = null
        var id_comite: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Verificar si ya hay una sesión guardada
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("userEmail", null)
        val savedPassword = sharedPref.getString("userPassword", null)

        if (savedEmail != null && savedPassword != null) {
            // Si hay credenciales guardadas, intentar iniciar sesión automáticamente
            loginUser(savedEmail, savedPassword)
        }

        val txtcorreologin = findViewById<EditText>(R.id.txtcorreologin)
        val txtcontralogin = findViewById<EditText>(R.id.txtContralogin)
        val btniniciarsesion = findViewById<Button>(R.id.btniniciarsesion)
        val txtcuentaolvidada = findViewById<TextView>(R.id.txtcuentaolvidada)
        val imgvercontra = findViewById<ImageView>(R.id.idvercontra)

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

        checkForAdminUser()

        btniniciarsesion.setOnClickListener {

            btniniciarsesion.isEnabled = false

            val Correo = txtcorreologin.text.toString()
            val Contrasena = txtcontralogin.text.toString()
            var hayErrores = false

            // Aquí cambiamos la validación para que el correo termine en @ricaldone.edu.sv
            if (!Correo.endsWith("@ricaldone.edu.sv")) {
                txtcorreologin.error = "Tu correo debe terminar en @ricaldone.edu.sv"
                hayErrores = true
            } else {
                txtcorreologin.error = null
            }

            if (Contrasena.length <= 7) {
                txtcontralogin.error = "La contraseña debe tener más de 7 caracteres"
                hayErrores = true
            } else {
                txtcontralogin.error = null
            }

            if (!hayErrores) {
                loginUser(Correo, Contrasena)
            } else {
                btniniciarsesion.isEnabled = true
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loginUser(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val contraencriptada = hashSHA256(password)

                val validarusuario = objConexion?.prepareStatement(
                    "SELECT id_rol, UUID_Usuario, nombre, correo_electronico, id_comite FROM Usuario WHERE correo_electronico = ? AND contraseña = ?"
                )!!
                validarusuario.setString(1, email)
                validarusuario.setString(2, contraencriptada)

                val resultado = validarusuario.executeQuery()

                if (resultado.next()) {
                    userRoleId = resultado.getInt("id_rol")
                    userUUID = resultado.getString("UUID_Usuario")
                    userEmail = resultado.getString("correo_electronico")
                    userName = resultado.getString("nombre")

                    val idComite = resultado.getString("id_comite")
                    id_comite = idComite.toInt()
                    println("ID del comité: $id_comite")

                    // Guardar las credenciales y los datos del usuario en SharedPreferences
                    val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("userEmail", email)
                        putString("userPassword", password)
                        putString("userUUID", userUUID)
                        putString("userName", userName)
                        putInt("userRoleId", userRoleId ?: -1)
                        apply()
                    }

                    withContext(Dispatchers.Main) {
                        when (userRoleId) {
                            1 -> {
                                if (idComite.isNullOrEmpty()) {
                                    // Si id_rol es 1 y no tiene id_comite, redirigir a ElegirComite
                                    val intent = Intent(this@Login, Unirse_Comite::class.java)
                                    startActivity(intent)
                                } else {
                                    // Si id_rol es 1 y tiene id_comite, redirigir a MainActivity
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            2, 3 -> {
                                // Redirigir directamente a MainActivity para id_rol 2 o 3
                                val intent = Intent(this@Login, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Login,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_LONG
                        ).show()
                        findViewById<Button>(R.id.btniniciarsesion).isEnabled = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    findViewById<Button>(R.id.btniniciarsesion).isEnabled = true
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkForAdminUser() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val checkAdminQuery = objConexion?.prepareStatement(
                    "SELECT COUNT(*) as count FROM Usuario WHERE id_rol = 3"
                )
                val resultado = checkAdminQuery?.executeQuery()

                if (resultado?.next() == true) {
                    val count = resultado.getInt("count")
                    if (count == 0) {
                        withContext(Dispatchers.Main) {
                            // No hay usuarios con id_rol = 3, redirigir a RegistroCuenta
                            val intent = Intent(this@Login, PrimerUso::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Error al verificar usuarios: ${e.message}", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun hashSHA256(contraencriptada: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contraencriptada.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
