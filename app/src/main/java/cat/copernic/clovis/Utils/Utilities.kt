package cat.copernic.clovis.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.R
import java.util.regex.Pattern

@Keep
class Utilities {
    fun campEsBuit(correu: String, contrasenya: String): Boolean {
        return correu.isNotEmpty() && contrasenya.isNotEmpty()
    }

    var email_Param =
        Pattern.compile("^[_a-z0-9]+(.[_a-z0-9]+)*@[a-z0-9]+(.[a-z0-9]+)*(.[a-z]{2,4})\$")

    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email == null) false else email_Param.matcher(email).matches()
    }
    companion object{
        //Crea crea un canal para la notificacion con el channelID y el channelName de la clase donde lo llames
        fun createNotificationChannel(channelName: String, channelID: String, context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance : Int = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelID, channelName, importance).apply {
                    lightColor = Color.RED
                    enableLights(true)
                }
                val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }
        //Crea la notificacion con el channelID y la notificationID de la clase donde lo llames
        fun createNotification(context: Context, channelID: String, notificationID:Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, channelID).also {
                it.setContentTitle("Nuevo arma")
                it.setContentText("Se ha creado un nuevo arma")
                it.setSmallIcon(R.drawable.logo_clovis)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setContentIntent(pendingIntent)
                it.setAutoCancel(true)
            }.build()
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationID, notification)
        }
        //Crea la notificacion con el channelID y la notificationID de la clase donde lo llames
        fun createNotificationFav(context: Context, channelID: String, notificationID:Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, channelID).also {
                it.setContentTitle("Favorito")
                it.setContentText("Se ha añadido una nueva arma a favoritos")
                it.setSmallIcon(R.drawable.logo_clovis)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setContentIntent(pendingIntent)
                it.setAutoCancel(true)
            }.build()
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationID, notification)
        }
        //Crea la notificacion con el channelID y la notificationID de la clase donde lo llames
        fun createNotificationRegistro(context: Context, channelID: String, notificationID:Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, channelID).also {
                it.setContentTitle("Bienvenido")
                it.setContentText("Te has registrado a Clov-is.")
                it.setSmallIcon(R.drawable.logo_clovis)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setContentIntent(pendingIntent)
                it.setAutoCancel(true)
            }.build()
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationID, notification)
        }



    }

}
