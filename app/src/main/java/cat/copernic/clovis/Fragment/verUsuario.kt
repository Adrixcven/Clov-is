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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerdatos()
                ponerimagenes()
            }
        }


        binding.btnEditar.setOnClickListener{
            val id = binding.cabUserName.text.toString()
            val directions = verUsuarioDirections.actionVerUsuarioToEditarUsuario(id)
            view.findNavController().navigate(directions)
        }
    }
    private suspend fun ponerdatos() {
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            bd.collection("Users").document(actual!!.email.toString()).get().addOnSuccessListener {
                binding.cabUserName.text = it.get("id").toString()
                binding.cabUserId.text = it.get("name").toString()
                binding.textoDeDescripcion.text = it.get("descripcion").toString()
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
                    }else{
                        binding.textoDeClase.text = "Sin clase"

                    }
                }
            }
        }
    }
    private suspend fun ponerimagenes(){
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            var email = actual!!.email.toString()
            val storageRef =FirebaseStorage.getInstance().reference.child("image/usuarios/$email.jpeg")
            storageRef.metadata.addOnSuccessListener {
                var localfile = File.createTempFile("tempImage", "jpeg")
                storageRef.getFile(localfile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    binding.imgUsuario.setImageBitmap(bitmap)
                }
            }.await()
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