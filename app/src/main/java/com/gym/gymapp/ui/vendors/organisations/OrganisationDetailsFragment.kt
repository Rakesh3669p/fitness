package com.gym.gymapp.ui.vendors.organisations

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentOraganisationDetailsBinding
import com.gym.gymapp.ui.vendors.VendorVieModel
import com.gym.gymapp.ui.vendors.model.VendorDetailsData
import com.gym.gymapp.ui.vendors.organisations.adapter.OrganisationPagerAdapter
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrganisationDetailsFragment : Fragment(R.layout.fragment_oraganisation_details),
    View.OnClickListener {

    lateinit var binding: FragmentOraganisationDetailsBinding

    private var currentView: View? = null

    private val vendorVieModel: VendorVieModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var appProgressBar: AppProgressBar

    private var organisationDetails: VendorDetailsData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView =
                inflater.inflate(R.layout.fragment_oraganisation_details, container, false)
            binding = FragmentOraganisationDetailsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        binding.progressView.isVisible = true

        if (arguments?.getString("id") != null) {
            vendorVieModel.getOrganisationDetails(arguments?.getString("id").toString())
        }
    }

    private fun setTabLayoutAndViewPager(organisationData: VendorDetailsData) {
        binding.vendorViewPager.isSaveEnabled = false
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_packages))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_trainer))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_star_badge))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_gallery))


        val fragmentManager = childFragmentManager
        val viewPagerAdapter =
            OrganisationPagerAdapter(fragmentManager, lifecycle, organisationData)
        binding.vendorViewPager.adapter = viewPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.vendorViewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        binding.vendorViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

    }


    private fun setObserver() {

        vendorVieModel.organisationDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgressBar.dismiss()
                    if (response.data!!.success) {
                        binding.progressView.isVisible = false
                        setOrganisationData(response.data.data[0])
                        organisationDetails = response.data.data[0]
                    } else {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                            .show()

                    }
                }
                is Resource.Loading -> {
                    binding.progressView.isVisible = true
                    appProgressBar.show(childFragmentManager, "")
                }
                is Resource.Error -> {
                    binding.progressView.isVisible = true
                    appProgressBar.dismiss()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setOrganisationData(organisationDetailsData: VendorDetailsData) {

        setTabLayoutAndViewPager(organisationDetailsData)
        with(binding) {
            Glide.with(requireContext()).applyDefaultRequestOptions(requestOption())
                .load(organisationDetailsData.profile).into(vendorLogo)
            vendorName.text = organisationDetailsData.name
            totalPackage.text = "${organisationDetailsData.total_package} Packages"
            totalMembers.text = "${organisationDetailsData.total_member} Members"
            totalOrganisations.text = "${organisationDetailsData.total_start} Star Icons"

            distanceKm.text = distance(
                session.userCurrentLat!!.toDouble(),
                session.userCurrentLong!!.toDouble(),
                organisationDetailsData.latitude.toDouble(),
                organisationDetailsData.longitude.toDouble()
            ).toString()
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@OrganisationDetailsFragment)
            distanceKm.setOnClickListener(this@OrganisationDetailsFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.distanceKm -> {
                val latitude = organisationDetails?.latitude
                val longitude = organisationDetails?.longitude
                val uri =
                    "http://maps.google.com/maps?saddr=${session.userCurrentLat},${session.userCurrentLong}&daddr=$latitude,$longitude"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                requireActivity().startActivity(intent)
            }
        }

    }
}