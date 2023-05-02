package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import cat.copernic.clovis.R
import java.util.*
import kotlin.concurrent.schedule

class SplashScreen : AppCompatActivity() {
    var timer = Timer()
    private lateinit var logo: ImageView
    private lateinit var anim: Animation
    /**

    * Este método sobrescribe el método onCreate() de la actividad SplashScreen.
    * Realiza la configuración de la interfaz de usuario y el manejo del temporizador para iniciar la actividad Login después de un segundo.
    * @param savedInstanceState El objeto Bundle que contiene el estado anterior de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Se establece que no se ejecutara el modo noche.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        // Se obtiene una referencia del logo y se carga la animación de fundido
        logo = findViewById(R.id.logo)
        anim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.animation = anim
        // Se oculta la barra de acción si existe.
        if (supportActionBar != null)
            supportActionBar!!.hide()
        // Se programa un temporizador para iniciar la actividad Login después de un segundo.
        timer.schedule(1000) {
            var intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}