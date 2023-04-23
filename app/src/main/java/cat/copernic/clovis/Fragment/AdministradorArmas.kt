package cat.copernic.clovis.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
    }
    override fun onResume() {
        super.onResume()
        initRecyclerView(requireView())
    }
    private suspend fun recycleAdmin() {
        lifecycleScope.launch {
            var documents = bd.collection("Armas").get().await()
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

                val wallItem = dataAdmin(
                    nombre = document["Nombre"].toString(),
                    imageResourceId = bitmap,
                    id = documentId,
                    editar = R.drawable.edit,
                    borrar = R.drawable.trash
                )
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
            binding.recyclerAdmin.layoutManager = LinearLayoutManager(context)
            binding.recyclerAdmin.adapter = adminAdapter(AdminList.admin, ArmasList.armas, FavList.favoritos)

        }
    }

    private fun initRecyclerView(view: View) {
        if (AdminList.admin.isEmpty()) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    recycleAdmin()
                }
            }
        }else {
            binding.recyclerAdmin.layoutManager = LinearLayoutManager(context)
            binding.recyclerAdmin.adapter = adminAdapter(AdminList.admin, ArmasList.armas, FavList.favoritos)
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