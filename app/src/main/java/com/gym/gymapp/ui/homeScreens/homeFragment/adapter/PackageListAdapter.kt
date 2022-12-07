package com.gym.gymapp.ui.homeScreens.homeFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemLeftPackageBinding
import com.gym.gymapp.databinding.ItemRightPackageBinding
import com.gym.gymapp.ui.packageListing.model.PackageListData
import com.gym.gymapp.utils.requestOption
import com.gym.gymapp.utils.showToast
import javax.inject.Inject


class PackageListAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var context: Context? = null

    inner class ViewHolder(val binding: ItemRightPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PackageListData) {
            with(binding) {
                subBanner1GymPackageLbl.text = data.category_name
                Glide.with(context!!).applyDefaultRequestOptions(requestOption())
                    .load(data.mobile_image).into(subBannerImage1)
                if (data.starting_price == null) {
                    subBanner1Price.isVisible = false
                } else {
                    subBanner1Price.isVisible = true
                    subBanner1Price.text = "₹ ${data.starting_price}/-"
                }
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.id)
                    }
                }
            }
        }
    }

    inner class ViewHolder2(val binding: ItemLeftPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PackageListData) {
            with(binding) {
                subBanner2GymPackageLbl.text = data.category_name
                Glide.with(context!!).applyDefaultRequestOptions(requestOption())
                    .load(data.mobile_image).into(subBannerImage2)
                subBanner2Price.text = "₹ ${data.starting_price}/-"
                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<PackageListData>() {
        override fun areItemsTheSame(oldItem: PackageListData, newItem: PackageListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PackageListData,
            newItem: PackageListData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            0 -> {
                ViewHolder(
                    ItemRightPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            1 -> {
                ViewHolder2(
                    ItemLeftPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            }
            else -> {
                ViewHolder(
                    ItemRightPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val viewHolder0: ViewHolder = holder as ViewHolder
                viewHolder0.bind(differ.currentList[position])
            }
            1 -> {
                val viewHolder2: ViewHolder2 = holder as ViewHolder2
                viewHolder2.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
}