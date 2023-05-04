package cat.copernic.clovis.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.FragmentInfoObjectsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
 * Use the [Info_objects.newInstance] factory method to
 * create an instance of this fragment.
 */
class Info_objects : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentInfoObjectsBinding
    val args: Info_objectsArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private var bd = FirebaseFirestore.getInstance()
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
    * Crea y devuelve la vista que representa el diseño del fragmento
    * @param inflater Objeto utilizado para inflar cualquier vista en el fragmento.
    * @param container El contenedor padre en el cual se infla la vista.
    * @param savedInstanceState Bundle que contiene los datos de estado previamente guardados del fragmento.
    * @return la vista que representa el diseño del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento usando el objeto LayoutInflater proporcionado y el contenedor padre especificado, pero no se adjunta a la raíz del contenedor todavía.
        binding = FragmentInfoObjectsBinding.inflate(inflater, container, false)
        // Devuelve la raíz de la vista inflada para que se pueda mostrar en la pantalla.
        return binding.root
    }

    /**
    * Esta función es llamada cuando se crea la vista del fragmento "Info Arma"
    * @param view La vista raíz del fragmento
    * @param savedInstanceState El estado previamente guardado de la instancia del fragmento
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Actualizar el título de la ActionBar en la actividad principal
        (activity as MainActivity?)!!.updateActionBarTitle("Info Arma")
        // Obtener el id del arma seleccionada de los argumentos
        id = args.id
        // Obtener la instancia de autenticación de Firebase
        auth = Firebase.auth
        var actual = auth.currentUser
        // Obtener la instancia de Firestore de Firebase
        val db = FirebaseFirestore.getInstance()
        // Obtener la referencia de la subcolección "Favoritos" del usuario actual
        val subcollectionRef = db.collection("Users").document(actual!!.email.toString()).collection("Favoritos")
        // Comprobar si el arma actual está en la subcolección "Favoritos" del usuario actual
        subcollectionRef.document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        binding.imgStar.setImageResource(R.drawable.estrellaon)
                    }
                }
            }
        // Lanzar una nueva corutina para obtener los datos y las imágenes del arma actual
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerdatos(id)
                ponerimagenes(id)
            }
        }
        // Configurar el clic en la estrella para añadir o eliminar el arma actual de la subcolección "Favoritos"
        binding.imgStar.setOnClickListener {
            subcollectionRef.document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        favoritosdelete(id)
                    } else {
                        favoritosadd(id)
                    }
                }
            }
        }
    }
    /**
    * Método que se encarga de poner los datos de un objeto de la colección "Armas" en la interfaz de usuario.
    * @param id el identificador del documento en la colección "Armas" que contiene los datos del objeto.
     */
    private suspend fun ponerdatos(id: String) {
        lifecycleScope.launch {
            // Obtener el documento con el identificador id de la colección "Armas"
            bd.collection("Armas").document(id).get().addOnSuccessListener {
                // Asignar los valores de los campos del documento a los componentes de la interfaz de usuario
                binding.txtNameObj.setText(it.get("Nombre").toString())
                binding.txtDescriptionObject.setText(it.get("Descripcion").toString())
                binding.txtNumCargador.setText(it.get("Cargador").toString())
                binding.txtNumDisparos.setText(it.get("Disparos por minuto").toString())
                binding.txtImpactoNum.setText(it.get("Impacto").toString())
                binding.txtRangoNum.setText(it.get("Rango").toString())
                binding.txtStabilityNum.setText(it.get("Estabilidad").toString())
                binding.txtReloadNum.setText(it.get("Recarga").toString())
                binding.txtInventarioNum.setText(it.get("Tamano del inventario").toString())
                binding.txtAimNum.setText(it.get("Asistencia de apuntado").toString())
                binding.txtZoomNum.setText(it.get("Zoom").toString())
                binding.txtAirassistNum.setText(it.get("Asistencia en aire").toString())
                binding.txtRecoilNum.setText(it.get("Retroceso").toString())
                binding.infoWhereNum.setText(it.get("Ubicación").toString())
            }.await()// Esperar a que la operación de obtener el documento se complete
        }
    }
    /**
     * Descarga y muestra las imágenes de arma y perks en la vista correspondiente.
     *
     * @param id El ID del arma para el que se descargan las imágenes.
     */
    private suspend fun ponerimagenes(id:String){
        lifecycleScope.launch {
            // Referencia al archivo de imagen del arma
            val storageRef =
                FirebaseStorage.getInstance().reference.child("image/Armas/$id" + "Image.jpeg")
            // Referencia al archivo de imagen de los perks del arma
            val storageRefPerks =
                FirebaseStorage.getInstance().reference.child("image/Perks/$id" + "perk.jpeg")

            // Descarga y muestra la imagen del arma
            var localfile = File.createTempFile("tempImage", "jpeg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgObject.setImageBitmap(bitmap)
            }.await()
            binding.cargandoImg.visibility=View.INVISIBLE
            binding.imgObject.visibility=View.VISIBLE

            // Descarga y muestra la imagen de los perks del arma
            storageRefPerks.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgPerks.setImageBitmap(bitmap)
            }.await()
            binding.cargandoPerks.visibility=View.INVISIBLE
            binding.imgPerks.visibility=View.VISIBLE
        }
    }
    /**
    * Agrega un objeto a la lista de favoritos del usuario actual.
    * @param id el ID del objeto a agregar.
     */
    fun favoritosadd(id: String){
        auth = Firebase.auth
        var actual = auth.currentUser
        // Agregar un documento con el ID y el nombre del objeto a la colección de favoritos del usuario actual
        bd.collection("Users").document(actual!!.email.toString()).collection("Favoritos").document(id).set(
            hashMapOf(
                "Nombre" to binding.txtNameObj.text.toString()
            )
        )
        // Cambiar la imagen de la estrella a "encendida"
        binding.imgStar.setImageResource(R.drawable.estrellaon)
    }
    /**

    * Elimina el arma con el ID especificado de la colección "Favoritos" del usuario actual.
    * @param id el ID del arma a eliminar de los favoritos del usuario
     */
    fun favoritosdelete (id:String){
        auth = Firebase.auth
        var actual = auth.currentUser
        // Elimina un documento con el id de la subcoleccion de favoritos del usuario actual
        bd.collection("Users").document(actual!!.email.toString()).collection("Favoritos").document(id).delete()
        // Cambiar la imagen de la estrella a "apagada"
        binding.imgStar.setImageResource(R.drawable.estrellaoff)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Info_objects.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Info_objects().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}