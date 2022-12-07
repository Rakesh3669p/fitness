package com.gym.gymapp.ui.productDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemProductImageBinding
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class ProductImageAdapter @Inject constructor() :
    RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {

    private var context: Context?=null
    inner class ViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            with(binding) {
                Glide.with(context!!).applyDefaultRequestOptions(requestOption())
                    .load(data).into(productImage)

            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductImageAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ItemProductImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductImageAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
}