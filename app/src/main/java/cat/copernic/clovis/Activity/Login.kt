package cat.copernic.clovis.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var bd = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null)
            supportActionBar!!.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth= Firebase.auth

        //CLICK EN INICIAR SESIÓN
        binding.btnLogin.setOnClickListener {
            var correo = binding.TextInputCajaNombre.text.toString().replace(" ", "")
            var contrasena = binding.TextInputCajaPassword.text.toString().replace(" ", "")

            val builder = AlertDialog.Builder(this)

            if (correo.isNotBlank() && contrasena.isNotEmpty()) {
                loguinar(correo, contrasena)
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
    override fun onStart() {
        super.onStart()
        val user = Firebase.auth.currentUser
        if (user != null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    entrarconadmin()
                }
            }
        }
    }
    fun loguinar(correo:String, contrasena:String){
        val builder = AlertDialog.Builder(this)

        auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this){task ->
            if(task.isSuccessful){
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        entrarconadmin()
                    }
                }

            }else{
                builder.setTitle("Error")
                builder.setMessage("Ha ocurrido un fallo en el inicio de sesión")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
    private suspend fun entrarconadmin() {
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
                if(it.get("admin") == true ){
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(applicationContext, MainActivity_Users::class.java)
                    startActivity(intent)
                    finish()
                }
            }.await()
        }
    }

}