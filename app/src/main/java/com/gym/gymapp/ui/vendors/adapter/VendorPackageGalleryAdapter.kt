package com.gym.gymapp.ui.vendors.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemPackageGalleryBinding
import com.gym.gymapp.ui.vendors.model.Packages
import javax.inject.Inject

class VendorPackageGalleryAdapter @Inject constructor() : RecyclerView.Adapter<VendorPackageGalleryAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemPackageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Packages) {
            with(binding) {
                Glide.with(context!!).load(data.thumbnail).into(galleryImages)
                packageName.text = data.name
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.package_id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Packages>() {
        override fun areItemsTheSame(
            oldItem: Packages,
            newItem: Packages
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Packages,
            newItem: Packages
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorPackageGalleryAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemPackageGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VendorPackageGalleryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
}