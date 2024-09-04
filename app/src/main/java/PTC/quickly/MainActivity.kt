package PTC.quickly

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import PTC.quickly.databinding.ActivityMainBinding
import android.view.Menu
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar los datos del usuario desde SharedPreferences
        loadUserData()

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.pantalla_admin, R.id.calendario_b, R.id.pantalla_perfil, R.id.pantalla_configuracion)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Configurar la visibilidad de los ítems del menú según el rol
        Login.userRoleId?.let { configureNavigationForRole(it, navView.menu) }

        // Navegar a la pantalla inicial según el rol
        Login.userRoleId?.let { navigateToInitialScreen(it) }
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        Login.userUUID = sharedPref.getString("userUUID", "") ?: ""
        Login.userRoleId = sharedPref.getInt("userRoleId", -1)
        Login.userEmail = sharedPref.getString("userEmail", null)
        Login.userName = sharedPref.getString("userName", null)
    }

    private fun configureNavigationForRole(roleId: Int, menu: Menu) {
        when (roleId) {
            3 -> { // Administrador
                menu.findItem(R.id.pantalla_admin)?.isVisible = true
                menu.findItem(R.id.calendario_b)?.isVisible = true
                // Configurar otros ítems según sea necesario
            }
            2 -> { // Coordinador
                menu.findItem(R.id.pantalla_coordinador)?.isVisible = true
                menu.findItem(R.id.calendario_b)?.isVisible = true
                // Configurar otros ítems según sea necesario
            }
            1 -> { // Alumno
                menu.findItem(R.id.pantalla_alumno)?.isVisible = true
                menu.findItem(R.id.calendario_b)?.isVisible = true
                // Configurar otros ítems según sea necesario
            }
        }
    }

    private fun navigateToInitialScreen(roleId: Int) {
        val initialDestination = when (roleId) {
            3 -> R.id.pantalla_admin
            2 -> R.id.pantalla_coordinador
            1 -> R.id.pantalla_alumno
            else -> R.id.pantalla_perfil
        }
        navController.navigate(initialDestination)
    }
}
