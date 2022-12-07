package com.gym.gymapp.ui.vendors.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemGalleryBinding
import com.gym.gymapp.databinding.ItemPackageGalleryBinding
import com.gym.gymapp.ui.vendors.model.Trainer
import javax.inject.Inject

class VendorTrainerGalleryAdapter @Inject constructor() : RecyclerView.Adapter<VendorTrainerGalleryAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemPackageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Trainer) {
            with(binding) {
                Glide.with(context!!).load(data.profile).into(galleryImages)
                packageName.text = data.name
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.trainer_id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Trainer>() {
        override fun areItemsTheSame(
            oldItem: Trainer,
            newItem: Trainer
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Trainer,
            newItem: Trainer
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorTrainerGalleryAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemPackageGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VendorTrainerGalleryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
}