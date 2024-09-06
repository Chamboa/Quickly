package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerViewRegistroCuenta.AdRegistroCuenta
import com.example.ptc1.modelo.tbUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class EditarCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_cuenta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Elementos de la vista
        val btnRegresarRegistroCuenta = findViewById<ImageView>(R.id.btnRegresarRegistroCuenta)
        val rcvRegistroCuenta = findViewById<RecyclerView>(R.id.rcvRegistroCuenta)

        rcvRegistroCuenta.layoutManager = LinearLayoutManager(this)

        // Función para obtener el registro de cuentas con validaciones
        fun obtenerRegistroCuenta(): List<tbUsuario> {
            val listaRegistroUsuario = mutableListOf<tbUsuario>()
            var objConexion: Connection? = null
            var statement: Statement? = null
            var resultSet: ResultSet? = null

            try {
                objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    statement = objConexion.createStatement()
                    resultSet = statement.executeQuery("SELECT * FROM Usuario")

                    while (resultSet?.next() == true) {
                        val uuid = resultSet.getString("UUID_Usuario")
                        val nombre = resultSet.getString("nombre")
                        val correo_electronico = resultSet.getString("correo_electronico")
                        val contrasena = resultSet.getString("contraseña")

                        // Validar que los campos no estén vacíos
                        if (!uuid.isNullOrEmpty() && !nombre.isNullOrEmpty() && !correo_electronico.isNullOrEmpty() && !contrasena.isNullOrEmpty()) {
                            val usuario = tbUsuario(uuid, nombre, contrasena, correo_electronico, 0, 0, 0)
                            listaRegistroUsuario.add(usuario)
                        }
                    }
                } else {
                    // Mostrar un mensaje si no hay conexión a la base de datos
                    runOnUiThread {
                        Toast.makeText(this@EditarCuenta, "Error: No se pudo conectar a la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@EditarCuenta, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                resultSet?.close()
                statement?.close()
                objConexion?.close()
            }

            return listaRegistroUsuario
        }

        // Corutina para cargar los datos de usuarios
        CoroutineScope(Dispatchers.IO).launch {
            val usuarioDB = obtenerRegistroCuenta()
            withContext(Dispatchers.Main) {
                if (usuarioDB.isNotEmpty()) {
                    val adapter = AdRegistroCuenta(usuarioDB)
                    rcvRegistroCuenta.adapter = adapter
                } else {
                    // Si no se encontraron usuarios, mostrar un mensaje
                    Toast.makeText(this@EditarCuenta, "No se encontraron registros de usuarios", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Programar el botón de regresar
        btnRegresarRegistroCuenta.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
