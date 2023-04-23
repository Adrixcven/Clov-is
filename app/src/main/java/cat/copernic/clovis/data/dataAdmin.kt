package cat.copernic.clovis.data

import android.graphics.Bitmap

data class dataAdmin(
    val imageResourceId: Bitmap?,
    val nombre: String,
    val editar: Int,
    val borrar: Int,
    val id: String
)
