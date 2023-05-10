package cat.copernic.clovis.Models

import cat.copernic.clovis.Fragment.Favoritos

data class Arma(
val nombre: String,
val descripcion: String,
val imgarma: Int?,
val cargador: Int,
val disparos: Int,
val impacto: Int,
val rango: Int,
val estabilidad: Int,
val recarga: Int,
val aim: Int,
val inventario: Int,
val zoom: Int,
val aire: Int,
val recoil: Int,
val imgperks:Int?,
val donde: String)
