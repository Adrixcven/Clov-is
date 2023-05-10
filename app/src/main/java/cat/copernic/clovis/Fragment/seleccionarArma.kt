package cat.copernic.clovis.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Activity.MainActivity_Users
import cat.copernic.clovis.R
import cat.copernic.clovis.adapter.armasAdapter
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.databinding.FragmentSeleccionarArmaBinding
import cat.copernic.clovis.datalist.ArmasList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hotchemi.android.rate.AppRate
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
 * Use the [SeleccionarArma.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeleccionarArma : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSeleccionarArmaBinding
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
        binding = FragmentSeleccionarArmaBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
    * Método para sobrescribir el comportamiento predeterminado de onViewCreated en un fragmento.
    * @param view La vista inflada del fragmento.
    * @param savedInstanceState Instancia anterior del estado guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializar el RecyclerView.
        initRecyclerView(view)
        // Actualizar el título de la ActionBar a "Seleccionar Arma".
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Seleccionar Arma")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Seleccionar Arma")
        }

        // Configurar y mostrar el diálogo de valoración de la aplicación si se cumplen las condiciones.
        AppRate.with(requireActivity()).setInstallDays(0).setLaunchTimes(2).setRemindInterval(1).monitor()
        AppRate.showRateDialogIfMeetsConditions(requireActivity())
    }
    /**
    * Método para sobrescribir el comportamiento del método [onResume] de la actividad o fragmento.
    * Este método llama al método [initRecyclerView] pasándole la vista raíz del fragmento.
     */
    override fun onResume() {
        super.onResume()
        initRecyclerView(requireView())
    }
    /**
    * Descarga y muestra en un RecyclerView las armas almacenadas en Firestore.
    * Se descarga la imagen de cada arma del Storage y se convierte a Bitmap para mostrarla en el RecyclerView.
    * Si la lista de armas ya contiene el arma, no se añade de nuevo.
     */
    private suspend fun recycleArmas() {
        lifecycleScope.launch {
            // Obtener los documentos de la colección "Armas"
            var documents = bd.collection("Armas").get().await()
            for (document in documents) {
                // Obtener el ID del documento y la referencia a la imagen del Storage
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

                // Crear un objeto dataArma con los datos del documento y la imagen descargada
                val wallItem = dataArma(
                    nombre = document["Nombre"].toString(),
                    imageResourceId = bitmap,
                    id = documentId
                )
                // Si la lista de armas está vacía, se añade el objeto directamente
                if (ArmasList.armas.isEmpty()) {
                    ArmasList.armas.add(wallItem)
                } else {
                    // Si la lista de armas ya contiene el arma, no se añade de nuevo
                    var contador = 0
                    for (i in ArmasList.armas) {
                        if (wallItem.id == i.id) {
                            contador++
                        }

                    }
                    if(contador <1){
                        ArmasList.armas.add(wallItem)
                    }
                }
            }
            // Configurar el RecyclerView y el Adapter
            binding.recyclerArmas.layoutManager = LinearLayoutManager(context)
            binding.recyclerArmas.adapter = armasAdapter(ArmasList.armas)

        }
    }

    /**
    * Inicializa el RecyclerView de armas. Si la lista de armas está vacía, llama a la función recycleArmas()
    * para cargar los datos desde Firebase y actualizar la lista. De lo contrario, configura el RecyclerView
    * con el adaptador correspondiente y oculta el indicador de carga.
    * @param view La vista que contiene el RecyclerView.
     */
    private fun initRecyclerView(view: View) {
        if (ArmasList.armas.isEmpty()) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    // Cargar los datos desde Firebase
                    recycleArmas()
                    // Ocultar el indicador de carga y mostrar el RecyclerView
                    binding.loadingArmas.visibility = View.INVISIBLE
                    binding.recyclerArmas.visibility = View.VISIBLE
                }
            }
        }else {
            // Configurar el RecyclerView con el adaptador correspondiente
            binding.recyclerArmas.layoutManager = LinearLayoutManager(context)
            binding.recyclerArmas.adapter = armasAdapter(ArmasList.armas)
            // Ocultar el indicador de carga y mostrar el RecyclerView
            binding.loadingArmas.visibility = View.INVISIBLE
            binding.recyclerArmas.visibility = View.VISIBLE
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SeleccionarArma.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeleccionarArma().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}