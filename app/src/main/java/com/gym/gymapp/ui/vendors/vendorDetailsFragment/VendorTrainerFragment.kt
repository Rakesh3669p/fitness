package com.gym.gymapp.ui.vendors.vendorDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentVendorPackagesBinding
import com.gym.gymapp.databinding.FragmentVendorTrainerBinding
import com.gym.gymapp.ui.vendors.adapter.VendorTrainerGalleryAdapter
import com.gym.gymapp.ui.vendors.model.Trainer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VendorTrainerFragment(val trainerList: List<Trainer>) : Fragment(R.layout.fragment_vendor_trainer) {
    lateinit var binding: FragmentVendorTrainerBinding

    private var currentView: View? = null

    @Inject
    lateinit var trainerGalleryAdapter: VendorTrainerGalleryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_vendor_trainer, container, false)
            binding = FragmentVendorTrainerBinding.bind(currentView!!)
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
        trainerGalleryAdapter.differ.submitList(trainerList)
        binding.galleryRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),3)
            adapter = trainerGalleryAdapter
        }
    }
    private fun setObserver(){

    }

    private fun setOnClickListener(){
        trainerGalleryAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }
            findNavController().navigate(R.id.trainerFragment, bundle)
        }
    }
}