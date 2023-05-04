package cat.copernic.clovis.Models

import cat.copernic.clovis.Fragment.Favoritos
import com.google.firebase.firestore.Exclude

data class Usuario(@get:Exclude val nombre: String?,
val id: String,
val email: String,
val descripcion: String?,
val admin: Boolean,
val clase: String?,
val Favoritos:ArrayList<Favoritos>?
)
