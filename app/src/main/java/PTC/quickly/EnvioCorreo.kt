package PTC.quickly

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun enviarCorreo(receptor: String, asunto: String, mensaje: String) = withContext(Dispatchers. IO) {

    val props = Properties() .apply {
        put ("mail. smtp.host", "smtp.gmail.com")
        put ("mail.smtp.socketFactory-port", "465")
        put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        put ("mail.smtp.auth", "true")
        put ("mail.smtp.port", "465")
    }

    val session = Session.getInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication ("quicklyptc2024@gmail.com", "t r a t w o i t p f q j n y z v")
        }
    })

    try{
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("quicklyptc2024@gmail.com"))
            addRecipient(Message.RecipientType.TO, InternetAddress(receptor))
            subject = asunto
            setText(mensaje)
        }
        Transport.send(message)
        println("Correo enviado")

    } catch (e: MessagingException){
        e.printStackTrace()
        println("No se pudo enviar el correo: ${e.message}")
    }

    }
