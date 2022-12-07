package com.gym.gymapp.ui.homeScreens.blogScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentBlogDetailsBinding
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsDetailsData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BlogDetailsFragment : Fragment(R.layout.fragment_blog_details), View.OnClickListener {

    private lateinit var binding: FragmentBlogDetailsBinding

    private var currentView: View? = null

    private val blogsViewModel: BlogsViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager


    lateinit var appProgress: AgileLoader


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_blog_details, container, false)
            binding = FragmentBlogDetailsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        binding.progressBg.isVisible = true
    }

    private fun setObserver() {
        blogsViewModel.getBlogsDetails(arguments?.getString("id").toString());
        blogsViewModel.blogsDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBg.isVisible = false
                    appProgress.dismiss()
                    if (response.data!!.success) {
                        setBlogData(response.data.data)
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setBlogData(blogsData: BlogsDetailsData) {

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        with(binding) {
            Glide.with(requireContext()).applyDefaultRequestOptions(requestOption())
                .load(blogsData.image).into(blogImage)

            blogTitle.text = blogsData.blog_name
            blogCreatedBy.text = "Created By: ${blogsData.created_by}"

            try {
                val formatDate: Date = dateFormat.parse(blogsData.created_at)
                blogCreated.text = formatDate.toString()

            } catch (e: Exception) {
                println("Exception$e")
            }
            blogDesc.text =
                HtmlCompat.fromHtml(blogsData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@BlogDetailsFragment)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }
}