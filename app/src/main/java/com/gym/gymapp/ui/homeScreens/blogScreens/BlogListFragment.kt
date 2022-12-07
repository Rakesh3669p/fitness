package com.gym.gymapp.ui.homeScreens.blogScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentBlogListBinding
import com.gym.gymapp.ui.homeScreens.blogScreens.adapter.BlogListAdapter
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BlogListFragment : Fragment(R.layout.fragment_blog_list), View.OnClickListener {

    private lateinit var binding: FragmentBlogListBinding

    private var currentView:View? = null

    private val blogsViewModel:BlogsViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager


    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var blogListAdapter: BlogListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_blog_list, container, false)
            binding = FragmentBlogListBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init(){
        appProgress = AgileLoader(requireContext())
    }

    private fun setObserver() {
        blogsViewModel.getBlogs()
        blogsViewModel.blogsList.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Success->{
                    appProgress.dismiss()
                    if(response.data!!.success){
                        if(response.data.data.isEmpty()){
                            requireContext().showToast("No blogs Found")

                        }else{
                            blogListAdapter.differ.submitList(response.data.data)

                        }

                        setRecyclerView()
                    }else{
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading->{
                    appProgress.show()
                }
                is Resource.Error->{
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.blogsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = blogListAdapter
        }
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@BlogListFragment)

        blogListAdapter.setOnBlogClickListener {
            val bundle = Bundle().apply {
                putString("id",it.toString())
            }

            findNavController().navigate(R.id.blogDetailsFragment,bundle)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.backArrow->{
                findNavController().popBackStack()
            }
        }
    }
}