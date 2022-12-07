package com.gym.gymapp.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.cart.model.GetCartData
import com.gym.gymapp.databinding.ItemCartBinding

import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class CartAdapter @Inject constructor() :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GetCartData) {
            with(binding) {
                Glide.with(context!!).applyDefaultRequestOptions(requestOption()).load(data.thumbnail)
                    .into(packageImage)
                packageName.text = data.package_name
                packageDesc.text = data.description
                packagePrice.text = "₹ ${data.package_price}/-"
                packageListPrice.isVisible = false

                removeFromCart.setOnClickListener {
                    removeClickListener?.let {
                        it(data.id,adapterPosition)
                    }
                }
                root.setOnClickListener {
                    productClickListener?.let {
                        it(data.package_id)
                    }
                }
            }
        }
    }

    private var removeClickListener: ((cartID: Int,position:Int) -> Unit)? = null
    private var productClickListener: ((cartID: Int) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<GetCartData>() {
        override fun areItemsTheSame(
            oldItem: GetCartData,
            newItem: GetCartData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GetCartData,
            newItem: GetCartData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnRemoveProductListener(listener: (cartId: Int,position:Int) -> Unit) {
        removeClickListener = listener
    }
    fun setOnProductListener(listener: (cartId: Int) -> Unit) {
        productClickListener = listener
    }
}

