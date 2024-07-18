package PTC.quickly

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.example.ptc1.modelo.dcRoles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.regex.Pattern

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
        imageView = findViewById(R.id.imgPerfil)
        val txtNombreRegistro = findViewById<TextView>(R.id.txtNombreRegistro)
        val txtCorreoRegistro = findViewById<TextView>(R.id.txtCorreoRegistro)
        val txtContraseñaRegistro = findViewById<TextView>(R.id.txtContraseñaRegistro)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val btnSubirFoto = findViewById<Button>(R.id.btnSubirFoto)
        val spRoles = findViewById<Spinner>(R.id.spRoles)
        val btnAtrasAgregarU = findViewById<ImageView>(R.id.btnAtrasAgregarU)

        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //2- Programar el botón de crear
        btnCrearCuenta.setOnClickListener {
            if (validarCampos(txtNombreRegistro, txtCorreoRegistro, txtContraseñaRegistro, spRoles)) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        objConexion?.use { connection ->
                            connection.autoCommit = false
                            val contraseniaEncriptada = hashSHA256(txtContraseñaRegistro.text.toString())
                            val crearUsuario = connection.prepareStatement(
                                "INSERT INTO Usuario(UUID, nombre, correo_electronico, contraseña, id_rol) VALUES (?, ?, ?, ?, ?)"
                            )
                            crearUsuario.use {
                                it.setString(1, UUID.randomUUID().toString())
                                it.setString(2, txtNombreRegistro.text.toString())
                                it.setString(3, txtCorreoRegistro.text.toString())
                                it.setString(4, contraseniaEncriptada)
                                it.setInt(5, spRoles.selectedItemPosition + 1)  // Asumiendo que los IDs de rol comienzan en 1
                                val rowsAffected = it.executeUpdate()
                                if (rowsAffected > 0) {
                                    connection.commit()
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(this@RegistroCuenta, "Usuario creado", Toast.LENGTH_SHORT).show()
                                        txtNombreRegistro.setText("")
                                        txtCorreoRegistro.setText("")
                                        txtContraseñaRegistro.setText("")
                                    }
                                } else {
                                    connection.rollback()
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(this@RegistroCuenta, "Error al crear usuario", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroCuenta, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        btnSubirFoto.setOnClickListener {
            checkStoragePermission()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val listaRoles = obtenerRoles()
            val roles = listaRoles.map { it.tipo_rol }
            val adaptador = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, roles)
            spRoles.adapter = adaptador
        }

        btnAtrasAgregarU.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(txtNombreRegistro: TextView, txtCorreoRegistro: TextView, txtContraseñaRegistro: TextView, spRoles: Spinner): Boolean {
        var hayErrores = false

        if (txtNombreRegistro.text.isNullOrEmpty()) {
            txtNombreRegistro.error = "El nombre no puede estar vacío"
            hayErrores = true
        }

        if (txtCorreoRegistro.text.isNullOrEmpty() || !isValidEmail(txtCorreoRegistro.text.toString())) {
            txtCorreoRegistro.error = "Correo electrónico inválido"
            hayErrores = true
        }

        if (txtContraseñaRegistro.text.isNullOrEmpty() || !isValidPassword(txtContraseñaRegistro.text.toString())) {
            txtContraseñaRegistro.error = "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula y un número"
            hayErrores = true
        }


        return !hayErrores
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }

    private suspend fun obtenerRoles(): List<dcRoles> {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("select * from Rol")
            val lista = mutableListOf<dcRoles>()
            while (resultSet.next()) {
                val id_rol = resultSet.getInt("id_Rol")
                val nombreRol = resultSet.getString("tipo_rol")
                val valoresJuntos = dcRoles(id_rol, nombreRol)
                lista.add(valoresJuntos)
            }
            return@withContext lista
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermisoAlmacenamiento()
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcional_galeria)
        }
    }

    private fun pedirPermisoAlmacenamiento() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // Mostrar una explicación al usuario si es necesario
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, codigo_opcional_galeria)
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                //else por si hay un permiso que no tenemos controlado
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                codigo_opcional_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPath = url
                            imageView.setImageURI(it)
                        }
                    }
                }
            }
        }
    }

    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val inmageRef = storageRef.child("images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = inmageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this@RegistroCuenta, "Error al subir imagen", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { TaskSnapshot ->
            inmageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }
}
