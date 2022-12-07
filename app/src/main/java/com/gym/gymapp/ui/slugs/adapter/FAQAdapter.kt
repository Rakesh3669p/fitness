package com.gym.gymapp.ui.slugs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ItemFaqBinding
import com.gym.gymapp.ui.slugs.model.FAQData
import javax.inject.Inject

class FAQAdapter @Inject constructor() : RecyclerView.Adapter<FAQAdapter.ViewHolder>() {

    private lateinit var faqRecyclerView: RecyclerView
    private lateinit var context: Context
    private var previousIndex: Int? = null

    inner class ViewHolder(val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FAQData) {
            with(binding) {
                title.text = "${adapterPosition + 1}.  ${data.title}"
                if (data.showDesc) {
                    description.isVisible = true
                    description.text =
                        HtmlCompat.fromHtml(data.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } else {
                    description.isVisible = false
                }
                root.setOnClickListener {
                    if (previousIndex == adapterPosition && data.showDesc) {
                        data.showDesc = false
                        arrow.setImageResource(R.drawable.ic_arrow_down)
                        notifyDataSetChanged()
                    } else {
                        previousIndex?.let {
                            differ.currentList[it].showDesc = false
                            val recyclerView = faqRecyclerView.findViewHolderForAdapterPosition(it)
                            val arrowImageView=(recyclerView as ViewHolder).itemView.findViewById<ImageView>(R.id.arrow)
                            arrowImageView.setImageResource(R.drawable.ic_arrow_down)
                        }
                        previousIndex = adapterPosition
                        data.showDesc = true
                        arrow.setImageResource(R.drawable.ic_arrow_up)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<FAQData>() {
        override fun areItemsTheSame(
            oldItem: FAQData,
            newItem: FAQData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FAQData,
            newItem: FAQData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setRecyclerView(recyclerView: RecyclerView){
        faqRecyclerView = recyclerView
    }
}