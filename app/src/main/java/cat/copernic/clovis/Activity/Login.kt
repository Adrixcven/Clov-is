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
        // Ocultar la ActionBar si existe
        if (supportActionBar != null)
            supportActionBar!!.hide()
        // Inflar el layout y asignarlo a la vista
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Obtener la instancia de Firebase Auth
        auth= Firebase.auth

        // Configurar el botón de inicio de sesión
        binding.btnLogin.setOnClickListener {
            // Obtener el correo y la contraseña ingresados por el usuario
            var correo = binding.TextInputCajaNombre.text.toString().replace(" ", "")
            var contrasena = binding.TextInputCajaPassword.text.toString().replace(" ", "")

            // Crear un dialogo de alerta para mostrar errores
            val builder = AlertDialog.Builder(this)

            // Validar que el correo y la contraseña no estén vacíos
            if (correo.isNotBlank() && contrasena.isNotEmpty()) {
                // Si están llenos, llamar al método de inicio de sesión
                loguinar(correo, contrasena)
            } else if (correo.isBlank() && contrasena.isNotEmpty()) {
                // Si el correo está vacío, mostrar mensaje de error
                builder.setTitle("Error")
                builder.setMessage("El correo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            } else if (contrasena.isBlank() && correo.isNotEmpty()) {
                // Si la contraseña está vacía, mostrar mensaje de error
                builder.setTitle("Error")
                builder.setMessage("La contraseña no puede estar vacia")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            } else if (contrasena.isBlank() && correo.isBlank()) {
                // Si ambos campos están vacíos, mostrar mensaje de error
                builder.setTitle("Error")
                builder.setMessage("Los campos no pueden estar vacíos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }

        }
        // Configurar al hacer click en el texto "Unete"
        binding.txtUnete.setOnClickListener {
            // Abrir la actividad de registro y cerrar la actividad de login
            startActivity(Intent(this, Register::class.java))
            finish()
        }
        // Configurar al hacer click en el texto "Olvidaste tu contraseña"
        binding.txtForgotPassword.setOnClickListener {
            // Abrir la actividad de recuperación de contraseña
            Log.d("ForgetPassword", "Ir a Restablecer Contraseña")
            val intent = Intent(this, RecoverPassword::class.java)
            startActivity((intent))
        }
    }

    /**

    * Este método se llama cuando la actividad está a punto de hacerse visible para el usuario.
    * Aquí se comprueba si el usuario actual está autenticado. Si lo está, se llama al método "entrarconadmin()" para verificar si el usuario tiene permisos de administrador.
     */
    override fun onStart() {
        super.onStart()
        val user = Firebase.auth.currentUser
        if (user != null) {
            // Si el usuario ya ha iniciado sesión, entonces vemos si se puede loguear con administrador
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    entrarconadmin()
                }
            }
        }
    }
    /**

    Inicia sesión en la aplicación con el correo y la contraseña proporcionados.

    @param correo Correo electrónico del usuario que desea iniciar sesión.

    @param contrasena Contraseña del usuario que desea iniciar sesión.
     */
    fun loguinar(correo:String, contrasena:String){
        // Crear un objeto AlertDialog.Builder para mostrar mensajes de error en caso de que el inicio de sesión falle
        val builder = AlertDialog.Builder(this)

        // Intentar iniciar sesión con el correo y la contraseña proporcionados
        auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this){task ->
            // Si el inicio de sesión es exitoso, mira si puede entrar con admin
            if(task.isSuccessful){
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        entrarconadmin()
                    }
                }

            }else{// Si el inicio de sesión falla, mostrar un mensaje de error
                builder.setTitle("Error")
                builder.setMessage("Ha ocurrido un fallo en el inicio de sesión")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
    /**

    *Entra a la aplicación con el rol de administrador. Si el usuario autenticado tiene el rol de administrador,
    * se redirecciona a la MainActivity, de lo contrario, se redirecciona a la MainActivity_Users.
    * Este método se ejecuta en un hilo de fondo y suspende la ejecución hasta que se recibe la respuesta
    * de la base de datos Firebase.
     */
    private suspend fun entrarconadmin() {
        // Inicia un nuevo alcance de ciclo de vida y realiza la operación de acceso a Firebase en un hilo de fondo
        lifecycleScope.launch {
            auth = Firebase.auth
            // Obtiene el usuario actual autenticado
            var actual = auth.currentUser
            // Obtiene los datos del usuario autenticado desde la colección "Users" en Firebase
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
                // Si el usuario autenticado tiene el rol de administrador, redirecciona a MainActivity
                if(it.get("admin") == true ){
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{// Si el usuario autenticado no tiene el rol de administrador, redirecciona a MainActivity_Users
                    val intent = Intent(applicationContext, MainActivity_Users::class.java)
                    startActivity(intent)
                    finish()
                }
            }.await()// Espera hasta que se recibe la respuesta de la base de datos Firebase
        }
    }

}