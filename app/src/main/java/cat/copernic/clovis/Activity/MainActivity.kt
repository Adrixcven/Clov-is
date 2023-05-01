package cat.copernic.clovis.Activity


import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import cat.copernic.clovis.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var mytoolbar: Toolbar
    private lateinit var customview: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val navHostFragment = supportFragmentManager
            .findFragmentById(cat.copernic.clovis.R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        //menú lateral
        appBarConfiguration = AppBarConfiguration(
            setOf(cat.copernic.clovis.R.id.seleccionarArma, cat.copernic.clovis.R.id.verUsuario, cat.copernic.clovis.R.id.ajustes, cat.copernic.clovis.R.id.administradorArmas, cat.copernic.clovis.R.id.addArma, cat.copernic.clovis.R.id.favoritos),
            binding.drawerLayout
        )

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



        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                cat.copernic.clovis.R.id.Inicio ->  {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.seleccionarArma)
                }
                cat.copernic.clovis.R.id.Cuenta -> {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.verUsuario)
                }
                cat.copernic.clovis.R.id.Favoritos -> {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.favoritos)
                }
                cat.copernic.clovis.R.id.Ajustes -> {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.ajustes)
                }
                cat.copernic.clovis.R.id.Añadir_Arma -> {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.addArma)
                }
                cat.copernic.clovis.R.id.administrararmas -> {
                    val drawerLayout = binding.drawerLayout
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(cat.copernic.clovis.R.id.administradorArmas)
                }
                cat.copernic.clovis.R.id.CerrarSesion -> {
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
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
    fun updateActionBarTitle(title: String?) {
        customview.text=title
        supportActionBar?.customView = customview
    }
}