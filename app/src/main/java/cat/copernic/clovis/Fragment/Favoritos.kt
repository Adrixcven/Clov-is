package cat.copernic.clovis.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.R
import cat.copernic.clovis.adapter.armasAdapter
import cat.copernic.clovis.adapter.favAdapter
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.data.dataFav
import cat.copernic.clovis.databinding.FragmentFavoritosBinding
import cat.copernic.clovis.databinding.FragmentSeleccionarArmaBinding
import cat.copernic.clovis.datalist.ArmasList
import cat.copernic.clovis.datalist.FavList
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
 * Use the [Favoritos.newInstance] factory method to
 * create an instance of this fragment.
 */
class Favoritos : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentFavoritosBinding
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
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)!!.updateActionBarTitle("Favoritos")
        initRecyclerView(view)
    }
    private suspend fun recycleFav() {
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            var documents = bd.collection("Users").document(actual!!.email.toString()).collection("Favoritos").get().await()
            for (document in documents) {
                val documentId: String = document.getId()
                val storageRef = FirebaseStorage.getInstance().reference.child("image/Little/$documentId"+"Little.jpeg")

                // Descargar la imagen del Storage y convertirla a Bitmap
                val bitmap = try {
                    val localFile = File.createTempFile("tempImage", "jpeg")
                    storageRef.getFile(localFile).await()
                    BitmapFactory.decodeFile(localFile.absolutePath)
                }catch (e: Exception) {
                    // Manejar el error
                    null
                }

                val wallItem = dataFav(
                    nombre = document["Nombre"].toString(),
                    imageResourceId = bitmap,
                    id = documentId
                )
                if (FavList.favoritos.isEmpty()) {
                    FavList.favoritos.add(wallItem)
                } else {
                    var contador = 0
                    for (i in FavList.favoritos) {
                        if (wallItem.id == i.id) {
                            contador++
                        }

                    }
                    if(contador <1){
                        FavList.favoritos.add(wallItem)
                    }
                }
            }
            binding.recyclerFavoritos.layoutManager = LinearLayoutManager(context)
            binding.recyclerFavoritos.adapter = favAdapter(FavList.favoritos)


        }
    }
    private fun initRecyclerView(view: View) {
        if (FavList.favoritos.isEmpty()) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    recycleFav()
                    binding.loadingFav.visibility = View.INVISIBLE
                    binding.recyclerFavoritos.visibility = View.VISIBLE
                }
            }
        }else {
            binding.recyclerFavoritos.layoutManager = LinearLayoutManager(context)
            binding.recyclerFavoritos.adapter = favAdapter(FavList.favoritos)
            binding.loadingFav.visibility = View.INVISIBLE
            binding.recyclerFavoritos.visibility = View.VISIBLE
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Favoritos.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Favoritos().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}