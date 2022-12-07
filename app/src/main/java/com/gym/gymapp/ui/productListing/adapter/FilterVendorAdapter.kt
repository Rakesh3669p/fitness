package com.gym.gymapp.ui.productListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemFilterSelectionBinding
import com.gym.gymapp.ui.productListing.model.FilterVendorData
import javax.inject.Inject

class FilterVendorAdapter @Inject constructor(): RecyclerView.Adapter<FilterVendorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFilterSelectionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: FilterVendorData){
            with(binding){
                filterCheckBox.text = data.name

                filterCheckBox.setOnCheckedChangeListener { _, b ->
                    if(b){
                        filterSelectListener?.let {
                            it(data.id)
                        }
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<FilterVendorData>(){
        override fun areItemsTheSame(
            oldItem: FilterVendorData,
            newItem: FilterVendorData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FilterVendorData,
            newItem: FilterVendorData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this,differCallBack)
    private var filterSelectListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterVendorAdapter.ViewHolder {
        return ViewHolder(
            ItemFilterSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: FilterVendorAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size

    fun setOnFilterSelectListener(listener: (position: Int) -> Unit) {
        filterSelectListener = listener
    }
}