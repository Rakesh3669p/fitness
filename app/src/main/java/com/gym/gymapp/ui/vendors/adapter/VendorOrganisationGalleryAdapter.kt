package com.gym.gymapp.ui.vendors.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemOrganisationBinding
import com.gym.gymapp.ui.vendors.model.Organization
import javax.inject.Inject

class VendorOrganisationGalleryAdapter @Inject constructor() : RecyclerView.Adapter<VendorOrganisationGalleryAdapter.ViewHolder>() {
    private var context: Context? = null
    var isFromGallery = false
    inner class ViewHolder(val binding: ItemOrganisationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Organization) {
            with(binding) {
                Glide.with(context!!).load(data.organization_image).into(organisationImage)
                organisationName.text = data.organization_name
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.organization_id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Organization>() {
        override fun areItemsTheSame(
            oldItem: Organization,
            newItem: Organization
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Organization,
            newItem: Organization
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorOrganisationGalleryAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemOrganisationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VendorOrganisationGalleryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }

}