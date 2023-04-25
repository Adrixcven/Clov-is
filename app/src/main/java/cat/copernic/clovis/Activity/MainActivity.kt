package cat.copernic.clovis.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
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
}