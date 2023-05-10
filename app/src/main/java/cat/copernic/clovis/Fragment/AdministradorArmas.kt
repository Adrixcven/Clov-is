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
import cat.copernic.clovis.adapter.adminAdapter
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.databinding.FragmentAdministradorArmasBinding
import cat.copernic.clovis.datalist.AdminList
import cat.copernic.clovis.datalist.ArmasList
import cat.copernic.clovis.datalist.FavList
import com.google.firebase.firestore.FirebaseFirestore
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
 * Use the [AdministradorArmas.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdministradorArmas : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAdministradorArmasBinding
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
        binding = FragmentAdministradorArmasBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
    * Método que se llama cuando la vista asociada a este fragment se ha creado.
    * Configura la action bar para que muestre "Administrador".
    * Llama al método initRecyclerView() para inicializar el RecyclerView.
    * @param view La vista raíz de la jerarquía de vistas del fragment.
    * @param savedInstanceState Objeto Bundle que contiene los datos guardados en la última instancia del fragment o null si no hay datos guardados.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configura la action bar con el título "Administrador"
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Administrador")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Administrador")
        }
        // Inicializa el RecyclerView
        initRecyclerView(view)
    }

    /**
    * Se llama cuando la actividad o el fragmento vuelve a estar en primer plano después de estar en segundo plano.
    * Inicializa el RecyclerView en la vista del fragmento.
    * @param view La vista raíz del fragmento.
     */
    override fun onResume() {
        super.onResume()
        initRecyclerView(requireView())
    }
    /**
    * Descarga los datos de las armas de la base de datos y los añade a la lista de Administrador.
    * Después de descargar y procesar los datos, configura el RecyclerView para mostrarlos.
    * @throws Exception si se produce un error al descargar o procesar los datos.
     */
    private suspend fun recycleAdmin() {
        lifecycleScope.launch {
            // Obtener todos los documentos de la colección "Armas"
            var documents = bd.collection("Armas").get().await()
            for (document in documents) {
                // Obtener el ID del documento y crear una referencia al archivo de imagen en el Storage
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

                // Crear un objeto dataAdmin con los datos descargados y procesados
                val wallItem = dataAdmin(
                    nombre = document["Nombre"].toString(),
                    imageResourceId = bitmap,
                    id = documentId,
                    editar = R.drawable.edit,
                    borrar = R.drawable.trash
                )
                // Añadir el objeto dataAdmin a la lista de Armas de administradores
                if (AdminList.admin.isEmpty()) {
                    AdminList.admin.add(wallItem)
                } else {
                    var contador = 0
                    for (i in AdminList.admin) {
                        if (wallItem.id == i.id) {
                            contador++
                        }

                    }
                    if(contador <1){
                        AdminList.admin.add(wallItem)
                    }
                }
            }
            // Configurar el RecyclerView para mostrar los datos descargados y procesados
            binding.recyclerAdmin.layoutManager = LinearLayoutManager(context)
            binding.recyclerAdmin.adapter = adminAdapter(AdminList.admin, ArmasList.armas, FavList.favoritos)

        }
    }

    /**

    * Inicializa el RecyclerView para mostrar la lista de armas de los administradores.
    * Si la lista está vacía, se inicia una tarea asíncrona para obtener los datos de Firebase Firestore.
    * Si la lista ya tiene datos, se configura el LinearLayoutManager y se muestra la lista.
    * @param view La vista del fragmento donde se encuentra el RecyclerView.
     */
    private fun initRecyclerView(view: View) {
        if (AdminList.admin.isEmpty()) {
            // Si la lista de armas de los administradores está vacía, se obtienen los datos de la base de datos en un hilo secundario
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    recycleAdmin()
                    // Una vez obtenidos los datos, se actualiza la interfaz de usuario para mostrar la lista de armas de los administradores.
                    binding.loadingAdmin.visibility = View.INVISIBLE
                    binding.recyclerAdmin.visibility = View.VISIBLE
                }
            }
        }else {
            // Si la lista de administradores no está vacía, se establece el LinearLayoutManager y el adapter correspondiente para mostrar la lista de administradores, armas y favoritos.
            binding.recyclerAdmin.layoutManager = LinearLayoutManager(context)
            binding.recyclerAdmin.adapter = adminAdapter(AdminList.admin, ArmasList.armas, FavList.favoritos)
            // Luego, se actualiza la interfaz de usuario para mostrar la lista de armas de los administradores.
            binding.loadingAdmin.visibility = View.INVISIBLE
            binding.recyclerAdmin.visibility = View.VISIBLE
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdministradorArmas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdministradorArmas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}