package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.clovis.R
import java.util.*
import kotlin.concurrent.schedule

class SplashScreen : AppCompatActivity() {
    var timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (supportActionBar != null)
            supportActionBar!!.hide()
        timer.schedule(1000) {
            var intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}