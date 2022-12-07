package com.gym.gymapp.ui.packageListing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentVendorListBinding
import com.gym.gymapp.ui.packageListing.adapter.VendorListAdapter
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.AppProgressBar
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VendorListFragment : Fragment(R.layout.fragment_vendor_list), View.OnClickListener {
    private lateinit var binding: FragmentVendorListBinding
    private var currentView: View? = null

    private val vendorListViewModel: VendorListViewModel by viewModels()

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var vendorListAdapter: VendorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_vendor_list, container, false)
            binding = FragmentVendorListBinding.bind(currentView!!)
            init()
            setObservers()
            setOnClickListener()
        }
        return currentView!!
    }



    private fun init() {
        appProgress = AgileLoader(requireContext())
    }

    private fun setRecyclerView() {
        binding.vendorRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vendorListAdapter
        }
    }

    private fun setObservers() {
        vendorListViewModel.vendorList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()

                    if(response.data!!.success){
                        if(response.data.data.isNullOrEmpty()){
                            binding.vendorRv.isVisible = false
                            binding.noVendorsLbl.isVisible= true
                        }else{
                            binding.vendorRv.isVisible = true
                            binding.noVendorsLbl.isVisible= false
                            vendorListAdapter.differ.submitList(response.data.data)
                            setRecyclerView()
                        }
                    }else{
                        requireContext().showToast(response.message.toString())
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

    private fun setOnClickListener() {
        with(binding){
            backArrow.setOnClickListener(this@VendorListFragment)
        }

        vendorListAdapter.setOnVendorClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }

            findNavController().navigate(
                R.id.vendorDetailsFragment,
                bundle
            )
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