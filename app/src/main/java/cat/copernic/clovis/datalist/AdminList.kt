package cat.copernic.clovis.datalist

import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataAdmin
import cat.copernic.clovis.data.dataArma

class AdminList {
    companion object{
        val admin = listOf<dataAdmin>(
            dataAdmin(R.drawable.ejemplo, "Ejemplo1", R.drawable.edit, R.drawable.trash, "1"),
            dataAdmin(R.drawable.ejemplo, "Ejemplo2", R.drawable.edit, R.drawable.trash, "2"),
            dataAdmin(R.drawable.ejemplo, "Ejemplo3", R.drawable.edit, R.drawable.trash, "3"),
        )
    }
}