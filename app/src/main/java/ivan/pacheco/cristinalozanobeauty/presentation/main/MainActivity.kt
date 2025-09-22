package ivan.pacheco.cristinalozanobeauty.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.ActivityMainBinding
import ivan.pacheco.cristinalozanobeauty.presentation.client.detail.ClientDetailFragment
import ivan.pacheco.cristinalozanobeauty.presentation.client.form.ClientFormFragment
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load toolbar
        loadToolbar()

        // Navigate back or close app if are on any of home screens
        onBackPressedDispatcher.addCallback(this) {
            if (!navController.popBackStack()) finish()
        }

        // Load bottom navigation
        loadBottomNavBar()

        // Customize status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.gold)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun showLoading() {
        binding.progressOverlay.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.progressOverlay.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun loadToolbar() {

        // Set custom toolbar as ActionBar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Link ActionBar with Navigation
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.clientListFragment,
            R.id.messageFragment
        )

        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Set fragments label as toolbar title
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            if (destination.id == R.id.homeFragment) {
                toolbar.title = getString(R.string.app_name)
            } else {
                toolbar.title = destination.label
            }

            // Hide back button on main menu screens
            if (appBarConfiguration.topLevelDestinations.contains(destination.id)) {
                toolbar.setNavigationIcon(null);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_navigation_back);
            }
        }

        // Show dialog for save changes before navigate back
        toolbar.setNavigationOnClickListener {
            val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            if (currentFragment is ClientFormFragment) {
                currentFragment.showBackPressDialog()
            } else if (currentFragment is ClientDetailFragment) {
                currentFragment.saveChangesDialog(Destination.Back)
            } else {
                if (!navController.popBackStack()) finish()
            }
        }
    }

    private fun loadBottomNavBar() {

        // Clear backstack and navigate
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            // Intercept click on BottomNavigationView
            if (navController.currentDestination?.id != item.itemId) {
                navController.navigate(
                    item.itemId,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestinationId, true)
                        .setLaunchSingleTop(true)
                        .build()
                )
            }
            true
        }
    }

}