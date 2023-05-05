package cat.copernic.clovis.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Activity.MainActivity_Users
import cat.copernic.clovis.Models.Arma
import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.data.dataFav
import cat.copernic.clovis.databinding.FragmentEditarWeaponBinding
import cat.copernic.clovis.datalist.AdminList
import cat.copernic.clovis.datalist.ArmasList
import cat.copernic.clovis.datalist.FavList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editarArma.newInstance] factory method to
 * create an instance of this fragment.
 */
class editarArma : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentEditarWeaponBinding
    val args: editarArmaArgs by navArgs()
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
        binding = FragmentEditarWeaponBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
    * Método que se ejecuta al crear la vista.
    * Se encarga de actualizar el título de la barra de acción, obtener el id del arma a editar y cargar los datos e imágenes correspondientes.
    * También se configuran los onClickListeners para cada una de las imágenes y el botón "Guardar".
    * @param view la vista creada.
    * @param savedInstanceState instancia guardada del estado de la aplicación.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Actualizar el título de la barra de acción
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Editar Arma")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Editar Arma")
        }
        // Obtener el id del arma a editar
        id = args.id
        // Cargar los datos e imágenes correspondientes
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ponerdatos(id)
                ponerimagenes(id)
            }
        }

        // Configurar los onClickListeners para las imágenes
        binding.imgObject.setOnClickListener {
            intentimgweapon.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.imgPerks.setOnClickListener {
            intentimgperk.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.imgObjectLittle.setOnClickListener {
            intentimgLittle.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        // Configurar el onClickListener para el botón "Guardar"
        binding.Guardar.setOnClickListener {
            addweapon(id)
            var arma = llegirDades()
            // Actualizar los datos del arma editada en AdminList.admin
            for (i in AdminList.admin.indices) {
                val dataArma = AdminList.admin[i]
                if (dataArma.id == id) {
                    var bitmapa = AdminList.admin[i].imageResourceId
                    val wallItem = dataAdmin(
                        nombre = arma.nombre,
                        imageResourceId = bitmapa,
                        id = id,
                        editar = R.drawable.edit,
                        borrar = R.drawable.trash
                    )
                    // Eliminar el objeto dataArma del array
                    AdminList.admin.set(i, wallItem)
                    break
                }
            }
            // Actualizar los datos del arma editada en ArmasList.armas
            for (i in ArmasList.armas.indices) {
                val dataArma = ArmasList.armas[i]
                if (dataArma.id == id) {
                    var bitmapa = ArmasList.armas[i].imageResourceId
                    val wallItem = dataArma(
                        nombre = arma.nombre,
                        imageResourceId = bitmapa,
                        id = id,
                    )
                    // Eliminar el objeto dataArma del array
                    ArmasList.armas.set(i, wallItem)
                    break
                }
            }
            // Actualizar los datos del arma editada en FavList.favoritos
            for (i in FavList.favoritos.indices) {
                val dataArma = FavList.favoritos[i]
                if (dataArma.id == id) {
                    var bitmapa = FavList.favoritos[i].imageResourceId
                    val wallItem = dataFav(
                        nombre = arma.nombre,
                        imageResourceId = bitmapa,
                        id = id,
                    )
                    // Eliminar el objeto dataArma del array
                    FavList.favoritos.set(i, wallItem)
                    break
                }
            }
            // Navegar hacia la pantalla del administrador de armas
            view.findNavController().navigate(R.id.action_editarArma_to_administradorArmas)
        }
    }
    private var storage = FirebaseStorage.getInstance()
    private var storageRefarmas  = storage.getReference().child("image/Armas").child("a.jpeg")
    private var storageRefPerks  = storage.getReference().child("image/Perks").child("a.jpeg")
    private var storageRefLittle  = storage.getReference().child("image/Little").child("a.jpeg")

    /**
     * Inicia el registro de actividad para seleccionar una imagen de la galería y guardarla en Firebase Storage como imagen del arma.
     * @param return el resultado de la actividad, incluyendo la URI de la imagen seleccionada.
     */
    private val intentimgweapon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Verificar que el resultado sea OK
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = result.data?.data
            // Verificar que la URI no sea nula
            if (imageUri != null) {
                try {
                    // Obtener el nombre del archivo
                    var texta = id + "Image.jpeg"
                    // Obtener el stream de la imagen seleccionada y decodificarlo a Bitmap
                    val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    // Comprimir la imagen a JPEG y obtener los bytes
                    val baos = ByteArrayOutputStream()
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    // Subir la imagen al Storage de Firebase
                    storageRefarmas = storage.getReference().child("image/Armas").child(texta)
                    var uploadTask = storageRefarmas.putBytes(data)
                    uploadTask.addOnFailureListener {
                    }.addOnSuccessListener { taskSnapshot ->
                    }
                    // Mostrar la imagen en el ImageView correspondiente
                    binding.imgObject.setImageBitmap(selectedImage)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
    * Inicia el registro de actividad para seleccionar una imagen de la galería y guardarla en Firebase Storage como imagen de Perk.
    * @param return el resultado de la actividad, incluyendo la URI de la imagen seleccionada.
     */
    private val intentimgperk = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // Verificar que el resultado sea OK
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = result.data?.data
            // Verificar que la URI no sea nula
            if (imageUri != null) {
                try {
                    // Obtener el nombre del archivo
                    var texta = id + "perk.jpeg"
                    // Obtener el stream de la imagen seleccionada y decodificarlo a Bitmap
                    val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    // Comprimir la imagen a JPEG y obtener los bytes
                    val baos = ByteArrayOutputStream()
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    // Subir la imagen al Storage de Firebase
                    storageRefPerks = storage.getReference().child("image/Perks").child(texta)
                    var uploadTask = storageRefPerks.putBytes(data)
                    uploadTask.addOnFailureListener {
                    }.addOnSuccessListener { taskSnapshot ->
                    }
                    // Mostrar la imagen en el ImageView correspondiente
                    binding.imgPerks.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
    * Función que se encarga de registrar y manejar el resultado de la actividad para seleccionar una imagen para el objeto en su versión "pequeña"
    * @param result El resultado de la actividad para seleccionar la imagen
     */
    private val intentimgLittle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // Verificar que el resultado sea OK
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = result.data?.data
            // Verificar que la URI no sea nula
            if (imageUri != null) {
                try {
                    // Obtener el nombre del archivo
                    var texta = id + "Little.jpeg"
                    // Obtener el stream de la imagen seleccionada y decodificarlo a Bitmap
                    val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    // Comprimir la imagen a JPEG y obtener los bytes
                    val baos = ByteArrayOutputStream()
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    // Subir la imagen al Storage de Firebase
                    storageRefLittle = storage.getReference().child("image/Little").child(texta)
                    var uploadTask = storageRefLittle.putBytes(data)
                    uploadTask.addOnFailureListener {
                    }.addOnSuccessListener { taskSnapshot ->
                    }
                    // Mostrar la imagen en el ImageView correspondiente
                    binding.imgObjectLittle.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
    * Función que recupera los datos de un arma de la base de datos Firestore y los coloca en los EditText correspondientes en la vista de edición.
    * @param id el ID del documento que contiene los datos del arma en Firestore.
     */
    private suspend fun ponerdatos(id : String){
        lifecycleScope.launch {
            // Acceder al documento correspondiente en Firestore y establecer los valores en los EditText
            bd.collection("Armas").document(id).get().addOnSuccessListener {
                binding.editarNombreArma.setText(it.get("Nombre").toString())
                binding.editarDescripcionArma.setText(it.get("Descripcion").toString())
                binding.editarAmmoArma.setText(it.get("Cargador").toString())
                binding.editarDisparosArma.setText(it.get("Disparos por minuto").toString())
                binding.editarImpactArma.setText(it.get("Impacto").toString())
                binding.editarDistanciaArma.setText(it.get("Rango").toString())
                binding.editarStabilityArma.setText(it.get("Estabilidad").toString())
                binding.editarReloadArma.setText(it.get("Recarga").toString())
                binding.editarMagazineArma.setText(it.get("Tamaño del inventario").toString())
                binding.editarAimassistArma.setText(it.get("Asistencia de apuntado").toString())
                binding.editarZoommArma.setText(it.get("Zoom").toString())
                binding.editarAirassistArma.setText(it.get("Asistencia en aire").toString())
                binding.editarRetrocesoArma.setText(it.get("Retroceso").toString())
                binding.editarFindArma.setText(it.get("Ubicación").toString())
            }.await()
        }
    }

    /**
    * Descarga las imágenes de una arma desde Firebase Storage y las muestra en la pantalla de detalle.
    * @param id El identificador de la arma de la que se van a descargar las imágenes.
     */
    private suspend fun ponerimagenes(id:String){
        lifecycleScope.launch {
            // Referencias a las imágenes en Firebase Storage
            val storageRefLittle =
                FirebaseStorage.getInstance().reference.child("image/Little/$id" + "Little.jpeg")
            val storageRefPerks =
                FirebaseStorage.getInstance().reference.child("image/Perks/$id" + "perk.jpeg")
            val storageRef =
                FirebaseStorage.getInstance().reference.child("image/Armas/$id" + "Image.jpeg")

            // Descarga la imagen principal de la arma y la muestra en la pantalla
            var localfile = File.createTempFile("tempImage", "jpeg")
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgObject.setImageBitmap(bitmap)
            }.await()
            binding.cargaImg.visibility=View.INVISIBLE
            binding.imgObject.visibility=View.VISIBLE
            // Descarga la imagen de los perks de la arma y la muestra en la pantalla

            storageRefPerks.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgPerks.setImageBitmap(bitmap)
            }.await()
            binding.cargaPerks.visibility=View.INVISIBLE
            binding.imgPerks.visibility=View.VISIBLE
            // Descarga la imagen en miniatura de la arma y la muestra en la pantalla
            storageRefLittle.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imgObjectLittle.setImageBitmap(bitmap)
            }.await()
            binding.cargaImgLittle.visibility=View.INVISIBLE
            binding.imgObjectLittle.visibility=View.VISIBLE
        }
    }

    /**
    * Método que lee los datos introducidos por el usuario y los guarda en un objeto Arma.
    * @return objeto Arma con los datos introducidos por el usuario.
     */
    fun llegirDades(): Arma {
        // Guardamos los datos introducidos por el usuario
        var nombre = binding.editarNombreArma.text.toString()
        var descripcion = binding.editarDescripcionArma.text.toString()
        var cargador = binding.editarAmmoArma.text.toString().toInt()
        var disparos = binding.editarDisparosArma.text.toString().toInt()
        var impacto = binding.editarImpactArma.text.toString().toInt()
        var rango = binding.editarDistanciaArma.text.toString().toInt()
        var estabilidad = binding.editarStabilityArma.text.toString().toInt()
        var recarga = binding.editarReloadArma.text.toString().toInt()
        var aim = binding.editarAimassistArma.text.toString().toInt()
        var magazine = binding.editarMagazineArma.text.toString().toInt()
        var zoom = binding.editarZoommArma.text.toString().toInt()
        var aire = binding.editarAirassistArma.text.toString().toInt()
        var recoil = binding.editarRetrocesoArma.text.toString().toInt()
        var donde = binding.editarFindArma.text.toString()

        // Devolvemos un objeto Arma con los datos introducidos
        return Arma(nombre, descripcion, null, cargador, disparos, impacto, rango, estabilidad, recarga, aim, magazine, zoom, aire, recoil,null, donde)
    }

    /**
    * Función que añade un arma a la colección "Armas" de Firestore con los datos proporcionados.
    * @param id El id del documento en la colección.
     */
    fun addweapon(id : String){
        // Leer los datos del arma
        var arma = llegirDades()
        bd.collection("Armas").document(id).set(
            hashMapOf(
                "Nombre" to arma.nombre,
                "Descripcion" to arma.descripcion,
                "Cargador" to arma.cargador,
                "Disparos por minuto" to arma.disparos,
                "Impacto" to arma.impacto,
                "Rango" to arma.rango,
                "Estabilidad" to arma.estabilidad,
                "Recarga" to arma.recarga,
                "Asistencia de apuntado" to arma.aim,
                "Tamaño del inventario" to arma.inventario,
                "Zoom" to arma.zoom,
                "Asistencia en aire" to arma.aire,
                "Retroceso" to arma.recoil,
                "Ubicación" to arma.donde
            )).addOnFailureListener{//No se ha añadido el arma
            // Si no se ha podido añadir el arma, mostrar un diálogo de error
            val context: Context = requireContext()
            val builder = AlertDialog.Builder(context)
            builder.setMessage("No se ha podido crear el arma")
            builder.setPositiveButton("Aceptar", null)
            val dialog = builder.create()
            dialog.show()
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editarArma.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editarArma().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}