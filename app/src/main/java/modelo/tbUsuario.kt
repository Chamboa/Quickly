package com.example.ptc1.modelo

data class tbUsuario(
    val uuid: String,
    var nombre: String,
    var contraseña: String,
    var correoElectronico: String,
    var idComite: Int,
    val idGrado: Int,
    val idRol: Int
)