package com.example.ptc1.modelo

data class tbEventos(
    val uuid: Int,
    val idUsuario: Int,
    val lugar: String,
    val descripcion: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val idAsistencia: Int
)
