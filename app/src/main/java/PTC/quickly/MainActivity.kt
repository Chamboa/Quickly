package PTC.quickly

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import PTC.quickly.databinding.ActivityMainBinding
import android.view.Menu
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val rol = Login.variablesGlobalesLogin.rol

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

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
    }

    // Función de extensión para mapear los items del menú a sus IDs
    private fun Menu.mapItemsToIds() = (0 until size()).map { getItem(it).itemId }.toSet()
}