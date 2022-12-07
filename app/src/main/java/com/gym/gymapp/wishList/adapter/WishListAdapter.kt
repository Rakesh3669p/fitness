package com.gym.gymapp.wishList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemCartBinding
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class WishListAdapter @Inject constructor() :
    RecyclerView.Adapter<WishListAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WishListPackageData) {
            with(binding) {
                Glide.with(context!!).applyDefaultRequestOptions(requestOption())
                    .load(data.thumbnail).into(packageImage)
                packageName.text = data.name
                packageDesc.text = data.description
                packagePrice.text = "₹ ${data.priceduration?.get(0)!!.price}/-"
                packageListPrice.isVisible = false
                removeFromCart.setOnClickListener {
                    removeClickListener?.let {
                        it(data.id!!)
                    }
                }

                root.setOnClickListener {
                    productClickListener?.let {
                        it(data.id!!)
                    }
                }
            }
        }
    }

    private var removeClickListener: ((id: Int) -> Unit)? = null
    private var productClickListener: ((id: Int) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<WishListPackageData>() {
        override fun areItemsTheSame(oldItem: WishListPackageData, newItem: WishListPackageData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WishListPackageData, newItem: WishListPackageData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WishListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WishListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnRemoveProductListener(listener: (position: Int) -> Unit) {
        removeClickListener = listener
    }

    fun setOnProductListener(listener: (position: Int) -> Unit) {
        productClickListener = listener
    }
}