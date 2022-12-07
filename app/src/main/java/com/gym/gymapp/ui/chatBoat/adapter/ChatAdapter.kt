package com.gym.gymapp.ui.chatBoat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.R
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksData
import com.gym.gymapp.ui.chatBoat.model.ChatData

import javax.inject.Inject


class ChatAdapter @Inject constructor() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var showOptions: Boolean = true
    private lateinit var context: Context
    private val TYPE_MESSAGE_SENT = 0
    private val TYPE_MESSAGE_RECEIVED = 1

    @Inject
    lateinit var optionAdapter: ChatOptionsAdapter

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val diffUtil = object : DiffUtil.ItemCallback<ChatData>() {
        override fun areItemsTheSame(oldItem: ChatData, newItem: ChatData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatData, newItem: ChatData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        context = parent.context
        return if (viewType == TYPE_MESSAGE_RECEIVED) {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_left_chat,
                    parent,
                    false
                )
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_right_chat,
                    parent,
                    false
                )
            )
        }
    }

    private var optionsClickListener: ((options: String, id: Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.itemView.apply {

            when (getItemViewType(position)) {
                TYPE_MESSAGE_SENT -> {
                    findViewById<TextView>(R.id.answer).text = model.answer
                }

                else -> {
                    findViewById<RecyclerView>(R.id.optionsRv).isVisible = showOptions
                    findViewById<TextView>(R.id.question).text = model.question
                    optionAdapter.setCurrentQuestionId(model.id!!)


                    if (position == differ.currentList.size - 1) {
                        optionAdapter.differ.submitList(model.options)
                        findViewById<RecyclerView>(R.id.optionsRv).apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = optionAdapter
                        }
                    }

                    optionAdapter.setOnOptionClickListener { option, currentQuestionId ->
                        optionsClickListener?.let { it(option, currentQuestionId) }
                    }

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].right) {
            true -> {
                TYPE_MESSAGE_SENT
            }
            false -> {
                TYPE_MESSAGE_RECEIVED
            }
        }
    }


    override fun getItemCount(): Int = differ.currentList.size


    fun setOnOptionClickListener(listener: (option: String, id: Int) -> Unit) {
        optionsClickListener = listener
    }

    fun showOption(status: Boolean) {
        showOptions = status
        notifyDataSetChanged()
    }
}