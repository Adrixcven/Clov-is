package cat.copernic.clovis.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.FragmentAdministradorArmasBinding
import cat.copernic.clovis.databinding.FragmentEditarWeaponBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var nom = args.id
        binding.editarNombreArma.setText(nom)

        binding.imgObject.setOnClickListener {
            intentimgweapon.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.imgPerks.setOnClickListener {
            intentimgperk.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
        binding.Guardar.setOnClickListener {
            view.findNavController().navigate(R.id.action_editarArma_to_administradorArmas)
        }
    }
    private var storage = FirebaseStorage.getInstance()
    private var storageRefarmas  = storage.getReference().child("image/Armas").child("a.jpeg")
    private var storageRefPerks  = storage.getReference().child("image/Perks").child("a.jpeg")
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
                    if (binding.editarNombreArma.text.toString() != ""){
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
                    Toast.makeText(requireContext(), "File not found.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Image not found.", Toast.LENGTH_LONG).show()
            }
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