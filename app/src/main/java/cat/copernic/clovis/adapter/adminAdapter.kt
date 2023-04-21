package cat.copernic.clovis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.clovis.Fragment.AdministradorArmas
import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.databinding.ItemSeleccionarAdminBinding
import cat.copernic.clovis.databinding.ItemSeleccionarArmaBinding
import cat.copernic.clovis.Fragment.AdministradorArmasDirections

class adminAdapter (private val Adminlist:List<dataAdmin>) :
    RecyclerView.Adapter<adminAdapter.adminholder>(){
        inner class adminholder(val binding: ItemSeleccionarAdminBinding): RecyclerView.ViewHolder(binding.root)
        private var binding: ItemSeleccionarAdminBinding? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adminAdapter.adminholder {
            val binding= ItemSeleccionarAdminBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return adminholder(binding)
        }

        override fun onBindViewHolder(holder: adminAdapter.adminholder, position: Int) {
            with(holder) {
                with(Adminlist[position]) {
                    binding.nameWeapon.text= this.nombre
                    binding.imgWeapon.setImageResource(this.imageResourceId)
                    binding.imgEdit.setImageResource(this.editar)
                    binding.imgTrash.setImageResource(this.borrar)
                    binding.servId.text=this.id
                }
                binding.cardRutas.setOnClickListener{ view ->
                    val id = binding.nameWeapon.text.toString()
                    val directions = AdministradorArmasDirections.actionAdministradorArmasToInfoObjects(id)
                    view.findNavController().navigate(directions)

                }
                binding.imgEdit.setOnClickListener { view ->
                    val id = binding.nameWeapon.text.toString()
                    val directions = AdministradorArmasDirections.actionAdministradorArmasToEditarArma(id)
                    view.findNavController().navigate(directions)
                }
            }
        }

        override fun getItemCount(): Int = Adminlist.size
}