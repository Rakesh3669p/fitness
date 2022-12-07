package com.gym.gymapp.ui.vendors.vendorDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentVendorStarIconBinding
import com.gym.gymapp.databinding.FragmentVendorTrainerBinding
import com.gym.gymapp.ui.vendors.adapter.VendorStarGalleryAdapter
import com.gym.gymapp.ui.vendors.model.Start
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VendorStarIconFragment(val startList: List<Start>) :
    Fragment(R.layout.fragment_vendor_star_icon) {
    lateinit var binding: FragmentVendorStarIconBinding

    private var currentView: View? = null

    @Inject
    lateinit var starGalleryAdapter: VendorStarGalleryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_vendor_star_icon, container, false)
            binding = FragmentVendorStarIconBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
            setRecyclerView()
        }
        return currentView!!
    }

    private fun init() {

    }

    private fun setRecyclerView() {
        starGalleryAdapter.differ.submitList(startList)
        binding.galleryRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = starGalleryAdapter
        }
    }

    private fun setObserver() {

    }

    private fun setOnClickListener() {
        starGalleryAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }
            findNavController().navigate(R.id.starIconFragment, bundle)
        }
    }
}