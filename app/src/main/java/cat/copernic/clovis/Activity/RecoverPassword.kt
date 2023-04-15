package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityRecoverPasswordBinding

class RecoverPassword : AppCompatActivity() {
    private lateinit var binding: ActivityRecoverPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null)
            supportActionBar!!.hide()

        //INICIAR LAYOUT CON BINDING
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //CLICK ENVIAR RESETEAR CONTRASEÑA
        binding.btnEnviar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var gmail = binding.tltEmail.text.toString()

            if(gmail.isNotEmpty()) {
                startActivity(Intent(this, Login::class.java))
                finish()
            }else{
                builder.setTitle("Error")
                builder.setMessage("El campo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
        //CLICK CANCELAR RESETEAR CONTRASEÑA
        binding.btnCancelar.setOnClickListener{
            Log.d("GoLogin", "Ir a Login")
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}