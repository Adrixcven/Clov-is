package cat.copernic.clovis.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import cat.copernic.clovis.Models.Usuario
import cat.copernic.clovis.R

import cat.copernic.clovis.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var bd = FirebaseFirestore.getInstance()

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
        auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener {
                if (it.isSuccessful) {
                    var usuario = llegirDades()
                    bd.collection("Users").document(usuario.email).set(
                        //En lloc d'afegir un objecte, també podem passar els parells clau valor d'un document mitjançant un hashMpa. Si hem de passar tots els
                        // atributs d'un objecte passarem com a paràmetre l'objecte no un hashMap amb els seus atributs.
                        hashMapOf(
                            "id" to usuario.id,
                            "email" to usuario.email,
                            "name" to usuario.nombre,
                            "descripcion" to usuario.descripcion,
                            "clase" to usuario.clase,
                            "admin" to usuario.admin

                        ))
                        .addOnFailureListener{ //No s'ha afegit el departament...
                            val context: Context = applicationContext
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("No se ha podido crear el usuario")
                            builder.setPositiveButton("Aceptar", null)
                            val dialog = builder.create()
                            dialog.show()
                        }
                    //USERS     FAVORITOS RUTAS
                    bd.collection("Users").document(usuario.email).collection("Favoritos").document().set(
                        HashMap<String, Any>())
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener{
                            val context: Context = applicationContext
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("No se ha podido crear la subclase de favoritos")
                            builder.setPositiveButton("Aceptar", null)
                            val dialog = builder.create()
                            dialog.show()
                        }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }
    fun llegirDades():Usuario{
        //Guardem les dades introduïdes per l'usuari
        var id = binding.TextInputCajaNombreRegister.text.toString()
        var Correo = binding.TextInputCajaEmailRegister.text.toString()

        //Afegim els treballadors introduïts per l'usuari a l'atribut treballadors

        return Usuario(null, id, Correo, null, false, null, null)
    }


}