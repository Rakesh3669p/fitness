package com.gym.gymapp.ui.achievements.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemAchievementBinding
import com.gym.gymapp.ui.achievements.model.AchievementsData
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AchievementsAdapter @Inject constructor() : RecyclerView.Adapter<AchievementsAdapter.ViewHolder>() {

    lateinit var context: Context
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AchievementsData) {
            with(binding) {

                achievementsTitle.text = data.title
                achievementsDesc.text = data.description

                val startDateUnFormat: Date = simpleDateFormat.parse(data.start_date.substring(0, 10))
                val startDate: String = formattedDate.format(startDateUnFormat)

                val endDateUnFormat: Date = simpleDateFormat.parse(data.complete_date.substring(0, 10))
                val endDate: String? = formattedDate.format(endDateUnFormat)

                achievementsDate.text = "$startDate - $endDate"


                if(data.status =="Complete"){
                    statusStart.isVisible = false
                    statusEnd.isVisible = true
                }else if(data.status == "Active"){
                    statusStart.isVisible = true
                    statusEnd.isVisible = false
                }


                deleteAchievements.setOnClickListener {
                    packageClickListener?.let {
                        it(data.id)
                    }
                }

                updateAchievements.setOnClickListener {
                    packageEditClickListener?.let {
                        it(data)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<AchievementsData>() {
        override fun areItemsTheSame(
            oldItem: AchievementsData,
            newItem: AchievementsData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AchievementsData,
            newItem: AchievementsData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: Int) -> Unit)? = null
    private var packageEditClickListener: ((data: AchievementsData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AchievementsAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AchievementsAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
    fun setOnAchievementsClickListener(listener: (position: Int) -> Unit) {
        packageClickListener = listener
    }
    fun setOnAchievementEditClickListener(listener: (data: AchievementsData) -> Unit) {
        packageEditClickListener = listener
    }
}