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
    /**

    * Esta función se llama cuando se crea la actividad RecoverPassword
    * @param savedInstanceState estado de la instancia guardada
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Oculta la action bar si está presente
        if (supportActionBar != null)
            supportActionBar!!.hide()

        // Infla el layout de la vista utilizando el binding y lo asigna a la variable view
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializa la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()
        auth= Firebase.auth

        // Configurar el botón de Enviar para que genere un AlertDialog Builder, recibir el email y mirar si el campo esta vacio o no.
        binding.btnEnviar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var gmail = binding.txtInputEmailRecoverTxt.text.toString()

            // Si el campo de correo electrónico no está vacío, llama a la función resetPassword para enviar el correo
            if(gmail.isNotEmpty()) {
                resetPassword(gmail)
            }else{
                // Si el campo está vacío, muestra un mensaje de error en un AlertDialog
                builder.setTitle("Error")
                builder.setMessage("El campo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
        // Configurar el botón de cancelar para navegar a la pantalla de Login.
        binding.btnCancelar.setOnClickListener{
            Log.d("GoLogin", "Ir a Login")
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    /**

    * Este método manda el correo de Reseteo de contraseña al correo proporcionado por el usuario.
    * @param correu: El correo del usuario que quiere resetear su contraseña.
     */
    fun resetPassword(correu: String){
        //Crea un AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        // Se establece el lenguaje del correo a español.
        auth.setLanguageCode("es")
        // Se envía el correo para restablecer la contraseña.
        auth.sendPasswordResetEmail(correu).addOnCompleteListener { task ->
            if(task.isSuccessful()){
                // Si se ha enviado el correo correctamente, se muestra un mensaje de éxito y se redirige al usuario a la pantalla de inicio de sesión.
                builder.setTitle("Perfecto!")
                builder.setMessage("Se ha enviado el Email con éxito.")
                builder.setPositiveButton("Aceptar"){ dialog, which ->
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()

            }else{
                // Si ha ocurrido un error al enviar el correo, se muestra un mensaje de error y se da la opción al usuario de volver a intentarlo.
                builder.setTitle("Error")
                builder.setMessage("No se ha podido enviar el Correo, porfavor introdúzcalo de nuevo.")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}