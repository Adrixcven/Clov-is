package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityRecoverPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverPassword : AppCompatActivity() {
    private lateinit var binding: ActivityRecoverPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null)
            supportActionBar!!.hide()

        //INICIAR LAYOUT CON BINDING
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        auth= Firebase.auth

        //CLICK ENVIAR RESETEAR CONTRASEÑA
        binding.btnEnviar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var gmail = binding.txtInputEmailRecoverTxt.text.toString()

            if(gmail.isNotEmpty()) {
                resetPassword(gmail)
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
    fun resetPassword(correu: String){
        val builder = AlertDialog.Builder(this)

        auth.setLanguageCode("es")
        auth.sendPasswordResetEmail(correu).addOnCompleteListener { task ->

            if(task.isSuccessful()){
                builder.setTitle("Perfecto!")
                builder.setMessage("Se ha enviado el Email con éxito.")
                builder.setPositiveButton("Aceptar"){ dialog, which ->
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()

            }else{
                builder.setTitle("Error")
                builder.setMessage("No se ha podido enviar el Correo, porfavor introdúzcalo de nuevo.")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}