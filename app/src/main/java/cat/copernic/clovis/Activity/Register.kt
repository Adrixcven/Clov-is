package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(supportActionBar != null)
            supportActionBar!!.hide()

        //INICIAR LAYOUT CON BINDING
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth= Firebase.auth

        binding.btnCrearCuenta.setOnClickListener{
            var correo = binding.TextInputCajaEmailRegister.text.toString()
            var contrasena = binding.TextInputCajaPasswordRegister.text.toString()
            var repContrasena = binding.TextInputCajaEepPasswordRegister.text.toString()

            val builder = AlertDialog.Builder(this)
                registrar(correo,contrasena)
            if(contrasena.equals(repContrasena)&&campoVacio(correo,contrasena,repContrasena)){
                startActivity(Intent(this, Login::class.java))
                finish()
            }else if(correo.isBlank()&&contrasena.isNotEmpty()){
                builder.setTitle("Error")
                builder.setMessage("El correo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(!contrasena.equals(repContrasena)){
                builder.setTitle("Error")
                builder.setMessage("La contraseña no coincide")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(contrasena.isBlank()&&correo.isNotEmpty()){
                builder.setTitle("Error")
                builder.setMessage("La contraseña no puede estar vacía")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(correo.isBlank()&&contrasena.isBlank()){
                builder.setTitle("Error")
                builder.setMessage("Los campos no pueden estar vacíos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }


        //CLICK TIENES CUENTA EXISTENTE
        binding.txtTienesCuenta.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }

    }
    fun campoVacio(correo:String, contrasena:String, repContrasena:String):Boolean{
        return correo.isNotEmpty()&&contrasena.isNotEmpty()&&repContrasena.isNotEmpty()
    }
    fun registrar(correo: String, contrasena: String) {
        val builder = AlertDialog.Builder(this)
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    builder.setTitle("Error")
                    builder.setMessage("El registro ha fallado")
                    builder.setPositiveButton("Aceptar", null)
                    val dialog = builder.create()
                    dialog.show()
                }
            }
    }
}