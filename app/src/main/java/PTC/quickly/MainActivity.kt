package PTC.quickly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import PTC.quickly.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatDelegate
import android.view.Menu
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el canal de notificación
        createNotificationChannel()

        // Cargar el tema de acuerdo a las preferencias
        loadTheme()

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

    // Crear el canal de notificación para Android 8.0 (Oreo) y versiones superiores
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "event_channel"
            val channelName = "Eventos"
            val channelDescription = "Notificaciones sobre eventos agregados"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
            }
            2 -> { // Coordinador
                menu.findItem(R.id.pantalla_coordinador)?.isVisible = true
                menu.findItem(R.id.calendario_b)?.isVisible = true
            }
            1 -> { // Alumno
                menu.findItem(R.id.pantalla_alumno)?.isVisible = true
                menu.findItem(R.id.calendario_b)?.isVisible = true
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

    // Cargar el tema guardado
    private fun loadTheme() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NightMode", false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
