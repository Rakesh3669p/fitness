package com.gym.gymapp.ui.productListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemFilterSelectionBinding
import javax.inject.Inject

class PackageFilterListAdapter @Inject constructor():RecyclerView.Adapter<PackageFilterListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemFilterSelectionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: String){
            with(binding){
                filterCheckBox.text = data
            }
        }
    }

    private var differCallBack = object:DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageFilterListAdapter.ViewHolder {
        return  ViewHolder(ItemFilterSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PackageFilterListAdapter.ViewHolder, position: Int) {
        holder.bind(data = differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
}