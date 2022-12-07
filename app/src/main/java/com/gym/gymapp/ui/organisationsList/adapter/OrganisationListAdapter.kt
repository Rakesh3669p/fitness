package com.gym.gymapp.ui.organisationsList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemOrganisationList2Binding
import com.gym.gymapp.ui.organisationsList.model.OrganisationData
import javax.inject.Inject

class OrganisationListAdapter @Inject constructor() : RecyclerView.Adapter<OrganisationListAdapter.ViewHolder>() {
    private var context: Context? = null
    inner class ViewHolder(val binding: ItemOrganisationList2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrganisationData) {
            with(binding) {
                Glide.with(context!!).load(data.organization_image).into(organisationImage)
                organisationName.text= data.organization_name
                organisationDesc.text= data.description

                organisationAddress.text = data.organization_addresse

                root.setOnClickListener {
                    organisationClickListener?.let {
                        it(data)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<OrganisationData>() {
        override fun areItemsTheSame(
            oldItem: OrganisationData,
            newItem: OrganisationData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OrganisationData,
            newItem: OrganisationData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    private var organisationClickListener: ((data: OrganisationData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrganisationListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemOrganisationList2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrganisationListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setOnOrganisationClickListener(listener: (data: OrganisationData) -> Unit) {
        organisationClickListener = listener
    }
}