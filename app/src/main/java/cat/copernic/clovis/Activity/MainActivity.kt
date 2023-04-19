package cat.copernic.clovis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cat.copernic.clovis.Fragment.SeleccionarArma
import cat.copernic.clovis.Fragment.ajustes
import cat.copernic.clovis.Fragment.verUsuario
import cat.copernic.clovis.R
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
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        setupActionBarWithNavController(navController)
        //menú lateral
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.seleccionarArma, R.id.verUsuario, R.id.ajustes, R.id.administradorArmas),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.Inicio ->  navController.navigate(R.id.seleccionarArma)
                R.id.Cuenta -> navController.navigate(R.id.verUsuario)
                R.id.Ajustes -> navController.navigate(R.id.ajustes)
                R.id.Añadir_Arma -> navController.navigate(R.id.addArma)
                R.id.administrararmas -> navController.navigate(R.id.administradorArmas)
                R.id.CerrarSesion -> {
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