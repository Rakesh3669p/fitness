package com.gym.gymapp.ui.organisationsList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemCitiesBinding
import com.gym.gymapp.databinding.ItemOrganisationList2Binding
import com.gym.gymapp.ui.organisationsList.model.FilterCitiesData
import com.gym.gymapp.ui.organisationsList.model.OrganisationData
import javax.inject.Inject

class CitiesFilterAdapter @Inject constructor() : RecyclerView.Adapter<CitiesFilterAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemCitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FilterCitiesData) {
            with(binding) {
                Glide.with(context!!).load(data.image).into(cityImage)
                cityName.text= data.name

                root.setOnClickListener {
                    cityClickListener?.let {
                        it(data)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<FilterCitiesData>() {
        override fun areItemsTheSame(
            oldItem: FilterCitiesData,
            newItem: FilterCitiesData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FilterCitiesData,
            newItem: FilterCitiesData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var cityClickListener: ((data: FilterCitiesData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitiesFilterAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemCitiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CitiesFilterAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setOnCityClickListener(listener: (data: FilterCitiesData) -> Unit) {
        cityClickListener = listener
    }
}