package com.gym.gymapp.ui.chatBoat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ItemOptionsBinding
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksData
import com.gym.gymapp.ui.chatBoat.model.ChatData

import javax.inject.Inject


class ChatOptionsAdapter @Inject constructor() :
    RecyclerView.Adapter<ChatOptionsAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var currentQuestionId = 0

    inner class ViewHolder(val binding: ItemOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String?) {
            with(binding) {
                option.text = data
                root.setOnClickListener {
                    optionsClickListener?.let { it(data.toString(),currentQuestionId) }
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatOptionsAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemOptionsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private var optionsClickListener: ((options: String,id:Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ChatOptionsAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size


    fun setOnOptionClickListener(listener: (option: String,id:Int) -> Unit) {
        optionsClickListener = listener
    }

    fun setCurrentQuestionId(id: Int) {
        currentQuestionId = id

    }
}