package com.gym.gymapp.ui.productListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemFilterBinding
import com.gym.gymapp.databinding.ItemFilterSelectionBinding
import com.gym.gymapp.ui.productListing.model.FilterCategoryData
import com.gym.gymapp.ui.productListing.model.FilterSubCategoryData
import javax.inject.Inject


class FilterSubCategoryAdapter @Inject constructor():RecyclerView.Adapter<FilterSubCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFilterSelectionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data:FilterSubCategoryData){
            with(binding){
                filterCheckBox.text = data.name

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

    private val differCallBack = object : DiffUtil.ItemCallback<FilterSubCategoryData>(){
        override fun areItemsTheSame(
            oldItem: FilterSubCategoryData,
            newItem: FilterSubCategoryData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FilterSubCategoryData,
            newItem: FilterSubCategoryData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this,differCallBack)
    private var filterSelectListener: ((position: Int,status:Boolean) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterSubCategoryAdapter.ViewHolder {
        return ViewHolder(
            ItemFilterSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: FilterSubCategoryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size

    fun setOnFilterSelectListener(listener: (position: Int,status:Boolean) -> Unit) {
        filterSelectListener = listener
    }
}