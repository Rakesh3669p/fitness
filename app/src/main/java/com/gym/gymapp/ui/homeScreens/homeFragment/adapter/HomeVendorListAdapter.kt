package com.gym.gymapp.ui.homeScreens.homeFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemVendorListBinding
import com.gym.gymapp.ui.packageListing.model.VendorsData
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class HomeVendorListAdapter @Inject constructor(): RecyclerView.Adapter<HomeVendorListAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(val binding: ItemVendorListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: VendorsData) {
            with(binding) {
                Glide.with(context!!).applyDefaultRequestOptions(requestOption()).load(data.profile).into(vendorImage)
                vendorName.text = data.name
                vendorGym.text = data.name_of_organization
                vendorGymDesc.text = data.type_of_organization
                vendorSince.text = data.created_at
                root.setOnClickListener {
                    vendorClickListener?.let {
                        it(data.id)
                    }
                }
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<VendorsData>() {
        override fun areItemsTheSame(oldItem: VendorsData, newItem: VendorsData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: VendorsData,
            newItem: VendorsData
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    private var vendorClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeVendorListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemVendorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeVendorListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = if (differ.currentList.size>4) 4 else differ.currentList.size

    fun setOnVendorClickListener(listener: (position: Int) -> Unit) {
        vendorClickListener = listener
    }
}