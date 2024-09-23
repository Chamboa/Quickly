package com.example.ptc1.modelo

data class dcChat(
    var id_mensaje: Int,
    var mensaje: String,
    var fecha: String,
    var UUID_remitente: String? = null,
    var UUID_destinatario: String? = null
)
