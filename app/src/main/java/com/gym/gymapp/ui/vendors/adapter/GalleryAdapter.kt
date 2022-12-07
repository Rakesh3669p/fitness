package com.gym.gymapp.ui.vendors.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemGalleryBinding
import javax.inject.Inject

class GalleryAdapter @Inject constructor() : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var context: Context? = null

    inner class ViewHolder(val binding:  ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            with(binding) {
                Glide.with(context!!).load(data).into(galleryImages)

                }
            }
        }


    private val differCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])

    }


    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }

}