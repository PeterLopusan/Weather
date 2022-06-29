package com.example.weather.ui.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.database.CityApplication
import com.example.weather.databinding.FragmentMyLocationBinding
import com.example.weather.ui.adapters.MyLocationsAdapter
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.viewModel.WeatherViewModel
import com.example.weather.viewModel.WeatherViewModelFactory

class MyLocationsFragment : Fragment() {
    private var _binding: FragmentMyLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory((activity?.application as CityApplication).database.cityDao(), Application())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyLocationBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchMyLocation.isChecked = SharedPreferencesUtils.getFavouriteCityId(requireContext()) == -1

        val adapter = MyLocationsAdapter(requireContext(), binding.switchMyLocation)
        recyclerView = binding.recyclerViewMyPlaces
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = adapter

        binding.switchMyLocation.setOnClickListener {
            if (binding.switchMyLocation.isChecked) {
                SharedPreferencesUtils.setFavouriteCityId(requireContext(), -1)
                adapter.setLastCheckedButtonToNull()
                adapter.notifyDataSetChanged()
                viewModel.setCurrentCity(requireContext())
            } else {
                binding.switchMyLocation.isChecked = true
            }
        }

        binding.fabMyPlaces.setOnClickListener {
            view.findNavController().navigate(MyLocationsFragmentDirections.actionNavMyPlacesToAddPlaceFragment())
        }

        binding.switchMyLocation.setOnCheckedChangeListener { _, _ ->
            viewModel.setCurrentCity(requireContext())
        }

        viewModel.listOfCities.observe(viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}