package com.example.ptc1.modelo

import oracle.sql.DATE

data class tbAsistencia(
    val id_Asistencia: Int,
    val Hora_de_entrada: DATE,
    val Hora_de_salida: DATE,
    val Asistencia_total: Int
)
