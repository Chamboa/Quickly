package modelo

import java.sql.Connection
import java.sql.DriverManager

@Suppress("UNREACHABLE_CODE")
class ClaseConexion {

    fun cadenaConexion(): Connection? {

        try {

            //Conexion Levi
            /* val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
            val usuariolevi = "SARAVIA_DEVELOPER"
            val contrasenalevi = "JLSN2024"

            val connection = DriverManager.getConnection(url, usuariolevi, contrasenalevi)
            return connection*/

            //Conexion Gamboa
            val url = "jdbc:oracle:thin@192.168.0.12:1521:xe"
            val usuarioGamboa = "PTCbase"
            val contrasenaGamboa = "."

            val connection = DriverManager.getConnection(url, usuarioGamboa, contrasenaGamboa)
            return connection

            //Conexion Ruth
            /*val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
               val usuarioRuth = ""
               val contrasenaRuth = ""

               val connection = DriverManager.getConnection(url, usuarioRuth, contrasenaRuth)
               return connection*/

            //Conexion Samuel
            /*val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
               val usuarioSamuel = ""
               val contrasenaSamuel = ""

               val connection = DriverManager.getConnection(url, usuarioSamuel, contrasenaSamuel)
               return connection*/

            //Conexion Mateo
            /*val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
               val usuarioMateo = ""
               val contrasenaMateo = ""

               val connection = DriverManager.getConnection(url, usuarioMateo, contrasenaMateo)
               return connection*/
        } catch (e: Exception) {
            println("Ha ocurrido un error: $e")
            return null
        }

        fun getAllCommissionsAvailability(): Map<String, Boolean> {
            val connection = cadenaConexion() ?: return emptyMap()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT comite, cupos FROM Comite")
            val availabilityMap = mutableMapOf<String, Boolean>()

            while (resultSet.next()) {
                val comite = resultSet.getString("comite")
                val cupos = resultSet.getInt("cupos")
                availabilityMap[comite] = cupos > 0
            }

            resultSet.close()
            statement.close()
            connection.close()

            return availabilityMap
        }
    }
}



