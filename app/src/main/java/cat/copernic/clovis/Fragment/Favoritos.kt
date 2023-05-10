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
import cat.copernic.clovis.Activity.MainActivity_Users
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

    /**
    * Se llama cuando la vista asociada a este fragmento ha sido creada.
    * @param view La vista inflada por el fragmento.
    * @param savedInstanceState El estado anterior del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Actualiza el título de la ActionBar en la MainActivity
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Favoritos")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Favoritos")
        }
        // Inicializa el RecyclerView
        initRecyclerView(view)
    }

    /**
    * Descarga los documentos de la colección "Favoritos" del usuario actual de Firestore y los muestra en el RecyclerView de la pestaña de Favoritos.
    * Cada elemento del RecyclerView consta de un nombre y una imagen asociada descargada del Storage de Firebase.
     */
    private suspend fun recycleFav() {
        lifecycleScope.launch {
            auth = Firebase.auth
            var actual = auth.currentUser
            // Obtiene los documentos de la colección "Favoritos" del usuario actual
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
                // Crear objeto de datos del RecyclerView y agregarlo a la lista de favoritos si no existe ya en ella o si esta vacia
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
            // Mostrar los elementos en el RecyclerView de la pestaña de Favoritos
            binding.recyclerFavoritos.layoutManager = LinearLayoutManager(context)
            binding.recyclerFavoritos.adapter = favAdapter(FavList.favoritos)


        }
    }
    /**

    * Inicializa el RecyclerView de la lista de favoritos.
    * Si la lista de favoritos está vacía, se lanza una corutina para cargar los datos de Firestore y actualizar la lista. Una vez cargados los datos, se actualiza la vista del RecyclerView.
    * Si la lista de favoritos no está vacía, se establece el LayoutManager y el Adapter del RecyclerView con los datos existentes en la lista.
    * @param view la vista que contiene el RecyclerView.
     */

    private fun initRecyclerView(view: View) {
        if (FavList.favoritos.isEmpty()) {
            // Si la lista de favoritos está vacía, lanzar una corutina para cargar los datos
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    // Llamar a la función para cargar los datos desde Firestore y actualizar la lista de favoritos
                    recycleFav()
                    // Una vez cargados los datos, actualizar la visibilidad de los elementos de la vista
                    binding.loadingFav.visibility = View.INVISIBLE
                    binding.recyclerFavoritos.visibility = View.VISIBLE
                }
            }
        }else {
            // Si la lista de favoritos no está vacía, establecer el LayoutManager y el Adapter del RecyclerView
            binding.recyclerFavoritos.layoutManager = LinearLayoutManager(context)
            binding.recyclerFavoritos.adapter = favAdapter(FavList.favoritos)
            // Actualizar la visibilidad de los elementos de la vista
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