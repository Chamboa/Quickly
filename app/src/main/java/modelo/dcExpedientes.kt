package com.example.ptc1.modelo

data class dcExpedientes(
    val UUID: String,
    val UUID_Alumno: String,
    val nombreEvento: String,
    val horasAgregadas: Int,
    val nombreUsuario: String,
    val grado: String,
    val tipoRol: String,
    val comite: String,
    val lugarEvento: String,
    val fechaEvento: String,
    val horaEvento: String
)