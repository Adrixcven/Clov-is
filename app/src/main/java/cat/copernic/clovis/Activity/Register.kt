package cat.copernic.clovis.Activity


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import cat.copernic.clovis.Models.Usuario
import cat.copernic.clovis.Utils.Utilities
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
    private var channelID = "ChannelID"
    private var channelName = "ChannelName"
    private val notificationID = 0

    /**

    * Método que sobreescribe el método onCreate de la actividad Register, se encarga de inicializar

    * los componentes de la interfaz y los elementos necesarios para el registro de un usuario.
    * @param savedInstanceState el estado anterior de la actividad
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Oculta la barra de acción si es existe.
        if(supportActionBar != null)
            supportActionBar!!.hide()

        //Infla el layout de la actividad usando ActivityRegisterBinding.
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Obtiene una instancia de FirebaseAuth.
        auth= Firebase.auth
        //Registra el listener del botón "Política de uso".
        binding.policy.setOnClickListener {
            openWebPage()
        }
        //Registra el listener del botón "Crear cuenta".
        binding.btnCrearCuenta.setOnClickListener{
            // Obtiene los valores de los campos de texto del formulario.
            var correo = binding.TextInputCajaEmailRegister.text.toString()
            var contrasena = binding.TextInputCajaPasswordRegister.text.toString()
            var repContrasena = binding.TextInputCajaEepPasswordRegister.text.toString()

            // Crea una instancia de AlertDialog.Builder para mostrar los mensajes de error.
            val builder = AlertDialog.Builder(this)

            // Verifica que los campos no estén vacíos, que la contraseña tenga un mínimo de 6 caracteres y que las contraseñas coincidan.
            if(contrasena.equals(repContrasena)&&campoVacio(correo,contrasena,repContrasena)&&contrasena.length >= 6&&binding.checkTermCond.isChecked()){
                // Si los campos son válidos, se llama a la función "registrar()" y se inicia la actividad "Login".
                registrar(correo,contrasena)
                Utilities.createNotificationChannel(channelName, channelID, this)
                Utilities.createNotificationRegistro(this, channelID, notificationID)
                startActivity(Intent(this, Login::class.java))
                finish()
            }else if(correo.isBlank()&&contrasena.isNotEmpty()){
                // Si el correo está vacío, muestra un mensaje de error.
                builder.setTitle("Error")
                builder.setMessage("El correo no puede estar vacío")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(!contrasena.equals(repContrasena)){
                // Si las contraseñas no coinciden, muestra un mensaje de error.
                builder.setTitle("Error")
                builder.setMessage("La contraseña no coincide")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(contrasena.isBlank()&&correo.isNotEmpty()){
                // Si la contraseña está vacía, muestra un mensaje de error.
                builder.setTitle("Error")
                builder.setMessage("La contraseña no puede estar vacía")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(correo.isBlank()&&contrasena.isBlank()){
                // Si ambos campos están vacíos, muestra un mensaje de error.
                builder.setTitle("Error")
                builder.setMessage("Los campos no pueden estar vacíos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else if(contrasena.length < 6||repContrasena.length < 6){
                // Si la contraseña es demasiado corta, muestra un mensaje de error.
                builder.setTitle("Error")
                builder.setMessage("La contraseña es demasiado pequeña. Tiene que ser de minimo 6 digitos")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }else{
                //Mostrar error si no se han aceptado las políticas de uso
                builder.setTitle("Error")
                builder.setMessage("Has aceptado las politicas de uso ")
                builder.setPositiveButton("Aceptar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }


        // Configurar el botón de tener cuenta para que navegue al Login
        binding.txtTienesCuenta.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }

    }
    /**

    * Verifica si los campos de correo, contraseña y confirmación de contraseña no están vacíos.
    * @param correo El correo ingresado por el usuario.
    * @param contrasena La contraseña ingresada por el usuario.
    * @param repContrasena La confirmación de contraseña ingresada por el usuario.
    * @return true si los campos no están vacíos, false de lo contrario.
     */
    fun campoVacio(correo:String, contrasena:String, repContrasena:String):Boolean{
        return correo.isNotEmpty()&&contrasena.isNotEmpty()&&repContrasena.isNotEmpty()
    }
    /**

    * Registra un usuario con su correo y contraseña.

    * @param correo El correo electrónico del usuario.

    * @param contrasena La contraseña del usuario.
     */
    fun registrar(correo: String, contrasena: String) {
        val builder = AlertDialog.Builder(this)
        //Verifica si la contraseña tiene al menos 6 caracteres
        if (contrasena.length >= 6) {
            //Crea un usuario en Firebase Auth con el correo y la contraseña proporcionados
            auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener {
                //Verifica si la creación del usuario fue exitosa
                if (it.isSuccessful) {
                    var usuario = llegirDades()

                    //Obtiene la referencia al documento del usuario en la colección "Users" de Firebase Firestore
                    val docRef = bd.collection("Users").document(usuario.email)
                    docRef.get().addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            //Verifica si ya existe un usuario con el mismo correo electrónico en la colección "Users" de Firebase Firestore
                            if (document != null && document.exists()) {
                                val context: Context = applicationContext
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("El archivo ya existe")
                                builder.setPositiveButton("Aceptar", null)
                                val dialog = builder.create()
                                dialog.show()
                            } else {
                                //Agrega el usuario a la colección "Users" de Firebase Firestore
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
                                    .addOnFailureListener { //No se crea el usuario
                                        val context: Context = applicationContext
                                        val builder = AlertDialog.Builder(context)
                                        builder.setMessage("No se ha podido crear el usuario")
                                        builder.setPositiveButton("Aceptar", null)
                                        val dialog = builder.create()
                                        dialog.show()
                                    }
                                //Crea la subcolección "Favoritos" del usuario
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
                                //Elimina todos los documentos de la subcolección "Favoritos" del usuario
                                val db = FirebaseFirestore.getInstance()
                                val parentDocRef = db.collection("Users").document(usuario.email)
                                val subCollectionRef = parentDocRef.collection("Favoritos")
                                subCollectionRef.get().addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // Borrar cada documento en la subcolección
                                        subCollectionRef.document(document.id).delete()
                                    }
                                }

                                // Guarda una imagen de perfil predeterminada para el usuario en Firebase Storage
                                val drawable = ContextCompat.getDrawable(this, cat.copernic.clovis.R.drawable.foto_perfil)
                                val file = File.createTempFile("tempImage", "png", this.cacheDir)
                                file.outputStream().use { outputStream ->
                                    drawable?.toBitmap()?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                }
                                // Obtiene la referencia al archivo de imagen del usuario en Firebase Storage
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child("image/usuarios/${usuario.email}.jpeg")
                                // Sube el archivo de imagen al almacenamiento de Firebase
                                storageRef.putFile(Uri.fromFile(file)).addOnSuccessListener {
                                }.addOnFailureListener { e ->
                                }

                                // Si se subió con éxito, se inicia la actividad de inicio de sesión y se cierra la actividad actual
                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                        }else{
                            // Si hubo un error al subir la imagen, se muestra un diálogo de alerta al usuario
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

    /**

    * Método encargado de leer los datos introducidos por el usuario en los campos de texto de la vista de registro.

    * @return Un objeto de tipo Usuario con los datos introducidos por el usuario.
     */
    fun llegirDades():Usuario{
        //Guardamos los datos introducidos por el usuario
        var id = binding.TextInputCajaNombreRegister.text.toString()
        var Correo = binding.TextInputCajaEmailRegister.text.toString()

        //Añadimos los datos introducidos por el usuario en el modelo de Usuario

        return Usuario("", id, Correo, "", false, "", null)
    }

    /**

    * Abre una página web en el navegador predeterminado del dispositivo.
     */
    fun openWebPage() {
        // Se crea un intent que indica que se desea abrir una página web  y se le pasa como argumento la URL de la página a abrir.
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/567abb4e-18b3-42d3-9b4f-d5ea62d3c21d"))
        // Se inicia la actividad correspondiente al intent.
        startActivity(intent)
    }


}