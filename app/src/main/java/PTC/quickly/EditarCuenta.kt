package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerViewRegistroCuenta.AdaptadorRegistroCuenta
import com.example.ptc1.modelo.tbUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

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

        //mando a llamar a todos los elementos de la vista
        val btnRegresarRegistroCuenta = findViewById<ImageView>(R.id.btnRegresarRegistroCuenta)

        val rcvRegistroCuenta = findViewById<RecyclerView>(R.id.rcvRegistroCuenta)

        rcvRegistroCuenta.layoutManager = LinearLayoutManager(this)

        fun obetenerRegistroCuenta(): List<tbUsuario>{

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbUsuario")!!

            val listaRegistroUsuario = mutableListOf<tbUsuario>()

            while (resultSet.next()){
                val uuid = resultSet.getString("UUID")
                val nombre = resultSet.getString("nombre")
                val correo_electronico = resultSet.getString("correo_electronico")
                val contraña = resultSet.getString("contraseña")

                val valoresJuntosRegistroCuenta = tbUsuario(uuid, nombre, correo_electronico, contraña)

                listaRegistroUsuario.add(valoresJuntosRegistroCuenta)
            }
            return listaRegistroUsuario
        }

        CoroutineScope(Dispatchers.IO).launch {
            val usuarioDB = obetenerRegistroCuenta()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorRegistroCuenta(usuarioDB)
                rcvRegistroCuenta.adapter = adapter
            }
        }

        //programo el boton de regresar
        btnRegresarRegistroCuenta.setOnClickListener {
            val intent = Intent(this, RegistroCuenta::class.java)
            startActivity(intent)

        }
    }
}