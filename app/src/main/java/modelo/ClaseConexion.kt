package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {

        try {

            /*Conexion Levi
            <<<<<<< HEAD
            val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
            =======*/
/* val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
>>>>>>> master
val usuariolevi = "SARAVIA_DEVELOPER"
val contrasenalevi = "JLSN2024"

val connection = DriverManager.getConnection(url, usuariolevi, contrasenalevi)
return connection*/

 //Conexion Gamboa
//<<<<<<< HEAD
  val url = "jdbc:oracle:thin@192.168.0.12:1521:xe"
  val usuarioGamboa = "PTCbase"
  val contrasenaGamboa = "."

  val connection = DriverManager.getConnection(url, usuarioGamboa, contrasenaGamboa)
  return connection

/*/Conexion Ruth
val url = "jdbc:oracle:thin@ 192.168.3.8:1521:xe"
   val usuarioRuth = "DBPRUEBA"
   val contrasenaRuth = "Ruth"

   val connection = DriverManager.getConnection(url, usuarioRuth, contrasenaRuth)
   return connection*/

//Conexion Samuel
/*val url = "jdbc:oracle:thin@192.168.1.19:1521:xe"
   val usuarioSamuel = "SAMUElSS_DEVELOPER"
   val contrasenaSamuel = "123456"

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


}
}