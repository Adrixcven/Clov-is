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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var nom = args.id
        var selectedOption = ""
        var admina = MutableLiveData<Boolean>(false)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerusuario(admina)
                ponerfoto()
            }
        }

        classSpinner = binding.spinnerClases
        ArrayAdapter.createFromResource(
            requireContext(),
            cat.copernic.clovis.R.array.class_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
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


        binding.fotoperfil.setOnClickListener{
            intentfotoperfil.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        binding.guardarPerfil.setOnClickListener {
            modificarusuario(selectedOption, admina)

            view.findNavController().navigate(cat.copernic.clovis.R.id.action_editar_usuario_to_verUsuario)
        }
    }
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference().child("image/usuarios").child(".jpeg")
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
    private suspend fun ponerusuario(admina: MutableLiveData<Boolean>){
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
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
                admina.value = it.get("admin") as Boolean
            }.await()
        }
    }
    private suspend fun ponerfoto(){
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            val email = actual!!.email.toString()
            val ref = FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")
            ref.getMetadata().addOnSuccessListener {
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")

                var localfile = File.createTempFile("tempImage", "jpeg")
                storageRef.getFile(localfile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    binding.fotoperfil.setImageBitmap(bitmap)
                }
            }.await()
        }
    }
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
    fun llegirDades(selectedoOption:String, admina: MutableLiveData<Boolean>): Usuario {
        //Guardem les dades introduïdes per l'usuari
        var nom = binding.nom.text.toString()
        var id = binding.id.text.toString()
        var Correo = binding.correoElectronico.text.toString()
        var clase = selectedoOption
        var info = binding.informacion.text.toString()
        var admin = admina.value


        //Afegim els treballadors introduïts per l'usuari a l'atribut treballadors

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