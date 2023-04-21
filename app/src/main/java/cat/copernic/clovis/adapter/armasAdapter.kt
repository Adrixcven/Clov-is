package cat.copernic.clovis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.clovis.Fragment.AdministradorArmasDirections
import cat.copernic.clovis.Fragment.SeleccionarArmaDirections
import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.databinding.ItemSeleccionarArmaBinding

class armasAdapter(private val Armalist:List<dataArma>) :
    RecyclerView.Adapter<armasAdapter.armasholder>(){
    inner class armasholder(val binding: ItemSeleccionarArmaBinding):RecyclerView.ViewHolder(binding.root)
    private var binding: ItemSeleccionarArmaBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): armasAdapter.armasholder {
        val binding= ItemSeleccionarArmaBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return armasholder(binding)
    }

    override fun onBindViewHolder(holder: armasholder, position: Int) {
        with(holder) {
            with(Armalist[position]) {
                binding.nameWeapon.text= this.nombre
                binding.imgWeapon.setImageResource(this.imageResourceId)
                binding.servId.text=this.id
            }
            binding.cardRutas.setOnClickListener { view ->
                val document = binding.nameWeapon.text.toString()
                val directions = SeleccionarArmaDirections.actionSeleccionarArmaToInfoObjects(document)
                view.findNavController().navigate(directions)
            }
        }
    }

    override fun getItemCount(): Int = Armalist.size


}