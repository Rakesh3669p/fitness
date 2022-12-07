package com.gym.gymapp.ui.slugs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentFAQBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.slugs.adapter.FAQAdapter
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FAQFragment : Fragment(R.layout.fragment_f_a_q), View.OnClickListener {
    private lateinit var binding: FragmentFAQBinding

    private var currentView: View? = null

    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var agileLoader: AgileLoader

    @Inject
    lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_f_a_q, container, false)
            binding = FragmentFAQBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        agileLoader = AgileLoader(requireContext())
        commonViewModel.getFAQ()
    }

    private fun setObserver() {

        commonViewModel.faq.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    agileLoader.dismiss()
                    if (response.data!!.success) {
                        faqAdapter.differ.submitList(response.data.data)
                        setFaqRecycle()
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    agileLoader.show()
                }
                is Resource.Error -> {
                    agileLoader.dismiss()
                }
            }
        }
    }

    private fun setFaqRecycle() {
        binding.faqRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = faqAdapter
        }
        faqAdapter.setRecyclerView(binding.faqRecycle)
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@FAQFragment)
        binding.chat.setOnClickListener(this@FAQFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.chat -> {
                findNavController().navigate(R.id.chatBoatFragment)
            }
        }
    }
}