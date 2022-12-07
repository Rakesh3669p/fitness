package com.gym.gymapp.ui.slugs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentFAQBinding
import com.gym.gymapp.databinding.FragmentSlugsBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.slugs.adapter.FAQAdapter
import com.gym.gymapp.utils.AppProgressBar
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SlugsFragment : Fragment(R.layout.fragment_slugs), View.OnClickListener {
    private lateinit var binding: FragmentSlugsBinding

    private var currentView: View? = null

    private val commonViewModel: CommonViewModel by viewModels()
    @Inject
    lateinit var appProgressBar: AppProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_slugs, container, false)
            binding = FragmentSlugsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        commonViewModel.getSlugs(arguments?.getString("id").toString())
        appProgressBar.show(childFragmentManager,"")
    }

    private fun setObserver() {

        commonViewModel.pages.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgressBar.dismiss()
                    if (response.data!!.success) {
                        binding.title.text = response.data.data[0].page_title
                        binding.slugDescription.text = HtmlCompat.fromHtml(response.data.data[0].description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    appProgressBar.showsDialog
                }
                is Resource.Error -> {
                    appProgressBar.dismiss()
                }
            }
        }
    }





    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@SlugsFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }
}