package com.gym.gymapp.ui.orders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemOrdersHistoryBinding
import com.gym.gymapp.ui.orders.model.OrdersData
import javax.inject.Inject

class OrdersAdapter @Inject constructor() : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding:  ItemOrdersHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrdersData) {
            with(binding) {
                Glide.with(context!!).load(data.package_image).into(logo)
                packageName.text= data.package_name
                packageStarDate.text= data.package_start_date
                packageEndDate.text= data.package_expire_date

                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.package_id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<OrdersData>() {
        override fun areItemsTheSame(
            oldItem: OrdersData,
            newItem: OrdersData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OrdersData,
            newItem: OrdersData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemOrdersHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }


}