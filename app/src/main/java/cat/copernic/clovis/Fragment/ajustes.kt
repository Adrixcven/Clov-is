package cat.copernic.clovis.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import cat.copernic.clovis.Activity.MainActivity
import cat.copernic.clovis.Activity.MainActivity_Users
import cat.copernic.clovis.databinding.FragmentAjustesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ajustes.newInstance] factory method to
 * create an instance of this fragment.
 */
class ajustes : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAjustesBinding
    /**

    * Método que se llama cuando se crea la actividad de Ajustes.

    * @param savedInstanceState Guarda el estado de la actividad en caso de que se destruya y vuelva a crearse.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Actualiza el título de la Action Bar a "Ajustes".
        if(getActivity() is MainActivity){
            (activity as MainActivity?)!!.updateActionBarTitle("Ajustes")
        }else if(activity is MainActivity_Users){
            (activity as MainActivity_Users?)!!.updateActionBarTitle("Ajustes")
        }
        // Si existen argumentos, los recupera.
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
        binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ajustes.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ajustes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}