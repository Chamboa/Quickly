package PTC.quickly

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.util.UUID

class RegistroCuenta : AppCompatActivity() {

    val codigo_opcional_galeria = 102
    val STORAGE_REQUEST_CODE = 1

    lateinit var imageView: ImageView
    lateinit var miPath: String

    val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //1- mandar a llamar a todos los elementos de la vista
        imageView = findViewById(R.id.imageView)
        val txtNombreRegistro = findViewById<TextView>(R.id.txtNombreRegistro)
        val txtCorreoRegistro = findViewById<TextView>(R.id.txtCorreoRegistro)
        val txtContraseñaRegistro = findViewById<TextView>(R.id.txtContraseñaRegistro)
        val txtConfirmarContraseñaRegistro = findViewById<TextView>(R.id.txtConfirmarContraseñaRegistro)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val btnSubirFoto = findViewById<Button>(R.id.btnSubirFoto)

        btnSubirFoto.setOnClickListener{
            checkStoragePermission()
        }

        private fun checkStorePermission() {
            if (ContextCompat.CheckSelPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != packageManager.PERMISSION_GRANTED) {
                pedirPermisoAlmacenamiento()
            } else {
                val intent = intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, codigo_opcional_galeria)
            }
        }

        private fun pedirPermisoAlmacenamiento() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                STORAGE_REQUEST_CODE -> {
                    if ((grantResultsisNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, codigo_opcional_galeria)
                    } else {
                        Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else -> {

                }
            }
        }




        //2- programar el boton de crear
        //TODO: Boton para crear la cuenta//
        //Al darle clic al boton se hace un insert a la base con los valores que escribe el usuario
        btnCrearCuenta.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //Creo un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //Creo una variable que contenga un PrepareStatement
                val crearUsuario =
                    objConexion?.prepareStatement("INSERT INTO tbCrearCuenta(uuid, NombreRegistro, CorreoRegistro, ContraseñaRegistro, ConfirmarContraseñaRegistro) VALUES (?, ?, ?, ?, ?)")!!
                crearUsuario.setString(1, UUID.randomUUID().toString())
                crearUsuario.setString(2, txtNombreRegistro.text.toString())
                crearUsuario.setString(3, txtCorreoRegistro.text.toString())
                crearUsuario.setString(4, txtContraseñaRegistro.text.toString())
                crearUsuario.setString(5, txtConfirmarContraseñaRegistro.text.toString())
                crearUsuario.executeUpdate()
                withContext(Dispatchers.Main) {
                    //Abro otra corrutina o "Hilo" para mostrar un mensaje y limpiar los campos
                    //Lo hago en el Hilo Main por que el hilo IO no permite mostrar nada en pantalla
                    Toast.makeText(this@RegistroCuenta, "Usuario creado", Toast.LENGTH_SHORT).show()
                    txtNombreRegistro.setText("")
                    txtCorreoRegistro.setText("")
                    txtContraseñaRegistro.setText("")
                    txtConfirmarContraseñaRegistro.setText("")
                }
            }
        }
    }
}