package cat.copernic.clovis.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Activity.MainActivity_Users
import cat.copernic.clovis.Models.Usuario
import cat.copernic.clovis.databinding.FragmentEditarUsuarioBinding
import cat.copernic.clovis.datalist.AdminList.Companion.admin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.StorageMetadata

import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editar_usuario.newInstance] factory method to
 * create an instance of this fragment.
 */
class editar_usuario : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentEditarUsuarioBinding
    val args: editar_usuarioArgs by navArgs()
    private lateinit var classSpinner: Spinner
    private lateinit var auth: FirebaseAuth
    private var bd = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditarUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
    * Función que se ejecuta al crear la vista del fragmento "Editar usuario".
    * Se encarga de actualizar el título de la action bar, obtener el id del usuario a editar,
    * inicializar variables y vistas, y configurar el spinner de selección de clase.
    * @param view Vista del fragmento.
    * @param savedInstanceState Estado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Actualizar el título de la action bar
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Editar Usuario")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Editar Usuario")
        }
        // Obtener el id del usuario a editar
        var nom = args.id
        // Inicializar variables y vistas
        var selectedOption = ""
        var admina = MutableLiveData<Boolean>(false)
        // Ejecutar las funciones para obtener los datos del usuario y la foto de perfil
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerusuario(admina)
                ponerfoto()
            }
        }
        // Configurar el spinner de selección de clase
        classSpinner = binding.spinnerClases
        ArrayAdapter.createFromResource(
            requireContext(),
            cat.copernic.clovis.R.array.class_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando aparece la lista de opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            classSpinner.adapter = adapter
        }

        binding.spinnerClases.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position)
                selectedOption = item.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Este método se llama cuando no se ha seleccionado ninguna opción
            }
        })

        // Configurar el botón de la foto de perfil para abrir la cámara
        binding.fotoperfil.setOnClickListener{
            intentfotoperfil.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        // Configurar el botón de guardar perfil para modificar los datos del usuario y navegar hacia la vista de usuario
        binding.guardarPerfil.setOnClickListener {
            modificarusuario(selectedOption, admina)

            view.findNavController().navigate(cat.copernic.clovis.R.id.action_editar_usuario_to_verUsuario)
        }
    }

    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference().child("image/usuarios").child(".jpeg")
    /**
    * Esta función registra el resultado de la actividad de tomar una foto para el perfil del usuario.
    * Si el resultado de la actividad es OK, se obtiene el usuario autenticado y su correo electrónico,
    * se crea una referencia al almacenamiento de Firebase para guardar la imagen y se comprime la imagen
    * en formato JPEG. Luego se sube la imagen al almacenamiento y se establece como imagen de perfil del usuario.
    * @param result el resultado de la actividad de tomar una foto.
     */
    private val intentfotoperfil = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            auth = Firebase.auth
            var actual = auth.currentUser
            val email = actual!!.email.toString()
            storageRef = storage.getReference().child("image/usuarios").child("$email.jpeg")
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {
            }.addOnSuccessListener { taskSnapshot ->
            }
            binding.fotoperfil.setImageBitmap(imageBitmap)
        }
    }
    /**
     * Función que se encarga de obtener los datos del usuario actual y ponerlos en la vista correspondiente.
     * También actualiza el valor de la variable admina con el valor de "admin" en la base de datos.
     *
     * @param admina MutableLiveData<Boolean> variable mutable que indica si el usuario actual es administrador o no.
     */
    private suspend fun ponerusuario(admina: MutableLiveData<Boolean>){
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
                // Pone los valores correspondientes en la vista
                binding.nom.setText(it.get("name").toString())
                binding.id.setText(it.get("id").toString())
                binding.correoElectronico.setText(it.get("email").toString())
                if (it.get("clase").toString() == "Hechicero") {
                    binding.spinnerClases.setSelection(3)
                } else if (it.get("clase").toString() == "Cazador") {
                    binding.spinnerClases.setSelection(1)
                } else if (it.get("clase").toString() == "Titan") {
                    binding.spinnerClases.setSelection(2)
                } else {
                    binding.spinnerClases.setSelection(0)
                }
                binding.informacion.setText(it.get("descripcion").toString())
                // Actualiza el valor de admina con el valor de "admin" en la base de datos
                admina.value = it.get("admin") as Boolean
            }.await()
        }
    }
    /**
     * Descarga la imagen de perfil del usuario actual desde Firebase Storage y la coloca en la ImageView correspondiente.
     * Utiliza la coroutine [lifecycleScope.launch] para realizar la tarea en un hilo separado.
     */
    private suspend fun ponerfoto(){
        lifecycleScope.launch {
            // Obtener el usuario actualmente autenticado
            auth = Firebase.auth
            var actual = auth.currentUser
            val email = actual!!.email.toString()
            // Crear una referencia al archivo de imagen en Firebase Storage
            val ref = FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")
            // Obtener los metadatos del archivo de imagen
            ref.getMetadata().addOnSuccessListener {
                // Si se obtienen los metadatos correctamente, descargar la imagen del Storage y convertirla a Bitmap
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")

                var localfile = File.createTempFile("tempImage", "jpeg")
                storageRef.getFile(localfile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    // Colocar el Bitmap en la ImageView correspondiente
                    binding.fotoperfil.setImageBitmap(bitmap)
                }
            }.await() // Esperar a que se completen las operaciones de descarga de imagen y obtención de metadatos
        }
    }

    /**
    * Función encargada de modificar los datos de un usuario en la base de datos.
    * @param selectedoOption String que indica el tipo de modificación a realizar.
    * @param admina MutableLiveData que indica si el usuario es administrador o no.
     */
    fun modificarusuario(selectedoOption:String, admina: MutableLiveData<Boolean>){
        auth = Firebase.auth
        var actual = auth.currentUser
        var user = llegirDades(selectedoOption, admina)
        bd.collection("Users").document(actual!!.email.toString()).set(
            hashMapOf( "name" to user.nombre,
                "id" to user.id,
                "email" to user.email,
                "clase" to user.clase,
                "descripcion" to user.descripcion,
                "admin" to user.admin
            )
        )
    }
    /**
     * Función encargada de leer los datos introducidos por el usuario en la vista y crear un objeto Usuario con ellos.
     * @param selectedoOption opción seleccionada por el usuario en la vista.
     * @param admina LiveData que indica si el usuario es administrador o no.
     * @return objeto Usuario con los datos introducidos por el usuario.
     */
    fun llegirDades(selectedoOption:String, admina: MutableLiveData<Boolean>): Usuario {
        //Guardamos los datos introducidos por el usuario
        var nom = binding.nom.text.toString()
        var id = binding.id.text.toString()
        var Correo = binding.correoElectronico.text.toString()
        var clase = selectedoOption
        var info = binding.informacion.text.toString()
        var admin = admina.value


        // Creamos un objeto Usuario con los datos introducidos por el usuario y lo devolvemos

        return Usuario(nom, id, Correo, info, admin!!, clase, null)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editar_usuario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editar_usuario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}