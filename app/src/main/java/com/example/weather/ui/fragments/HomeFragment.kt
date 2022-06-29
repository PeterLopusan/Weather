package com.example.weather.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.weather.R
import com.example.weather.database.CityApplication
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.ui.adapters.ForecastAdapter
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.utils.setMap
import com.example.weather.viewModel.WeatherViewModel
import com.example.weather.viewModel.WeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val requestLocationPermission = 1

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory((activity?.application as CityApplication).database.cityDao(), activity?.application as CityApplication)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfSetFromLocation()

        viewModel.weather.observe(
            viewLifecycleOwner
        ) {
            binding.componentCurrentWeatherView.setValues(viewModel.weather.value!!, viewModel.currentCity.name, viewModel.currentCity.country)
            setRecycler()
        }

        viewModel.errorMessage.observe(
            viewLifecycleOwner
        ) {
            if (viewModel.errorMessage.value != "") {
                showErrorMessage()
            }
        }

        viewModel.listOfCities.observe(
            viewLifecycleOwner
        ) {
            viewModel.setCurrentCity(requireContext())
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.swipeRefresh.setOnRefreshListener {
            view.findNavController().navigate(HomeFragmentDirections.actionNavHomeSelf())
        }
    }

    private fun setRecycler() {
        val recyclerAdapter = ForecastAdapter(requireContext())
        binding.recyclerForecast.adapter = recyclerAdapter
        var recyclerData = viewModel.weather.value!!.forecast.forecastday
        recyclerData = recyclerData.filter { it != recyclerData.first() }
        recyclerAdapter.submitList(recyclerData)
    }

    private fun showErrorMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(viewModel.errorMessage.value)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                activity?.finish()
            }
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                viewModel.errorMessage.value = ""
                activity?.recreate()
            }
            .show()
    }

    private fun checkIfSetFromLocation() {
        if (SharedPreferencesUtils.getFavouriteCityId(requireContext()) == -1) {
            setWeatherFromLocation()
        }
    }


    private fun setWeatherFromLocation() {
        val context = requireContext()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { it ->
                    val latitude = it.latitude
                    val longitude = it.longitude
                    val geocoder = Geocoder(context)
                    val address: List<Address> = geocoder.getFromLocation(latitude, longitude, 10).filter { it.locality != null }

                    if (address.isNotEmpty() && address[0].locality != null) {
                        val city = address[0].locality
                        val country = address[0].countryName

                        SharedPreferencesUtils.apply {
                            setLastCity(context, city)
                            setLastCountry(context, country)
                            setLastLatitude(context, latitude.toString())
                            setLastLongitude(context, longitude.toString())
                        }
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermission)
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.apply {
            setMap(this, requireContext())
            clear()
            uiSettings.isScrollGesturesEnabled = false
            uiSettings.isZoomControlsEnabled = true
            val latitude = viewModel.currentCity.latitude.toDouble()
            val longitude = viewModel.currentCity.longitude.toDouble()
            val homeLatLng = LatLng(latitude, longitude)
            val zoomLevel = 11f
            moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
            addMarker(MarkerOptions().position(homeLatLng))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


