package com.example.weather.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.database.City
import com.example.weather.database.CityRoomDatabase
import com.example.weather.databinding.MyLocationsAdapterBinding
import com.example.weather.utils.SharedPreferencesUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyLocationsAdapter(private val context: Context, private val switch: SwitchCompat): ListAdapter<City, MyLocationsAdapter.MyPlacesViewHolder>(DiffCallbackCity) {

    private var lastCheckedRadioButton: RadioButton? = null

        class MyPlacesViewHolder(binding: MyLocationsAdapterBinding): RecyclerView.ViewHolder(binding.root) {
            val radioButton = binding.radioBtnCityMyPlaces
            val imageButtonDelete = binding.imgDeleteMyPlaces
            val cityName = binding.txtCityMyPlaces
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlacesViewHolder {
        return MyPlacesViewHolder(MyLocationsAdapterBinding.inflate(LayoutInflater.from(parent.context)))
    }

    @DelicateCoroutinesApi
    override fun onBindViewHolder(holder: MyPlacesViewHolder, position: Int) {
        val city = getItem(position)
        setCityValue(city, holder)

        if(SharedPreferencesUtils.getFavouriteCityId(context)==city.id) {
            lastCheckedRadioButton = holder.radioButton
        }

        holder.radioButton.setOnClickListener {
            setRadioButtonChecked(holder.radioButton, city.id)
        }

        holder.cityName.setOnClickListener {
            setRadioButtonChecked(holder.radioButton, city.id)
        }

        holder.imageButtonDelete.setOnClickListener {
            GlobalScope.launch {
                CityRoomDatabase.getDatabase(context).cityDao().delete(city)
            }
            if(holder.radioButton.isChecked) {
                SharedPreferencesUtils.setFavouriteCityId(context,-1)
                switch.isChecked = true
            }
        }
    }

    fun setLastCheckedButtonToNull() {
        lastCheckedRadioButton=null
    }


    private fun setCityValue(city: City,holder: MyPlacesViewHolder) {
        holder.cityName.text = city.name
        holder.radioButton.isChecked = SharedPreferencesUtils.getFavouriteCityId(context)==city.id
    }




    companion object DiffCallbackCity : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }

    private fun setRadioButtonChecked(clickedRadioButton: RadioButton, cityId: Int) {
        if(clickedRadioButton!=lastCheckedRadioButton) {
            SharedPreferencesUtils.setFavouriteCityId(context,cityId)
            if(lastCheckedRadioButton != null) {
                lastCheckedRadioButton!!.isChecked = false
            }
            lastCheckedRadioButton = clickedRadioButton
            lastCheckedRadioButton!!.isChecked = true
            switch.isChecked = true
            switch.isChecked = false
        }
    }

}