package cat.copernic.clovis.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.clovis.Fragment.AdministradorArmasDirections
import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.data.dataFav
import cat.copernic.clovis.databinding.ItemSeleccionarAdminBinding
import cat.copernic.clovis.datalist.AdminList.Companion.admin
import cat.copernic.clovis.datalist.ArmasList
import cat.copernic.clovis.datalist.ArmasList.Companion.armas
import cat.copernic.clovis.datalist.FavList
import cat.copernic.clovis.datalist.FavList.Companion.favoritos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File


class adminAdapter (private val Adminlist:List<dataAdmin>, private val ArmasList:List<dataArma>, private val FavList:List<dataFav>) :
    RecyclerView.Adapter<adminAdapter.adminholder>(){
        inner class adminholder(val binding: ItemSeleccionarAdminBinding): RecyclerView.ViewHolder(binding.root)
        private var binding: ItemSeleccionarAdminBinding? = null
        private var bd = FirebaseFirestore.getInstance()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adminAdapter.adminholder {
            val binding= ItemSeleccionarAdminBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return adminholder(binding)
        }


        override fun onBindViewHolder(holder: adminAdapter.adminholder, position: Int) {
            with(holder) {
                with(Adminlist[position]) {
                    binding.nameWeapon.text = this.nombre
                    binding.imgWeapon.setImageBitmap(this.imageResourceId)
                    binding.imgEdit.setImageResource(this.editar)
                    binding.imgTrash.setImageResource(this.borrar)
                    binding.servId.text = this.id
                }
                binding.cardRutas.setOnClickListener { view ->
                    val id = binding.nameWeapon.text.toString()
                    val directions =
                        AdministradorArmasDirections.actionAdministradorArmasToInfoObjects(id)
                    view.findNavController().navigate(directions)

                }
                binding.imgEdit.setOnClickListener { view ->
                    val id = binding.servId.text.toString()

                    val directions =
                        AdministradorArmasDirections.actionAdministradorArmasToEditarArma(id)
                    view.findNavController().navigate(directions)
                }
                binding.imgTrash.setOnClickListener { view ->
                    val storageRef = FirebaseStorage.getInstance().reference
                    val id = binding.servId.text.toString()
                    val imageRefLittle = storageRef.child("image/Little/$id"+"Little.jpeg")
                    val imageRefPerk = storageRef.child("image/Perks/$id"+"perk.jpeg")
                    val imageRef = storageRef.child("image/Armas/$id"+"Image.jpeg")
                    imageRef.delete()
                    imageRefPerk.delete()
                    imageRefLittle.delete()
                    for (i in Adminlist.indices) {
                        val dataArma = admin[i]
                        if (dataArma.id == id) {
                            // Eliminar el objeto dataArma del array
                            admin.removeAt(i)
                            break
                        }
                    }
                    for (i in ArmasList.indices) {
                        val dataArma = armas[i]
                        if (dataArma.id == id) {
                            // Eliminar el objeto dataArma del array
                            armas.removeAt(i)
                            break
                        }
                    }
                    for (i in FavList.indices) {
                        val dataArma = favoritos[i]
                        if (dataArma.id == id) {
                            // Eliminar el objeto dataArma del array
                            favoritos.removeAt(i)
                            break
                        }
                    }
                    bd.collection("Armas").document(id)
                        .delete()
                    view.findNavController().navigate(R.id.action_administradorArmas_self)
                }
            }
        }


        override fun getItemCount(): Int = Adminlist.size
}