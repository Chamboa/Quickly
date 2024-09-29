package PTC.quickly

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ActualizarFotodePerfil : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_fotode_perfil)
        supportActionBar?.hide()

        imageView = findViewById(R.id.imageView24)
        val btnActualizarIMG = findViewById<Button>(R.id.btnSubirIMG)
        val btnGuardarIMG = findViewById<Button>(R.id.btnGuardarIMG)

        // Obtener UUID del Intent
        uuid = intent.getStringExtra("uuid") ?: UUID.randomUUID().toString()

        // Al hacer click en el botón de "Subir Imagen"
        btnActualizarIMG.setOnClickListener {
            openGallery()
        }

        // Al hacer click en el botón de "Guardar Imagen"
        btnGuardarIMG.setOnClickListener {
            uploadImageToFirebase()
        }
    }

    // Función para abrir la galería
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }

    // Registrar el resultado de la selección de la galería
    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            imageView.setImageURI(imageUri)
        }
    }

    // Función para subir la imagen a Firebase Storage
    private fun uploadImageToFirebase() {
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("images/$uuid.jpg")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }
}
