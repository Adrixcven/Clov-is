package cat.copernic.clovis.Activity

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import cat.copernic.clovis.R
import cat.copernic.clovis.databinding.ActivityMainBinding
import cat.copernic.clovis.databinding.ActivityMainUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity_Users : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainUsersBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var customview: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el diseño de la actividad principal
        binding = ActivityMainUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la autenticación de Firebase
        auth = Firebase.auth
        // Obtener el Fragmento del Host de navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(cat.copernic.clovis.R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        // Configurar el menú de la barra de herramientas lateral
        appBarConfiguration = AppBarConfiguration(
            setOf(cat.copernic.clovis.R.id.seleccionarArma, cat.copernic.clovis.R.id.verUsuario, cat.copernic.clovis.R.id.ajustes, cat.copernic.clovis.R.id.administradorArmas, cat.copernic.clovis.R.id.addArma, cat.copernic.clovis.R.id.favoritos),
            binding.drawerLayout
        )
        // Configurar la vista personalizada de la barra de acción
        val params = ActionBar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT, // Ancho ajustado al tamaño de la pantalla
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayShowCustomEnabled(true)
        customview = TextView(this)
        customview.setTextColor(Color.WHITE)
        customview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        customview.layoutParams = ActionBar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        val customActionBarView = LinearLayout(this)
        customActionBarView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        customActionBarView.orientation = LinearLayout.HORIZONTAL
        actionBar?.customView = customview
        actionBar?.setCustomView(null)
        actionBar?.setCustomView(customActionBarView)


        // Configurar la acción de la barra de acción con el controlador de navegación
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Establecer la acción del elemento del menú lateral seleccionado
        binding.navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                cat.copernic.clovis.R.id.Inicio ->  {
                    // Cerrar el menú lateral y navegar al fragmento de selección de armas
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.seleccionarArma)
                }
                cat.copernic.clovis.R.id.Cuenta -> {
                    // Cerrar el menú lateral y navegar al fragmento de ver usuario
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.verUsuario)
                }
                cat.copernic.clovis.R.id.Favoritos -> {
                    // Cerrar el menú lateral y navegar al fragmento de favoritos
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.favoritos)
                }
                cat.copernic.clovis.R.id.Ajustes -> {
                    // Cerrar el menú lateral y navegar al fragmento de Ajustes
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.ajustes)
                }
                cat.copernic.clovis.R.id.Añadir_Arma -> {
                    // Cerrar el menú lateral y navegar al fragmento de Añadir arma
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.addArma)
                }
                cat.copernic.clovis.R.id.administrararmas -> {
                    // Cerrar el menú lateral y navegar al fragmento de Administrador
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.administradorArmas)
                }
                cat.copernic.clovis.R.id.CerrarSesion -> {
                    // Cerrar sesión y volver a la actividad de Login
                    auth.signOut()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {

                }
            }
            true
        }
    }
    /**

     * Metodo que se llama cuando se presiona el botón de navegación hacia arriba en la barra de acción.
     * @return true si la navegación se manejó correctamente, de lo contrario, false.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
    /**

     * Actualiza el título de la ActionBar con el texto proporcionado
     * @param title el título a establecer en la ActionBar
     */
    fun updateActionBarTitle(title: String?) {
        // Establece el texto del título en el CustomView de la ActionBar
        customview.text=title
        // Establece el CustomView actualizado en la ActionBar
        supportActionBar?.customView = customview
    }
}