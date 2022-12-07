package com.gym.gymapp.ui.productListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemFilterBinding
import com.gym.gymapp.databinding.ItemFilterSelectionBinding
import com.gym.gymapp.ui.productListing.model.FilterCategoryData
import javax.inject.Inject


class FilterCategoryAdapter @Inject constructor():RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFilterSelectionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data:FilterCategoryData){
            with(binding){
                filterCheckBox.text = data.category_name

                filterCheckBox.isChecked = data.checked

                filterCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                    data.checked = b
                    if(b){
                        filterSelectListener?.let {
                            it(data.id,true)
                        }
                    }else{
                        filterSelectListener?.let {
                            it(data.id,false)
                        }
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<FilterCategoryData>(){
        override fun areItemsTheSame(
            oldItem: FilterCategoryData,
            newItem: FilterCategoryData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FilterCategoryData,
            newItem: FilterCategoryData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this,differCallBack)
    private var filterSelectListener: ((position: Int,status:Boolean) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterCategoryAdapter.ViewHolder {
        return ViewHolder(
            ItemFilterSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: FilterCategoryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size

    fun setOnFilterSelectListener(listener: (position: Int,status:Boolean) -> Unit) {
        filterSelectListener = listener
    }
}