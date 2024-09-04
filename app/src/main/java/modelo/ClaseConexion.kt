package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {

        try {

<<<<<<< HEAD
            val ipLevi = "jdbc:oracle:thin@192.168.1.19:1521:xe"
            val ipGamboa = "jdbc:oracle:thin:@ 192.168.0.17:1521:xe"
            val ipRuth = "jdbc:oracle:thin@ 192.168.3.8:1521:xe"
            val ipSamuel = "jdbc:oracle:thin@192.168.1.19:1521:xe"
            val ipMateo = "jdbc:oracle:thin@192.168.1.19:1521:xe"


            val usuario = "Quickly"
            val contrasena = "."

=======

           val ipLevi = "jdbc:oracle:thin@192.168.1.19:1521:xe"
           val ipGamboa = "jdbc:oracle:thin:@ 192.168.0.12:1521:xe"
           val ipRuth = "jdbc:oracle:thin@ 192.168.3.8:1521:xe"

<<<<<<< HEAD
           val ipSamuel = "jdbc:oracle:thin@192.168.0.11:1521:xe"
           val ipMateo = "jdbc:oracle:thin@:1521:xe"



            val usuario = "Quickly"
            val contrasena = "QUICKLY"

            val connection = DriverManager.getConnection(ipSamuel, usuario, contrasena)
=======
           val ipSamuel = "jdbc:oracle:thin@192.168.0.5:1521:xe"
           val ipMateo = "jdbc:oracle:thin@192.168.1.24:1521:xe"



<<<<<<< HEAD
            val usuario = "Quickly"
            val contrasena = "QUICKLY"

            val connection = DriverManager.getConnection(ipLevi, usuario, contrasena)
=======
            val usuario = "MATEO_DEVELOPER"
            val contrasena = "bUtj72PG"

>>>>>>> master
            val connection = DriverManager.getConnection(ipGamboa, usuario, contrasena)
>>>>>>> master
>>>>>>> master
            return connection


<<<<<<< HEAD
=======


>>>>>>> master
        } catch (e: Exception) {
            println("Ha ocurrido un error: $e")
            return null

        }
    }
}
