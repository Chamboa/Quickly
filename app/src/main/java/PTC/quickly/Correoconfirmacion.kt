package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ptc1.PTC.quickly.enviarCorreo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Correoconfirmacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        imgregresarrecuccontra.setOnClickListener {
            val pantallarecuperarcontrasena = Intent (this, Recuperar_contrasena::class.java)
            startActivity(pantallarecuperarcontrasena)
    }
}

}