package com.gym.gymapp.ui.noification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemNotificationBinding
import com.gym.gymapp.ui.noification.model.NotificationData
import javax.inject.Inject

class NotificationAdapter @Inject constructor() :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationData) {
            with(binding) {
                notificationTitle.text = data.title
                notificationDesc.text = data.message
            }
        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.notification_id == newItem.notification_id
        }

        override fun areContentsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)
    private var vendorClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnVendorClickListener(listener: (position: Int) -> Unit) {
        vendorClickListener = listener
    }
}