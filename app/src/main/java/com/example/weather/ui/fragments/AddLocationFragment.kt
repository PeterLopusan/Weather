package com.example.weather.ui.fragments

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weather.LATITUDE
import com.example.weather.LONGITUDE
import com.example.weather.R
import com.example.weather.database.City
import com.example.weather.database.CityApplication
import com.example.weather.databinding.FragmentAddLocationBinding
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

private const val zoomLevel = 10f
private const val requestLocationPermission = 1

class AddPlaceFragment : Fragment() {
    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var setFromMap = false

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory((activity?.application as CityApplication).database.cityDao(), Application())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_add_places) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.apply {
            editCity.addTextChangedListener {
                if (!setFromMap) {
                    setValuesFromName(editCity.text.toString())
                } else {
                    setFromMap = false
                }
            }
            btnConfirm.setOnClickListener { addCity() }
        }
    }

    private fun addCity() {
        if (checkValues()) {
            val city = City(
                name = binding.editCity.text.toString(),
                country = binding.txtCountry.text.toString(),
                latitude = binding.txtLatitude.text.toString(),
                longitude = binding.txtLongitude.text.toString()
            )
            viewModel.insertCity(city)
            binding.apply {
                editCity.text.clear()
                txtCountry.text = ""
                txtLatitude.text = ""
                txtLongitude.text = ""
            }
            Toast.makeText(requireContext(), getString(R.string.successfully_save), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.unknown_location), Toast.LENGTH_LONG).show()
        }
    }

    private fun checkValues(): Boolean {
        if (binding.txtLatitude.text != "" && binding.txtLongitude.text != "" && binding.editCity.text.toString() != "") {
            return true
        }
        return false
    }

    private fun setValuesFromName(cityName: String) {
        if (cityName != "") {
            val geocoder = Geocoder(requireContext())
            val address = geocoder.getFromLocationName(cityName, 1)

            if (address.isNotEmpty()) {
                val latLong = LatLng(address[0].latitude, address[0].longitude)
                map.apply {
                    clear()
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoomLevel))
                    addMarker(MarkerOptions().position(latLong))
                }

                binding.apply {
                    txtCountry.text = address[0].countryName
                    txtLatitude.text = address[0].latitude.toString()
                    txtLongitude.text = address[0].longitude.toString()
                }

            } else {
                binding.apply {
                    txtCountry.text = ""
                    txtLatitude.text = ""
                    txtLongitude.text = ""
                }
            }
        }
    }


    private fun setValuesFromMap(latLng: LatLng) {
        val geocoder = Geocoder(requireContext())
        val address: List<Address> = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10).filter { it.locality != null }

        if (address.isNotEmpty()) {
            if (address[0].locality != null) {
                setFromMap = true
                map.apply {
                    clear()
                    addMarker(MarkerOptions().position(latLng))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
                }

                binding.apply {
                    editCity.text = Editable.Factory.getInstance().newEditable(address[0].locality)
                    txtCountry.text = address[0].countryName
                    txtLatitude.text = address[0].latitude.toString()
                    txtLongitude.text = address[0].longitude.toString()
                }
            }
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                map.isMyLocationEnabled = true
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    setValuesFromMap(latLng)
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermission)
        }
    }

    override fun onResume() {
        super.onResume()
        enableMyLocation()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val latitude = LATITUDE.toDouble()
        val longitude = LONGITUDE.toDouble()
        val homeLatLng = LatLng(latitude, longitude)
        map = googleMap

        map.apply {
            setMap(this, requireContext())
            uiSettings.isZoomControlsEnabled = true
            moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
            setOnMapClickListener { latLng -> setValuesFromMap(latLng) }
        }
    }
}