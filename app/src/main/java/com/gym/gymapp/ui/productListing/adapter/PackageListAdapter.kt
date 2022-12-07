package com.gym.gymapp.ui.productListing.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemPackageListBinding
import com.gym.gymapp.ui.productListing.model.ProductData
import javax.inject.Inject

class PackageListAdapter @Inject constructor() : RecyclerView.Adapter<PackageListAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemPackageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductData) {
            with(binding) {
                Glide.with(context!!).load(data.thumbnail).into(packageImage)
                packageName.text= data.name
                packageDesc.text= data.description

                listingPrice.paintFlags = listingPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                listingPrice.text = "₹ ${data.price}"
                sellingPrice.text = "₹ ${data.price}"

                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.id!!)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ProductData>() {
        override fun areItemsTheSame(
            oldItem: ProductData,
            newItem: ProductData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ProductData,
            newItem: ProductData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemPackageListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PackageListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
}