package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class Correoconfirmacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_correoconfirmacion)
        enableEdgeToEdge()
        setContentView(R.layout.activity_correoconfirmacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgregresarrecuccontra = findViewById<ImageView>(R.id.imgregresarrecucontra)
        val etxtcodigoconfirmacion = findViewById<EditText>(R.id.etxtcodigoconfirmacion)
        val btnenviarcod = findViewById<Button>(R.id.btnenviarcod)

        btnenviarcod.setOnClickListener {
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> master

            val codigoIngresado = etxtcodigoconfirmacion.text.toString().toIntOrNull()

            if (codigoIngresado != null && codigoIngresado == Recuperar_contrasena.varialesglobales.Codigorecuperacion) {
                val pantallanuevacontrasena = Intent(this, Nueva_Contrasena::class.java)
                startActivity(pantallanuevacontrasena)
            }

            else {
                Toast.makeText(this, "Código incorrecto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
<<<<<<< HEAD
=======
=======
            val pantallanuevacontrasena = Intent (this, Nueva_Contrasena::class.java)
            startActivity(pantallanuevacontrasena)
>>>>>>> master
>>>>>>> master
        }

        imgregresarrecuccontra.setOnClickListener {
            val pantallarecuperarcontrasena = Intent (this, Recuperar_contrasena::class.java)
            startActivity(pantallarecuperarcontrasena)
        }
    }

}