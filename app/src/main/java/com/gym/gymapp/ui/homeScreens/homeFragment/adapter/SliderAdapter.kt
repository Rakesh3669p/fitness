package com.gym.gymapp.ui.homeScreens.homeFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.SlideItemContainerBinding
import com.gym.gymapp.ui.homeScreens.homeFragment.model.SliderData
import javax.inject.Inject

class SliderAdapter @Inject constructor() : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {
    var context: Context? = null

    inner class SliderViewHolder(val binding: SlideItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImage(sliderItems: SliderData) {
            with(binding) {

                Glide.with(context!!).load(sliderItems.image).into(imageSlide)
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<SliderData>() {
        override fun areItemsTheSame(oldItem: SliderData, newItem: SliderData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SliderData, newItem: SliderData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        return SliderViewHolder(
            SlideItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(differ.currentList[position])

    }

    override fun getItemCount(): Int = differ.currentList.size

}