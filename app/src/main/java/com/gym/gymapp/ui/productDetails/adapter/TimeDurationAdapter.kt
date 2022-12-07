package com.gym.gymapp.ui.productDetails.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ItemSelectMonthBinding
import com.gym.gymapp.databinding.ItemSelectTimeBinding
import com.gym.gymapp.ui.productDetails.model.PackageStartTime
import com.gym.gymapp.ui.productDetails.model.PriceDuration
import javax.inject.Inject

class TimeDurationAdapter @Inject constructor() :
    RecyclerView.Adapter<TimeDurationAdapter.ViewHolder>() {

    private var context: Context? = null
    private var previousPosition: Int? = null

    inner class ViewHolder(val binding: ItemSelectTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(data: PackageStartTime) {
            with(binding) {
                if (data.status) {
                    packageTime.setTextColor(context!!.getColor(R.color.white))
                    mainLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.themeOrange_600
                        )
                    )
                } else {
                    packageTime.setTextColor(context!!.getColor(R.color.black))
                    mainLayout.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                }
                packageTime.text = "${data.start_time} - ${data.end_time}"

                root.setOnClickListener {
                    differ.currentList.forEachIndexed { index, packageStartTime ->
                        packageStartTime.status = absoluteAdapterPosition==index
                    }
                    packageDurationClickListener?.let {
                        it(absoluteAdapterPosition)
                    }
                    notifyDataSetChanged()
                }

            }

        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<PackageStartTime>() {
        override fun areItemsTheSame(
            oldItem: PackageStartTime,
            newItem: PackageStartTime
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PackageStartTime,
            newItem: PackageStartTime
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    private var packageDurationClickListener: ((position: Int) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeDurationAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemSelectTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TimeDurationAdapter.ViewHolder, position: Int) {

        holder.bind(differ.currentList[position])

    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageDurationClickListener(listener: (position: Int) -> Unit) {
        packageDurationClickListener = listener
    }
}