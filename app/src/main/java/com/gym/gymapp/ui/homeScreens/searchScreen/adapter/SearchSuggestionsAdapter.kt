package com.gym.gymapp.ui.homeScreens.searchScreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemSearchBinding
import com.gym.gymapp.ui.homeScreens.searchScreen.model.SearchData
import javax.inject.Inject


class SearchSuggestionsAdapter @Inject constructor() :
    RecyclerView.Adapter<SearchSuggestionsAdapter.ViewHolder>() {
    private var context: Context? = null

    inner class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchData) {
            with(binding) {


                searchText.text = data.name

                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data.name)
                    }
                }

            }
        }
    }


    private val differCallBack = object : DiffUtil.ItemCallback<SearchData>() {
        override fun areItemsTheSame(oldItem: SearchData, newItem: SearchData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchData,
            newItem: SearchData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    private var packageClickListener: ((position: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = if (differ.currentList.size > 4)  4 else differ.currentList.size


    fun setOnSearchClickListener(listener: (position: String) -> Unit) {
        packageClickListener = listener
    }
}
