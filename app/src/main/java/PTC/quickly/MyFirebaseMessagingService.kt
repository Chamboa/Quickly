package PTC.quickly

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Manejar los datos recibidos en el mensaje de notificación
        remoteMessage.notification?.let {
            mostrarNotificacion(it.title, it.body)
        }
    }

    private fun mostrarNotificacion(title: String?, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val channelId = "global_notifications"
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.imgquickly)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(UUID.randomUUID().hashCode(), builder.build())
        }
    }

    override fun onNewToken(token: String) {
        // Llamada para enviar el token al servidor
        enviarTokenAlServidor(token)
    }

    private fun enviarTokenAlServidor(token: String) {
        // Implementar la lógica para enviar el token FCM al backend
        // Ejemplo: Usar Retrofit o una solicitud HTTP para enviar el token
        val url = "https://tu-servidor.com/api/guardar_token"
        val requestBody = mapOf("token" to token)

        // Implementar el envío del token usando tu método HTTP favorito
        // Retrofit, Volley o HttpURLConnection
    }
}
