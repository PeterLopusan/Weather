package com.example.weather.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.service.NotificationServiceManager
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.utils.setImage
import com.example.weather.viewModel.WeatherViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocalization()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        viewModel.setCurrentCity(this)
        viewModel.weather.observe(this) {
            setNavHeader()
            NotificationServiceManager.resetService(this)
        }

        viewModel.errorMessage.observe(this) {
            if (viewModel.errorMessage.value != "") {
                showErrorMessage()
            }
        }


        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_my_places, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val weather = viewModel.weather.value?.current

        val city = headerView.findViewById<TextView>(R.id.txt_city_nav_header)
        val temperature = headerView.findViewById<TextView>(R.id.txt_temperature_nav_header)
        val descriptionWeather = headerView.findViewById<TextView>(R.id.txt_weather_description_nav_header)
        val weatherIcon = headerView.findViewById<ImageView>(R.id.img_weather_nav_header)

        setImage(weather?.condition?.icon, weatherIcon)
        city.text = viewModel.currentCity.name


        if (SharedPreferencesUtils.getTemperatureUnit(this) == SharedPreferencesUtils.defaultTemperatureUnit) {
            temperature.text = getString(R.string.temperature_value_celsius, weather?.temperatureCelsius?.toDouble()?.toInt())
        } else {
            temperature.text = getString(R.string.temperature_value_fahrenheit, weather?.temperatureFahrenheit?.toDouble()?.toInt())
        }
        descriptionWeather.text = weather?.condition?.text
    }

    private fun showErrorMessage() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.error))
            .setMessage(viewModel.errorMessage.value)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                this.finish()
            }
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                viewModel.errorMessage.value = ""
                this.recreate()
            }
            .show()
    }

    private fun setLocalization() {
        val language = SharedPreferencesUtils.getLocalization(this)
        val locale = Locale(language)
        val application = this.application
        val applicationResources: Resources = application.resources
        val activityResources: Resources = this.resources
        Locale.setDefault(locale)
        setResources(applicationResources, locale)
        setResources(activityResources, locale)
    }

    private fun setResources(resources: Resources, locale: Locale) {
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, displayMetrics)
    }

}