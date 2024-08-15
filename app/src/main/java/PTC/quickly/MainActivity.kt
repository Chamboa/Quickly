package PTC.quickly

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import PTC.quickly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Verificamos el rol del usuario y configuramos la navegación según el rol
        when (Login.userRoleId) {
            3 -> { // Rol de Administrador
                val appBarConfiguration = AppBarConfiguration(
                    setOf(R.id.pantalla_admin, R.id.navigation_home, R.id.navigation_notifications)
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                navController.navigate(R.id.pantalla_admin)
            }
            2 -> { // Rol de Coordinador
                val appBarConfiguration = AppBarConfiguration(
                    setOf(R.id.pantalla_coordinador, R.id.navigation_home, R.id.navigation_notifications)
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                navController.navigate(R.id.pantalla_coordinador)
            }
            1 -> { // Rol de Alumno
                val appBarConfiguration = AppBarConfiguration(
                    setOf(R.id.pantalla_alumno, R.id.navigation_home, R.id.navigation_notifications)
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                navController.navigate(R.id.pantalla_alumno)
            }
            else -> {
                // Si el rol no es válido o no está definido
                navController.navigate(R.id.navigation_home)
            }
        }
    }
}
