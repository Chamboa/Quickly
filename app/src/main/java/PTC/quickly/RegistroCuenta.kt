package PTC.quickly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ptc1.modelo.dcGrado
import com.example.ptc1.modelo.dcRoles
import com.example.ptc1.modelo.dcComites
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.UUID
import com.example.ptc1.RecyclerViewRegistroCuenta.AdRegistroCuenta as adRegistroCuenta

class RegistroCuenta : AppCompatActivity() {
    private val codigoOpcionalGaleria = 102
    private val READ_EXTERNAL_STORAGE_REQUEST = 1 // Solicitud de permiso de almacenamiento externo

    lateinit var imageView: ImageView
    private lateinit var miPath: String

    private var uuidUsuarioCreado: String? = null

    var rolidentificador = 0
    private val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cuenta)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imgPerfil)
        val txtNombreRegistro = findViewById<TextView>(R.id.txtNombreRegistro)
        val txtCorreoRegistro = findViewById<TextView>(R.id.txtCorreoRegistro)
        val txtContraseñaRegistro = findViewById<TextView>(R.id.txtContraseñaRegistro)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val btnSubirFoto = findViewById<Button>(R.id.btnSubirFoto)
        val spRoles = findViewById<Spinner>(R.id.spRoles)
        val btnAtrasAgregarU = findViewById<ImageView>(R.id.btnAtrasAgregarU)
        val sp = findViewById<Spinner>(R.id.spGrado)

        // Pedir permiso al inicio si no se ha dado
        checkStoragePermission()

        // Desactivar el botón de subir foto hasta que se cree el usuario
        btnSubirFoto.isEnabled = false

        // Función para encriptar contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnCrearCuenta.setOnClickListener {
            val uuid = UUID.randomUUID().toString() // Generar UUID para el nuevo usuario
            uuidUsuarioCreado = uuid // Guardar el UUID globalmente

            // Validar el correo antes de proceder
            val correo = txtCorreoRegistro.text.toString()
            if (!validarCorreo(correo)) {
                Toast.makeText(
                    this@RegistroCuenta,
                    "El correo debe terminar con @ricaldone.edu.sv",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()
                    objConexion?.use { connection ->
                        connection.autoCommit = false
                        val contraseniaEncriptada = hashSHA256(txtContraseñaRegistro.text.toString())
                        val crearUsuario = connection.prepareStatement(
                            if (rolidentificador == 1) {
                                "INSERT INTO Usuario(UUID_Usuario, nombre, correo_electronico, contraseña, id_rol, id_grado) VALUES (?, ?, ?, ?, ?, ?)"
                            } else {
                                "INSERT INTO Usuario(UUID_Usuario, nombre, correo_electronico, contraseña, id_rol, id_comite) VALUES (?, ?, ?, ?, ?, ?)"
                            }
                        )
                        crearUsuario.use {
                            it.setString(1, uuid)
                            it.setString(2, txtNombreRegistro.text.toString())
                            it.setString(3, txtCorreoRegistro.text.toString())
                            it.setString(4, contraseniaEncriptada)
                            it.setInt(5, spRoles.selectedItemPosition + 1)
                            it.setInt(6, sp.selectedItemPosition + 1)
                            val rowsAffected = it.executeUpdate()
                            if (rowsAffected > 0) {
                                connection.commit()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@RegistroCuenta,
                                        "Usuario creado exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Habilitar el botón para subir la foto
                                    btnSubirFoto.isEnabled = true

                                    txtNombreRegistro.setText("")
                                    txtCorreoRegistro.setText("")
                                    txtContraseñaRegistro.setText("")
                                }
                            } else {
                                connection.rollback()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@RegistroCuenta,
                                        "Error al crear usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistroCuenta, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        println("Error: ${e.message}")
                    }
                }
            }
        }

        btnSubirFoto.setOnClickListener {
            // Verifica si el UUID del usuario ya fue creado
            if (uuidUsuarioCreado != null) {
                // Llamar a abrirGaleria directamente
                abrirGaleria()
            } else {
                Toast.makeText(this, "Primero cree un usuario", Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val listaRoles = obtenerRoles()
            val roles = listaRoles.map { it.tipo_rol }
            val adaptador = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, roles)
            spRoles.adapter = adaptador

            spRoles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (position + 1 == 1) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val listaGrado = obtenerGrado()
                            val grados = listaGrado.map { it.grado }
                            val adaptadorGrados = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, grados)
                            sp.adapter = adaptadorGrados
                            sp.visibility = View.VISIBLE

                            rolidentificador = 1
                        }
                    } else if (position + 1 == 2) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val listaComites = obtenerComites()
                            val comites = listaComites.map { it.comite }
                            val adaptadorComites = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, comites)
                            sp.adapter = adaptadorComites

                            rolidentificador = 2
                        }
                    } else {
                        sp.visibility = View.GONE
                        rolidentificador = 0
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No action needed
                }
            }
        }

        btnAtrasAgregarU.setOnClickListener {
            val intent = Intent(this@RegistroCuenta, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun obtenerRoles(): List<dcRoles> {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("SELECT * FROM Rol")
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

    private suspend fun obtenerGrado(): List<dcGrado> {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("SELECT * FROM Grado")
            val lista = mutableListOf<dcGrado>()
            while (resultSet.next()) {
                val id_grado = resultSet.getInt("id_grado")
                val nombreGrado = resultSet.getString("grado")
                val valoresJuntos = dcGrado(id_grado, nombreGrado)
                lista.add(valoresJuntos)
            }
            return@withContext lista
        }
    }

    private suspend fun obtenerComites(): List<dcComites> {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("SELECT * FROM Comite")
            val lista = mutableListOf<dcComites>()
            while (resultSet.next()) {
                val id_comite = resultSet.getInt("id_comite")
                val nombreComite = resultSet.getString("comite")
                val valoresJuntos = dcComites(id_comite, nombreComite)
                lista.add(valoresJuntos)
            }
            return@withContext lista
        }
    }

    // Función para validar el formato del correo
    fun validarCorreo(correo: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return correo.endsWith("@ricaldone.edu.sv") && emailPattern.matcher(correo).matches()
    }

    // Función para abrir la galería de imágenes
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, codigoOpcionalGaleria)
    }

    // Función para pedir permiso de almacenamiento si no se tiene
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codigoOpcionalGaleria && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageView.setImageURI(imageUri)

            // Cargar la imagen a Firebase Storage utilizando el UUID del usuario
            if (uuidUsuarioCreado != null && imageUri != null) {
                subirImagenAFirebase(imageUri)
            } else {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subirImagenAFirebase(imageUri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagenPerfilRef = storageRef.child("images/$uuidUsuarioCreado.jpg")

        // Convertir la imagen URI a Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagenPerfilRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen: ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}
