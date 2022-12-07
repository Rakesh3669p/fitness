package com.gym.gymapp.ui.productDetails.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ItemSelectMonthBinding
import com.gym.gymapp.ui.productDetails.model.PriceDuration
import javax.inject.Inject

class PackageDurationAdapter @Inject constructor() :
    RecyclerView.Adapter<PackageDurationAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(val binding: ItemSelectMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(data: PriceDuration) {
            with(binding) {
                if (data.status) {
                    packageMonths.setTextColor(context!!.getColor(R.color.white))
                    packageMonths.background = context!!.getDrawable(R.drawable.orange_corner_radius)
                } else {
                    packageMonths.setTextColor(context!!.getColor(R.color.black))
                    packageMonths.background = context!!.getDrawable(R.drawable.white_corner_radius)
                }
                packageMonths.text = "${data.month}"
                root.setOnClickListener {
                    differ.currentList.forEachIndexed { index, it ->
                        it.status = absoluteAdapterPosition==index
                    }
                    packageDurationClickListener?.let {
                        it(absoluteAdapterPosition)
                    }

                    notifyDataSetChanged()
                }
            }

        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<PriceDuration>() {
        override fun areItemsTheSame(oldItem: PriceDuration, newItem: PriceDuration): Boolean {
            return oldItem.month == newItem.month
        }

        override fun areContentsTheSame(oldItem: PriceDuration, newItem: PriceDuration): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    private var packageDurationClickListener: ((position: Int) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageDurationAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemSelectMonthBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: PackageDurationAdapter.ViewHolder, position: Int) {

        holder.bind(differ.currentList[position])

    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageDurationClickListener(listener: (position: Int) -> Unit) {
        packageDurationClickListener = listener
    }
}