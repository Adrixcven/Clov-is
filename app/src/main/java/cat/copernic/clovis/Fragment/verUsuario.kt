package cat.copernic.clovis.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Activity.MainActivity_Users
import cat.copernic.clovis.R
import cat.copernic.clovis.Utils.Utilities
import cat.copernic.clovis.databinding.FragmentSeleccionarArmaBinding
import cat.copernic.clovis.databinding.FragmentVerUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
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
 * Use the [verUsuario.newInstance] factory method to
 * create an instance of this fragment.
 */
class verUsuario : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentVerUsuarioBinding
    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

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
        binding = FragmentVerUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Este método se llama después de que la vista asociada a este fragmento
     * haya sido creada. Aquí se deben realizar todas las operaciones necesarias
     * para inicializar la vista y asociarla con los datos del modelo.
     *
     * @param view La vista que se ha creado para este fragmento.
     * @param savedInstanceState Los datos guardados del estado de la instancia del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Llamada al método de la clase base.
        super.onViewCreated(view, savedInstanceState)
        // Actualizar el título de la barra de acción en función del fragmento actual.
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Usuario")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Usuario")
        }
        // Iniciar un Intent para cargar datos y actualizar la interfaz de usuario.
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerdatos()
                ponerimagenes()
            }
        }

        // Configurar el botón de editar usuario para navegar a la pantalla de edición de usuario.
        binding.btnEditar.setOnClickListener{
            val id = binding.cabUserName.text.toString()
            val directions = verUsuarioDirections.actionVerUsuarioToEditarUsuario(id)
            view.findNavController().navigate(directions)
        }
    }
    /**
     * Este método se utiliza para cargar los datos de usuario desde la base de datos y actualizar
     * la vista de usuario con estos datos.
     */
    private suspend fun ponerdatos() {
        // Iniciar una tarea asincrónica para cargar datos de usuario desde la base de datos.
        lifecycleScope.launch {
            // Obtener la instancia de autenticación de Firebase.
            auth = Firebase.auth
            // Obtener el usuario actualmente autenticado.
            var actual = auth.currentUser
            // Obtener los datos del usuario desde la base de datos.
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
                // Actualizar los campos de la vista de usuario con los datos del usuario obtenidos de la base de datos.
                binding.cabUserName.text = it.get("id").toString()
                binding.cabUserId.text = it.get("name").toString()
                binding.textoDeDescripcion.text = it.get("descripcion").toString()
                // Cargar la imagen de la clase del usuario desde Firebase Storage y actualizar la vista.
                lifecycleScope.launch {
                    if (it.get("clase").toString() == "Hechicero") {
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("image/Clases/Hechicero.png")
                        var localfile = File.createTempFile("tempImage", "jpeg")
                        val task = storageRef.getFile(localfile)
                        val result = task.await() // espera a que se complete la tarea
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        binding.imgClase.setImageBitmap(bitmap)
                        binding.textoDeClase.text = it.get("clase").toString()
                    } else if (it.get("clase").toString() == "Cazador") {
                        val storageRef =FirebaseStorage.getInstance().reference.child("image/Clases/Hechicero.png")
                        var localfile = File.createTempFile("tempImage", "jpeg")
                        val task = storageRef.getFile(localfile)
                        val result = task.await() // espera a que se complete la tarea
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        binding.imgClase.setImageBitmap(bitmap)
                        binding.textoDeClase.text = it.get("clase").toString()
                    } else if (it.get("clase").toString() == "Titan") {
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("image/Clases/Hechicero.png")
                        var localfile = File.createTempFile("tempImage", "jpeg")
                        val task = storageRef.getFile(localfile)
                        val result = task.await() // espera a que se complete la tarea
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        binding.imgClase.setImageBitmap(bitmap)
                        binding.textoDeClase.text = it.get("clase").toString()
                    } else{
                        binding.textoDeClase.text = "Sin clase"
                    }
                }
            }
        }
    }
    /**

    * Carga la imagen del usuario actual desde Firebase Storage y la muestra en la vista correspondiente.
    * Utiliza una tarea asíncrona para evitar bloquear el hilo principal.
     */
    private suspend fun ponerimagenes(){
        lifecycleScope.launch {
            // Obtiene la instancia de autenticación de Firebase
            auth = Firebase.auth
            // Obtiene el usuario actualmente autenticado
            var actual = auth.currentUser
            // Obtiene el email del usuario actual
            var email = actual!!.email.toString()
            // Crea una referencia al archivo de imagen del usuario en Firebase Storage
            val storageRef =FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")
            // Obtiene los metadatos del archivo de imagen
            storageRef.metadata.addOnSuccessListener {
                // Crea un archivo temporal para almacenar la imagen descargada
                var localfile = File.createTempFile("tempImage", "jpeg")
                // Descarga el archivo de imagen y lo guarda en el archivo temporal
                storageRef.getFile(localfile).addOnSuccessListener {
                    // Convierte el archivo temporal en un bitmap
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    // Muestra el bitmap en la vista correspondiente
                    binding.imgUsuario.setImageBitmap(bitmap)
                }
            }.await()// Espera a que se completen todas las tareas anteriores
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment verUsuario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            verUsuario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}