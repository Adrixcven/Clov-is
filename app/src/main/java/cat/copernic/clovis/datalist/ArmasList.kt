package cat.copernic.clovis.datalist

import android.provider.SyncStateContract.Helpers.insert
import cat.copernic.clovis.R
import cat.copernic.clovis.data.dataArma

class ArmasList {
    companion object{
        val armas = listOf<dataArma>(
                dataArma("Ejemplo1", R.drawable.ejemplo, "1"),
                dataArma("Ejemplo2", R.drawable.ejemplo, "2"),
                dataArma("Ejemplo3", R.drawable.ejemplo, "3"),
                dataArma("Ejemplo4", R.drawable.ejemplo, "4"),

            )
    }

}