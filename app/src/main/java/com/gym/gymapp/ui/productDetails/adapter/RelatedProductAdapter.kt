package com.gym.gymapp.ui.productDetails.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemRelatedProductsBinding
import com.gym.gymapp.ui.productListing.model.ProductData
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class RelatedProductAdapter @Inject constructor() :
    RecyclerView.Adapter<RelatedProductAdapter.ViewHolder>() {

    private var context:Context?=null
    inner class ViewHolder(val binding: ItemRelatedProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductData) {
            with(binding) {
                Glide.with(context!!).applyDefaultRequestOptions(requestOption())
                    .load(data.thumbnail).into(productImage)

                listingPrice.paintFlags = listingPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                listingPrice.text = "₹ ${data.price}"
                price.text = "₹ ${data.price}"
                productName.text = data.name

                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.id)
                    }
                }
            }
        }
    }
    private var packageClickListener: ((position: Int) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<ProductData>(){
        override fun areItemsTheSame(oldItem: ProductData, newItem: ProductData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductData, newItem: ProductData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RelatedProductAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ItemRelatedProductsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RelatedProductAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }

}