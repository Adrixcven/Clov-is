package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import cat.copernic.clovis.R
import java.util.*
import kotlin.concurrent.schedule

class SplashScreen : AppCompatActivity() {
    var timer = Timer()
    private lateinit var logo: ImageView
    private lateinit var anim: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        logo = findViewById(R.id.logo)
        anim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.animation = anim
        if (supportActionBar != null)
            supportActionBar!!.hide()
        timer.schedule(1000) {
            var intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}