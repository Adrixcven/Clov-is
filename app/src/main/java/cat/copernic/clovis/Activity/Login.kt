package cat.copernic.clovis.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityLoginBinding


class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null)
            supportActionBar!!.hide()
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //CLICK EN INICIAR SESIÓN
        binding.btnLogin.setOnClickListener {
            var correo = binding.TextInputCajaNombre.text.toString().replace(" ", "")
            var contrasena = binding.TextInputCajaPassword.text.toString().replace(" ", "")

            val builder = AlertDialog.Builder(this)

            if (correo.isNotBlank() && contrasena.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity((intent))
            } else if (correo.isBlank() && contrasena.isNotEmpty()) {
                builder.setTitle("Error")
                builder.setMessage("El correo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            } else if (contrasena.isBlank() && correo.isNotEmpty()) {
                builder.setTitle("Error")
                builder.setMessage("La contraseña no puede estar vacia")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            } else if (contrasena.isBlank() && correo.isBlank()) {
                builder.setTitle("Error")
                builder.setMessage("Los campos no pueden estar vacíos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
            //CLICK EN REGISTRATE
            binding.txtUnete.setOnClickListener {
                startActivity(Intent(this, Register::class.java))
                finish()
            }
            //CLICK EN OLVIDAR CONTRASEÑA
            binding.txtForgotPassword.setOnClickListener {
                Log.d("ForgetPassword", "Ir a Restablecer Contraseña")
                val intent = Intent(this, RecoverPassword::class.java)
                startActivity((intent))
            }
        }
    }
}