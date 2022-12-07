package com.gym.gymapp.ui.homeScreens.blogScreens.adapter

import android.content.Context
import android.text.Html
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.databinding.ItemBlogListBinding
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsData
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject

class BlogListAdapter @Inject constructor() : RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {

    lateinit var context: Context
    inner class ViewHolder(val binding: ItemBlogListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BlogsData) {
            with(binding) {
                Glide.with(context).applyDefaultRequestOptions(requestOption()).load(data.image).into(blogsImage)
                blogsTitle.text = data.blog_name
                viewsCount.text = data.view_count
                blogsDesc.text = HtmlCompat.fromHtml(data.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                root.setOnClickListener {
                    blogClickListener?.let {
                        it(data.id)
                    }
                }

            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<BlogsData>() {
        override fun areItemsTheSame(
            oldItem: BlogsData,
            newItem: BlogsData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BlogsData,
            newItem: BlogsData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    private var blogClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BlogListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemBlogListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BlogListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnBlogClickListener(listener: (position: Int) -> Unit) {
        blogClickListener = listener
    }
}