package PTC.quickly

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController

class pantalla_configuracion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pantalla_configuracion, container, false)

        val switch1 = view.findViewById<Switch>(R.id.switch1)
        val rol = Login.userRoleId

        // Cargar el estado del switch desde SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NightMode", false)
        switch1.isChecked = isNightMode

        // Cambiar el tema y navegar a la pantalla correspondiente
        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
            }

            // Navegar a la pantalla según el rol después de cambiar el tema
            navigateToRoleScreen(rol)
        }

        return view
    }

    // Guardar la preferencia en SharedPreferences
    private fun saveThemePreference(isNightMode: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("NightMode", isNightMode)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        // Asegurarse de que el tema correcto esté aplicado cuando se vuelve al fragmento
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NightMode", false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Navegar a la pantalla correspondiente según el rol del usuario
    private fun navigateToRoleScreen(roleId: Int?) {
        val navController = findNavController()
        when (roleId) {
            3 -> navController.navigate(R.id.pantalla_admin)      // Administrador
            2 -> navController.navigate(R.id.pantalla_coordinador) // Coordinador
            1 -> navController.navigate(R.id.pantalla_alumno)      // Alumno
            else -> navController.navigate(R.id.pantalla_perfil)   // Default: Perfil
        }
    }
}
