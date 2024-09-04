package PTC.quickly

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import PTC.quickly.databinding.ActivityMainBinding
import android.view.Menu
<<<<<<< HEAD
import androidx.navigation.NavController
=======
import androidx.navigation.fragment.NavHostFragment
>>>>>>> master

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD

        // Cargar los datos del usuario desde SharedPreferences
        loadUserData()
=======
        supportActionBar?.hide()

        val rol = Login.variablesGlobalesLogin.rol
>>>>>>> master

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

<<<<<<< HEAD
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.pantalla_admin, R.id.calendario_b, R.id.navigation_home, R.id.navigation_notifications)
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
            else -> R.id.navigation_home
        }
        navController.navigate(initialDestination)
=======
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Definir el destino inicial y el menú basado en el rol
        val (startDestination, menuResId) = when (rol) {
            3 -> R.id.pantalla_admin to R.menu.bottom_nav_menu
            2 -> R.id.pantalla_coordinador to R.menu.coordinador_botton_navigation
            1 -> R.id.pantalla_alumno to R.menu.alumno_botton_navigation
            else -> R.id.pantalla_alumno to R.menu.alumno_botton_navigation // Destino por defecto
        }

        // Crear un nuevo grafo de navegación con el destino inicial
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph

        // Configurar el BottomNavigationView con el menú correspondiente
        navView.menu.clear()
        navView.inflateMenu(menuResId)
        navView.setupWithNavController(navController)

        // Configurar AppBarConfiguration si es necesario
        val appBarConfiguration = AppBarConfiguration(navView.menu.mapItemsToIds())
        // setupActionBarWithNavController(navController, appBarConfiguration)
>>>>>>> master
    }

    // Función de extensión para mapear los items del menú a sus IDs
    private fun Menu.mapItemsToIds() = (0 until size()).map { getItem(it).itemId }.toSet()
}