package cat.copernic.clovis.Activity


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import cat.copernic.clovis.Models.Usuario
import cat.copernic.clovis.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


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
        binding.policy.setOnClickListener {
            openWebPage()
        }

        binding.btnCrearCuenta.setOnClickListener{
            var correo = binding.TextInputCajaEmailRegister.text.toString()
            var contrasena = binding.TextInputCajaPasswordRegister.text.toString()
            var repContrasena = binding.TextInputCajaEepPasswordRegister.text.toString()

            val builder = AlertDialog.Builder(this)

            if(contrasena.equals(repContrasena)&&campoVacio(correo,contrasena,repContrasena)&&contrasena.length >= 6&&binding.checkTermCond.isChecked()){
                registrar(correo,contrasena)
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
            }else if(contrasena.length < 6||repContrasena.length < 6){
                builder.setTitle("Error")
                builder.setMessage("La contraseña es demasiado pequeña. Tiene que ser de minimo 6 digitos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else{
                builder.setTitle("Error")
                builder.setMessage("Has aceptado las politicas de uso ")
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
        if (contrasena.length >= 6) {

            auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener {
                if (it.isSuccessful) {
                    var usuario = llegirDades()

                    val docRef = bd.collection("Users").document(usuario.email)
                    docRef.get().addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document != null && document.exists()) {
                                val context: Context = applicationContext
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("El archivo ya existe")
                                builder.setPositiveButton("Aceptar", null)
                                val dialog = builder.create()
                                dialog.show()
                            } else {
                                bd.collection("Users").document(usuario.email).set(
                                    hashMapOf(
                                        "id" to usuario.id,
                                        "email" to usuario.email,
                                        "name" to usuario.nombre,
                                        "descripcion" to usuario.descripcion,
                                        "clase" to usuario.clase,
                                        "admin" to usuario.admin
                                    )
                                )
                                    .addOnFailureListener { //No s'ha afegit el departament...
                                        val context: Context = applicationContext
                                        val builder = AlertDialog.Builder(context)
                                        builder.setMessage("No se ha podido crear el usuario")
                                        builder.setPositiveButton("Aceptar", null)
                                        val dialog = builder.create()
                                        dialog.show()
                                    }
                                //USERS     FAVORITOS
                                bd.collection("Users").document(usuario.email).collection("Favoritos")
                                    .document().set(
                                        HashMap<String, Any>()
                                    )
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener {
                                        val context: Context = applicationContext
                                        val builder = AlertDialog.Builder(context)
                                        builder.setMessage("No se ha podido crear la subclase de favoritos")
                                        builder.setPositiveButton("Aceptar", null)
                                        val dialog = builder.create()
                                        dialog.show()
                                    }
                                val db = FirebaseFirestore.getInstance()
                                val parentDocRef = db.collection("Users").document(usuario.email)
                                val subCollectionRef = parentDocRef.collection("Favoritos")
                                subCollectionRef.get().addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // Borrar cada documento en la subcolección
                                        subCollectionRef.document(document.id).delete()
                                    }
                                }

                                val drawable = ContextCompat.getDrawable(this, cat.copernic.clovis.R.drawable.foto_perfil)

                                val file = File.createTempFile("tempImage", "png", this.cacheDir)
                                file.outputStream().use { outputStream ->
                                    drawable?.toBitmap()?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                }

                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child("image/usuarios/${usuario.email}.jpeg")
                                storageRef.putFile(Uri.fromFile(file)).addOnSuccessListener {
                                }.addOnFailureListener { e ->
                                }


                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                        }else{
                            val context: Context = applicationContext
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Error al asegurar que no existe el usuario")
                            builder.setPositiveButton("Aceptar", null)
                            val dialog = builder.create()
                            dialog.show()
                        }

                    }

                }
            }
        }
    }
    fun llegirDades():Usuario{
        //Guardem les dades introduïdes per l'usuari
        var id = binding.TextInputCajaNombreRegister.text.toString()
        var Correo = binding.TextInputCajaEmailRegister.text.toString()

        //Afegim els treballadors introduïts per l'usuari a l'atribut treballadors

        return Usuario("", id, Correo, "", false, "", null)
    }
    fun openWebPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/567abb4e-18b3-42d3-9b4f-d5ea62d3c21d"))
        startActivity(intent)
    }


}