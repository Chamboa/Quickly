package PTC.quickly

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.security.MessageDigest

class Pantalla_Carga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_carga)
        supportActionBar?.hide()


        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 3000) // 3 segundos de retraso
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("userEmail", null)
        val savedPassword = sharedPref.getString("userPassword", null)

        if (savedEmail != null && savedPassword != null) {
            // Si hay credenciales guardadas, intentar iniciar sesión automáticamente
            loginUser(savedEmail, savedPassword)
        } else {
            // Si no hay credenciales guardadas, ir a la pantalla de login
            startActivity(Intent(this, Login::class.java))
            finish()
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
                    Login.userRoleId = resultado.getInt("id_rol")
                    Login.userUUID = resultado.getString("UUID_Usuario")
                    Login.userEmail = resultado.getString("correo_electronico")
                    Login.userName = resultado.getString("nombre")

                    launch(Dispatchers.Main) {
                        val intent = Intent(this@Pantalla_Carga, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // Si las credenciales guardadas ya no son válidas, ir a la pantalla de login
                    launch(Dispatchers.Main) {
                        startActivity(Intent(this@Pantalla_Carga, Login::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                // En caso de error, ir a la pantalla de login
                launch(Dispatchers.Main) {
                    startActivity(Intent(this@Pantalla_Carga, Login::class.java))
                    finish()
                }
            }
        }
    }

    private fun hashSHA256(contraencriptada: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contraencriptada.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
