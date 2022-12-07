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
import com.gym.gymapp.databinding.FragmentOraganisationDetailsBinding
import com.gym.gymapp.ui.vendors.adapter.VendorOrganisationGalleryAdapter
import com.gym.gymapp.ui.vendors.model.Organization
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrganisationFragment(val organizationList: List<Organization>) : Fragment(R.layout.fragment_oraganisation) {

    lateinit var binding: FragmentOraganisationBinding

    private var currentView: View? = null

    @Inject
    lateinit var organisationGalleryAdapter: VendorOrganisationGalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_oraganisation, container, false)
            binding = FragmentOraganisationBinding.bind(currentView!!)
            init()
            setObserver()
            setRecyclerView()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {

    }

    private fun setRecyclerView() {
        organisationGalleryAdapter.differ.submitList(organizationList)
        binding.galleryRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = organisationGalleryAdapter
        }
    }

    private fun setObserver() {

    }

    private fun setOnClickListener() {
        organisationGalleryAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id",it.toString())
            }
            findNavController().navigate(R.id.organisationDetailsFragment,bundle)
        }
    }

}