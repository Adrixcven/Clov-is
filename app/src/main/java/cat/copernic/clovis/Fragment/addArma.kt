package cat.copernic.clovis.Fragment

import android.app.Activity
import android.content.Intent
import android.content.Intent.CATEGORY_APP_GALLERY
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.FragmentAddArmaBinding
import cat.copernic.clovis.databinding.FragmentAdministradorArmasBinding
import cat.copernic.clovis.databinding.FragmentEditarWeaponBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addArma.newInstance] factory method to
 * create an instance of this fragment.
 */
class addArma : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAddArmaBinding
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

        binding.imgObject.setOnClickListener {
            hacerfotoObject.launch(Intent(CATEGORY_APP_GALLERY))
        }
        binding.imgPerks.setOnClickListener {
            hacerfotoperk.launch(Intent(CATEGORY_APP_GALLERY))
        }
        binding.flechaAtras.setOnClickListener{
            view.findNavController().navigate(R.id.action_addArma_to_seleccionarArma)
        }
        binding.Guardar.setOnClickListener {
            view.findNavController().navigate(R.id.action_addArma_to_seleccionarArma)
        }
    }
    private val hacerfotoObject = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            binding.imgObject.setImageBitmap(imageBitmap)
        }
    }
    private val hacerfotoperk = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            binding.imgObject.setImageBitmap(imageBitmap)
        }
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