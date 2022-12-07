package com.gym.gymapp.ui.vendors.vendorDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentGalleryBinding
import com.gym.gymapp.databinding.FragmentOraganisationBinding
import com.gym.gymapp.ui.vendors.adapter.GalleryAdapter
import com.gym.gymapp.ui.vendors.adapter.VendorOrganisationGalleryAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment(private val imagesList:List<String>) : Fragment(R.layout.fragment_gallery) {
    lateinit var binding: FragmentGalleryBinding

    private var currentView: View? = null

    @Inject
    lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_gallery, container, false)
            binding = FragmentGalleryBinding.bind(currentView!!)
            setRecyclerView()
        }
        return currentView!!
    }


    private fun setRecyclerView() {
        galleryAdapter.differ.submitList(imagesList)
        binding.galleryRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = galleryAdapter
        }
    }
}