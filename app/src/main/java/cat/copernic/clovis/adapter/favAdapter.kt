package cat.copernic.clovis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.clovis.Fragment.FavoritosDirections
import cat.copernic.clovis.Fragment.SeleccionarArmaDirections
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.data.dataFav
import cat.copernic.clovis.databinding.ItemSeleccionarArmaBinding

class favAdapter(private val FavList:List<dataFav>) :
    RecyclerView.Adapter<favAdapter.favholder>(){
    inner class favholder(val binding: ItemSeleccionarArmaBinding): RecyclerView.ViewHolder(binding.root)
    private var binding: ItemSeleccionarArmaBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): favAdapter.favholder {
        val binding= ItemSeleccionarArmaBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return favholder(binding)
    }

    override fun onBindViewHolder(holder: favAdapter.favholder, position: Int) {
        with(holder) {
            with(FavList[position]) {
                binding.nameWeapon.text= this.nombre
                binding.imgWeapon.setImageResource(this.imageResourceId)
                binding.servId.text=this.id
            }
            binding.cardRutas.setOnClickListener { view ->
                val document = binding.nameWeapon.text.toString()
                val directions = FavoritosDirections.actionFavoritosToInfoObjects(document)
                view.findNavController().navigate(directions)
            }
        }
    }

    override fun getItemCount(): Int = FavList.size
}