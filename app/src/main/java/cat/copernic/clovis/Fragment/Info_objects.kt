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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInfoObjectsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = args.id
        auth = Firebase.auth
        var actual = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val subcollectionRef = db.collection("Users").document(actual!!.email.toString()).collection("Favoritos")
        subcollectionRef.document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        binding.imgStar.setImageResource(R.drawable.estrellaon)
                    }
                }
            }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerdatos(id)
                ponerimagenes(id)
            }
        }
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
    private suspend fun ponerdatos(id: String) {
        lifecycleScope.launch {
            bd.collection("Armas").document(id).get().addOnSuccessListener {
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
            }.await()
        }
    }
    private suspend fun ponerimagenes(id:String){
        lifecycleScope.launch {
            val storageRefPerks =
                FirebaseStorage.getInstance().reference.child("image/Perks/$id" + "perk.jpeg")
            val storageRef =
                FirebaseStorage.getInstance().reference.child("image/Armas/$id" + "Image.jpeg")

            var localfile = File.createTempFile("tempImage", "jpeg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgObject.setImageBitmap(bitmap)
            }.await()
            binding.cargandoImg.visibility=View.INVISIBLE
            binding.imgObject.visibility=View.VISIBLE
            storageRefPerks.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgPerks.setImageBitmap(bitmap)
            }.await()
            binding.cargandoPerks.visibility=View.INVISIBLE
            binding.imgPerks.visibility=View.VISIBLE
        }
    }
    fun favoritosadd(id: String){
        auth = Firebase.auth
        var actual = auth.currentUser
        bd.collection("Users").document(actual!!.email.toString()).collection("Favoritos").document(id).set(
            hashMapOf(
                "Nombre" to binding.txtNameObj.text.toString()
            )
        )
        binding.imgStar.setImageResource(R.drawable.estrellaon)
    }
    fun favoritosdelete (id:String){
        auth = Firebase.auth
        var actual = auth.currentUser
        bd.collection("Users").document(actual!!.email.toString()).collection("Favoritos").document(id).delete()
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