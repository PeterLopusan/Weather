package com.example.weather.ui.fragments

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weather.R
import com.example.weather.database.CityApplication
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.service.NotificationService
import com.example.weather.service.NotificationServiceManager
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.viewModel.WeatherViewModel
import com.example.weather.viewModel.WeatherViewModelFactory


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory((activity?.application as CityApplication).database.cityDao(), Application())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setRadioGroups()
        val context = requireContext()

        binding.apply {
            radioBtnCelsium.setOnClickListener {
                SharedPreferencesUtils.setTemperatureUnit(context, SharedPreferencesUtils.defaultTemperatureUnit)
                viewModel.setCurrentCity(context)
            }

            radioBtnFahrenheit.setOnClickListener {
                SharedPreferencesUtils.setTemperatureUnit(context, SharedPreferencesUtils.changedTemperatureUnit)
                viewModel.setCurrentCity(context)
            }

            radioBtnKilometers.setOnClickListener {
                SharedPreferencesUtils.setSpeedUnit(context, SharedPreferencesUtils.defaultSpeedUnit)
            }

            radioBtnMiles.setOnClickListener {
                SharedPreferencesUtils.setSpeedUnit(context, SharedPreferencesUtils.changedSpeedUnit)
            }

            radioBtnNormal.setOnClickListener {
                SharedPreferencesUtils.setMapType(context, SharedPreferencesUtils.defaultMapType)
            }

            radioBtnSatellite.setOnClickListener {
                SharedPreferencesUtils.setMapType(context, SharedPreferencesUtils.changedMapType)
            }

            radioBtnPermanentNotificationOn.setOnClickListener {
                NotificationServiceManager.startService(requireContext())
            }

            radioBtnPermanentNotificationOff.setOnClickListener {
                NotificationServiceManager.stopService(requireContext())
            }

            btnLanguageSelector.setOnClickListener {
                showLanguageSelectDialog()
            }
        }

        return binding.root
    }


    private fun setRadioGroups() {
        if (SharedPreferencesUtils.getTemperatureUnit(requireContext()) == SharedPreferencesUtils.defaultTemperatureUnit) {
            binding.radioBtnCelsium.isChecked = true
        } else {
            binding.radioBtnFahrenheit.isChecked = true
        }

        if (SharedPreferencesUtils.getSpeedUnit(requireContext()) == SharedPreferencesUtils.defaultSpeedUnit) {
            binding.radioBtnKilometers.isChecked = true
        } else {
            binding.radioBtnMiles.isChecked = true
        }

        if (SharedPreferencesUtils.getMapType(requireContext()) == SharedPreferencesUtils.defaultMapType) {
            binding.radioBtnNormal.isChecked = true
        } else {
            binding.radioBtnSatellite.isChecked = true
        }

        if (NotificationService.getIsRunning()) {
            binding.radioBtnPermanentNotificationOn.isChecked = true
        } else {
            binding.radioBtnPermanentNotificationOff.isChecked = true
        }
    }

    private fun showLanguageSelectDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val context = requireContext()
        val options: Array<String> = arrayOf(context.getString(R.string.english), context.getString(R.string.german), context.getString(R.string.czech), context.getString(R.string.slovak))
        dialog.apply {
            setCancelable(true)
            setItems(options) { _, which ->
                when (which) {
                    0 -> setLanguage("en")
                    1 -> setLanguage("de")
                    2 -> setLanguage("cs")
                    3 -> setLanguage("sk")
                }
            }
            show()
        }
    }

    private fun setLanguage(language: String) {
        SharedPreferencesUtils.setLocalization(requireContext(), language)
        activity?.recreate()
    }
}
