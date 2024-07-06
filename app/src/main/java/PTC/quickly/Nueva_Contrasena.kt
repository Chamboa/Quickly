package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Nueva_Contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etxtnewcontra = findViewById<EditText>(R.id.etxtnewcontra)
        val imgvernuevacontra = findViewById<ImageView>(R.id.imgvernuevacontra)
        val etxtnewcontradnv = findViewById<EditText>(R.id.etxtnewcontradnv)
        val imgvernuevacontradnv = findViewById<ImageView>(R.id.imgvernuevacontradnv)
        val btnaceptarnuevacontra = findViewById<Button>(R.id.btnaceptarnuevacontra)

        imgvernuevacontra.setOnClickListener {
            if (etxtnewcontra.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etxtnewcontra.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etxtnewcontra.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        imgvernuevacontradnv.setOnClickListener {
            if (etxtnewcontradnv.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etxtnewcontradnv.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etxtnewcontradnv.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        btnaceptarnuevacontra.setOnClickListener {
            val pantallacontrasenarestablecida = Intent(this, Contrasena_Reestablecida::class.java)
            startActivity(pantallacontrasenarestablecida)
        }
    }
}