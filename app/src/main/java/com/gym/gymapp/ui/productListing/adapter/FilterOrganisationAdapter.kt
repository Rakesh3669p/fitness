package com.gym.gymapp.ui.productListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemFilterSelectionBinding
import com.gym.gymapp.ui.productListing.model.FilterOrganizationData
import javax.inject.Inject

class FilterOrganisationAdapter@Inject constructor(): RecyclerView.Adapter<FilterOrganisationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFilterSelectionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: FilterOrganizationData){
            with(binding){
                filterCheckBox.text = data.organization_name
                filterCheckBox.isChecked = data.checked

                filterCheckBox.setOnCheckedChangeListener { _, b ->
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

    private val differCallBack = object : DiffUtil.ItemCallback<FilterOrganizationData>(){
        override fun areItemsTheSame(
            oldItem: FilterOrganizationData,
            newItem: FilterOrganizationData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FilterOrganizationData,
            newItem: FilterOrganizationData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this,differCallBack)

    private var filterSelectListener: ((position: Int,status:Boolean) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterOrganisationAdapter.ViewHolder {
        return ViewHolder(
            ItemFilterSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: FilterOrganisationAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size


    fun setOnFilterSelectListener(listener: (position: Int,status:Boolean) -> Unit) {
        filterSelectListener = listener
    }
}