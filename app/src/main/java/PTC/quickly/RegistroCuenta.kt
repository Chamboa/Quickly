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
import java.sql.Connection

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
            // Obtener los valores de los campos
            val nombre = txtNombreRegistro.text.toString()
            val correo = txtCorreoRegistro.text.toString()
            val contrasenia = txtContraseñaRegistro.text.toString()

            // Verificar si los campos están vacíos
            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (correo.isEmpty()) {
                Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (contrasenia.isEmpty()) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo
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

                        // Verificar si el correo ya existe en la base de datos
                        val correoExistente = verificarCorreoExistente(connection, correo)
                        if (correoExistente) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@RegistroCuenta,
                                    "El correo ya está registrado, intenta con otro",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }

                        // Verificar cupos del comité si el rol es de comité
                        if (rolidentificador == 2) {
                            val cuposDisponibles = verificarCuposComite(connection, sp.selectedItemPosition + 1)
                            if (cuposDisponibles <= 0) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@RegistroCuenta,
                                        "No hay cupos disponibles en el comité seleccionado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                return@launch
                            }
                        }

                        connection.autoCommit = false
                        val contraseniaEncriptada = hashSHA256(contrasenia)
                        val crearUsuario = connection.prepareStatement(
                            if (rolidentificador == 1) {
                                "INSERT INTO Usuario(UUID_Usuario, nombre, correo_electronico, contraseña, id_rol, id_grado) VALUES (?, ?, ?, ?, ?, ?)"
                            } else {
                                "INSERT INTO Usuario(UUID_Usuario, nombre, correo_electronico, contraseña, id_rol, id_comite) VALUES (?, ?, ?, ?, ?, ?)"
                            }
                        )

                        crearUsuario.use {
                            it.setString(1, uuid)
                            println(uuid)
                            it.setString(2, nombre)
                            it.setString(3, correo)
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

                                    // Limpiar los campos
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

            // Filtrar los roles para excluir los que tienen id_rol = 3
            val rolesFiltrados = listaRoles.filter { it.id_Rol == 1 || it.id_Rol == 2 }
            val roles = rolesFiltrados.map { it.tipo_rol }

            val adaptador = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, roles)
            spRoles.adapter = adaptador

            spRoles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    // Identificar el id_rol seleccionado basado en los roles filtrados
                    val rolSeleccionado = rolesFiltrados[position]

                    if (rolSeleccionado.id_Rol == 1) {
                        // Mostrar los grados para el rol de id_rol = 1
                        CoroutineScope(Dispatchers.Main).launch {
                            val listaGrado = obtenerGrado()
                            val grados = listaGrado.map { it.grado }
                            val adaptadorGrados = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, grados)
                            sp.adapter = adaptadorGrados
                            sp.visibility = View.VISIBLE
                            rolidentificador = 1
                        }
                    } else if (rolSeleccionado.id_Rol == 2) {
                        // Mostrar los comités para el rol de id_rol = 2
                        CoroutineScope(Dispatchers.Main).launch {
                            val listaComites = obtenerComites()
                            val comites = listaComites.map { it.comite }
                            val adaptadorComites = ArrayAdapter(this@RegistroCuenta, android.R.layout.simple_spinner_dropdown_item, comites)
                            sp.adapter = adaptadorComites
                            sp.visibility = View.VISIBLE
                            rolidentificador = 2
                        }
                    } else {
                        sp.visibility = View.GONE
                        rolidentificador = 0
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada
                }
            }
        }

        btnAtrasAgregarU.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun verificarCorreoExistente(connection: Connection, correo: String): Boolean {
        return withContext(Dispatchers.IO) {
            val consulta = connection.prepareStatement("SELECT COUNT(*) FROM Usuario WHERE correo_electronico = ?")
            consulta.use {
                it.setString(1, correo)
                val resultSet = it.executeQuery()
                if (resultSet.next()) {
                    return@withContext resultSet.getInt(1) > 0
                }
            }
            return@withContext false
        }
    }

    private suspend fun verificarCuposComite(connection: Connection, idComite: Int): Int {
        return withContext(Dispatchers.IO) {
            val consultaCupos = connection.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = ?")
            consultaCupos.use {
                it.setInt(1, idComite)
                val resultSet = it.executeQuery()
                if (resultSet.next()) {
                    return@withContext resultSet.getInt("cupos")
                }
            }
            return@withContext 0
        }
    }

    private suspend fun obtenerRoles(): List<dcRoles> {
        return withContext(Dispatchers.IO) {
            val roles = mutableListOf<dcRoles>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT id_rol, tipo_rol FROM Rol")

                while (resultSet.next()) {
                    val idRol = resultSet.getInt("id_rol")
                    val tipoRol = resultSet.getString("tipo_rol")
                    val rol = dcRoles(idRol, tipoRol)
                    roles.add(rol)
                }
            }
            return@withContext roles
        }
    }

    private suspend fun obtenerGrado(): List<dcGrado> {
        return withContext(Dispatchers.IO) {
            val grados = mutableListOf<dcGrado>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT id_grado, grado FROM Grado")

                while (resultSet.next()) {
                    val idGrado = resultSet.getInt("id_grado")
                    val grado = resultSet.getString("grado")
                    val gradoObj = dcGrado(idGrado, grado)
                    grados.add(gradoObj)
                }
            }
            return@withContext grados
        }
    }

    private suspend fun obtenerComites(): List<dcComites> {
        return withContext(Dispatchers.IO) {
            val comites = mutableListOf<dcComites>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT id_comite, comite FROM Comite")

                while (resultSet.next()) {
                    val idComite = resultSet.getInt("id_comite")
                    val comite = resultSet.getString("comite")
                    val comiteObj = dcComites(idComite, comite)
                    comites.add(comiteObj)
                }
            }
            return@withContext comites
        }
    }

    // Validar que el correo termine con @ricaldone.edu.sv
    private fun validarCorreo(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches() && correo.endsWith("@ricaldone.edu.sv")
    }

    // Verificar si el permiso de almacenamiento externo está concedido
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    // Abrir la galería de imágenes
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, codigoOpcionalGaleria)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == codigoOpcionalGaleria) {
            val selectedImageUri: Uri? = data?.data
            imageView.setImageURI(selectedImageUri)

            if (selectedImageUri != null) {
                val uuid = uuidUsuarioCreado
                val imageRef = Firebase.storage.reference.child("images/$uuid.jpg")
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {
                    Toast.makeText(this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
