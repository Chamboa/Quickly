package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest
import java.sql.Connection
import java.sql.SQLException
import java.util.UUID

class PrimerUso : AppCompatActivity() {
    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var btnCrearUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_primer_uso)
        supportActionBar?.hide()


        setupWindowInsets()
        initializeViews()
        setupCreateUserButton()

        lifecycleScope.launch {
            checkAndCreateAdminUser()
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        txtNombre = findViewById(R.id.editTextNombre)
        txtCorreo = findViewById(R.id.editTextCorreo)
        txtContraseña = findViewById(R.id.editTextContraseña)
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario)
    }

    private fun setupCreateUserButton() {
        btnCrearUsuario.setOnClickListener {
            lifecycleScope.launch {
                try {
                    createAdminUser()
                } catch (e: Exception) {
                    Log.e("PrimerUsuario", "Error creating admin user", e)
                    showToast(getString(R.string.error_create_admin))
                }
            }
        }
    }

    private suspend fun checkAndCreateAdminUser() {
        withContext(Dispatchers.IO) {
            try {
                if (isUserTableEmpty()) {
                    // Table is empty, show UI to create admin
                    withContext(Dispatchers.Main) {
                        // Show UI elements for creating admin user
                        btnCrearUsuario.isEnabled = true
                    }
                } else {
                    // Users exist, navigate to PantallaCarga
                    withContext(Dispatchers.Main) {
                        navigateToPantallaCarga()
                    }
                }
            } catch (e: Exception) {
                Log.e("PrimerUsuario", "Error checking user table", e)
                withContext(Dispatchers.Main) {
                    showToast(getString(R.string.error_check_users))
                }
            }
        }
    }

    private suspend fun createAdminUser() {
        withContext(Dispatchers.IO) {
            ClaseConexion().cadenaConexion()?.use { connection ->
                val sqlInsert = """
                    INSERT INTO Usuario (UUID_Usuario, nombre, id_grado, id_rol, id_comite, contraseña, correo_electronico) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

                connection.prepareStatement(sqlInsert).use { statement ->
                    statement.setString(1, UUID.randomUUID().toString())
                    statement.setString(2, txtNombre.text.toString())
                    statement.setNull(3, java.sql.Types.INTEGER)
                    statement.setInt(4, 3) // Rol Administrador
                    statement.setNull(5, java.sql.Types.INTEGER)
                    statement.setString(6, hashPassword(txtContraseña.text.toString()))
                    statement.setString(7, txtCorreo.text.toString())

                    statement.executeUpdate()
                }
            }

            withContext(Dispatchers.Main) {
                showToast(getString(R.string.admin_created))
                navigateToPantallaCarga()
            }
        }
    }

    private fun isUserTableEmpty(): Boolean {
        var isEmpty = true
        ClaseConexion().cadenaConexion()?.use { connection ->
            val sqlCheck = "SELECT COUNT(*) FROM Usuario"
            connection.prepareStatement(sqlCheck).use { statement ->
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        isEmpty = resultSet.getInt(1) == 0
                    }
                }
            }
        }
        return isEmpty
    }

    private fun navigateToPantallaCarga() {
        val intent = Intent(this@PrimerUso, Pantalla_Carga::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PrimerUso, message, Toast.LENGTH_SHORT).show()
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}