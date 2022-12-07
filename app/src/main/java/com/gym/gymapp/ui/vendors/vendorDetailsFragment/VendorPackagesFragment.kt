package com.gym.gymapp.ui.vendors.vendorDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentOraganisationBinding
import com.gym.gymapp.databinding.FragmentVendorPackagesBinding
import com.gym.gymapp.ui.productListing.adapter.PackageListAdapter
import com.gym.gymapp.ui.vendors.adapter.VendorPackageGalleryAdapter
import com.gym.gymapp.ui.vendors.model.Packages
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VendorPackagesFragment(val packagesList: List<Packages>) : Fragment(R.layout.fragment_vendor_packages) {
    lateinit var binding: FragmentVendorPackagesBinding

    private var currentView: View? = null

    @Inject
    lateinit var packageGalleryAdapter: VendorPackageGalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_vendor_packages, container, false)
            binding = FragmentVendorPackagesBinding.bind(currentView!!)
            init()
            setObserver()
            setRecyclerView()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init(){

    }
    private fun setRecyclerView() {
        packageGalleryAdapter.differ.submitList(packagesList)
        binding.galleryRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),3)
            adapter = packageGalleryAdapter
        }
    }

    private fun setObserver(){

    }

    private fun setOnClickListener(){
        packageGalleryAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }
            findNavController().navigate(
                R.id.productDetailsFragment,
                bundle
            )
        }
    }
}