package cat.copernic.clovis.datalist

import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataArma
import cat.copernic.clovis.data.dataFav

class FavList {
    companion object{
        val favoritos = listOf<dataFav>(
            dataFav("Ejemplo1", R.drawable.ejemplo, "1"),
            dataFav("Ejemplo3", R.drawable.ejemplo, "3"),
            dataFav("Ejemplo5", R.drawable.ejemplo, "5"),

            )
    }
}