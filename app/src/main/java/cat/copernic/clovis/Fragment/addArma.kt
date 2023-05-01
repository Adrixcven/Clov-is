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
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Models.Arma
import cat.copernic.clovis.R
import cat.copernic.clovis.Utils.Utilities
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.databinding.FragmentAddArmaBinding
import cat.copernic.clovis.datalist.AdminList
import cat.copernic.clovis.datalist.ArmasList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addArma.newInstance] factory method to
 * create an instance of this fragment.
 */
class addArma : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var channelID = "ChannelID"
    private var channelName = "ChannelName"
    private val notificationID = 0

    private lateinit var binding: FragmentAddArmaBinding
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
        binding = FragmentAddArmaBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)!!.updateActionBarTitle("Añadir arma")

        binding.imgObject.setOnClickListener {
            intentimgweapon.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.imgPerks.setOnClickListener {
            intentimgperk.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.imgObjectLittle.setOnClickListener {
            intentimgLittle.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.Guardar.setOnClickListener {
            addweapon()

        }
    }
    private var storage = FirebaseStorage.getInstance()
    private var storageRefarmas  = storage.getReference().child("image/Armas").child("a.jpeg")
    private var storageRefPerks  = storage.getReference().child("image/Perks").child("a.jpeg")
    private var storageRefLittle  = storage.getReference().child("image/Little").child("a.jpeg")
    private val intentimgweapon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                try {
                if(binding.editarNombreArma.text.toString() != ""){
                        var texta = binding.editarNombreArma.text.toString() + "Image.jpeg"
                        val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val baos = ByteArrayOutputStream()
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storageRefarmas = storage.getReference().child("image/Armas").child(texta)
                        var uploadTask = storageRefarmas.putBytes(data)
                        uploadTask.addOnFailureListener {
                        }.addOnSuccessListener { taskSnapshot ->
                        }
                        binding.imgObject.setImageBitmap(selectedImage)
                    }else{
                        Toast.makeText(requireContext(), "No existe ningun nombre para identificar la imagen", Toast.LENGTH_LONG).show()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val intentimgperk = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                try {
                    if(binding.editarNombreArma.text.toString() != ""){
                        var texta = binding.editarNombreArma.text.toString() + "perk.jpeg"
                        val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val baos = ByteArrayOutputStream()
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storageRefPerks = storage.getReference().child("image/Perks").child(texta)
                        var uploadTask = storageRefPerks.putBytes(data)
                        uploadTask.addOnFailureListener {
                        }.addOnSuccessListener { taskSnapshot ->
                        }
                        binding.imgPerks.setImageBitmap(selectedImage)
                    }else{
                        Toast.makeText(requireContext(), "No existe ningun nombre para identificar la imagen", Toast.LENGTH_LONG).show()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    val context: Context = requireContext()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Archivo no encontrado")
                    builder.setPositiveButton("Aceptar", null)
                    val dialog = builder.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    var selectedImage= Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private val intentimgLittle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                try {
                    if (binding.editarNombreArma.text.toString() != ""){
                        var texta = binding.editarNombreArma.text.toString() + "Little.jpeg"
                        val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                        selectedImage = BitmapFactory.decodeStream(imageStream)
                        val baos = ByteArrayOutputStream()
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storageRefLittle = storage.getReference().child("image/Little").child(texta)
                        var uploadTask = storageRefLittle.putBytes(data)
                        uploadTask.addOnFailureListener {
                        }.addOnSuccessListener { taskSnapshot ->
                        }
                        binding.imgObjectLittle.setImageBitmap(selectedImage)
                    }else{
                        Toast.makeText(requireContext(), "No existe ningun nombre para identificar la imagen", Toast.LENGTH_LONG).show()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun addweapon(){
        var arma = llegirDades()
        val db = FirebaseFirestore.getInstance()
        val subcollectionRef = db.collection("Armas")
        subcollectionRef.document(arma.nombre).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val context: Context = requireContext()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("El arma ya existe")
                    builder.setPositiveButton("Aceptar", null)
                    val dialog = builder.create()
                    dialog.show()
                }else{
                    bd.collection("Armas").document(arma.nombre).set(
                        //En lloc d'afegir un objecte, també podem passar els parells clau valor d'un document mitjançant un hashMpa. Si hem de passar tots els
                        // atributs d'un objecte passarem com a paràmetre l'objecte no un hashMap amb els seus atributs.
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
                            "Tamano del inventario" to arma.inventario,
                            "Zoom" to arma.zoom,
                            "Asistencia en aire" to arma.aire,
                            "Retroceso" to arma.recoil,
                            "Ubicación" to arma.donde
                        )).addOnSuccessListener {
                        val wallItem = dataArma(
                            nombre = arma.nombre,
                            imageResourceId = selectedImage,
                            id = arma.nombre
                        )
                        ArmasList.armas.add(wallItem)
                        if(AdminList.admin.isEmpty()){
                        }else{
                            val wallItemadmin = dataAdmin(
                                nombre = arma.nombre,
                                imageResourceId = selectedImage,
                                id = arma.nombre,
                                editar = R.drawable.edit,
                                borrar = R.drawable.trash
                            )
                            AdminList.admin.add(wallItemadmin)
                        }
                        Utilities.createNotificationChannel(channelName, channelID, requireContext())
                        Utilities.createNotification(requireContext(), channelID, notificationID)
                        view?.findNavController()?.navigate(R.id.action_addArma_to_seleccionarArma)
                    }
                        .addOnFailureListener{ //No s'ha afegit el departament...
                        val context: Context = requireContext()
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("No se ha podido crear el arma")
                        builder.setPositiveButton("Aceptar", null)
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }

    }
    fun llegirDades(): Arma {
        //Guardem les dades introduïdes per l'usuari
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

        //Afegim els treballadors introduïts per l'usuari a l'atribut treballadors

        return Arma(nombre, descripcion, null, cargador, disparos, impacto, rango, estabilidad, recarga, aim, magazine, zoom, aire, recoil,null, donde)
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addArma.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            addArma().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}