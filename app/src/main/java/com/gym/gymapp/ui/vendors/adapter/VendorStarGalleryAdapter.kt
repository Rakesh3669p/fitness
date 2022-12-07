package com.gym.gymapp.ui.vendors.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemGalleryBinding
import com.gym.gymapp.databinding.ItemPackageGalleryBinding
import com.gym.gymapp.ui.vendors.model.Start
import javax.inject.Inject

class VendorStarGalleryAdapter @Inject constructor() : RecyclerView.Adapter<VendorStarGalleryAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemPackageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Start) {
            with(binding) {
                Glide.with(context!!).load(data.profile).into(galleryImages)
                packageName.text = data.name
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.star_icon_id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Start>() {
        override fun areItemsTheSame(
            oldItem: Start,
            newItem: Start
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Start,
            newItem: Start
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorStarGalleryAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemPackageGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VendorStarGalleryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
}