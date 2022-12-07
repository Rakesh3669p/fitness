package com.gym.gymapp.ui.vendors

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentVendorDetailsBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.vendors.adapter.VendorPagerAdapter
import com.gym.gymapp.ui.vendors.model.VendorDetailsData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap
import javax.inject.Inject

@AndroidEntryPoint
class VendorDetailsFragment : Fragment(R.layout.fragment_vendor_details), View.OnClickListener {
    lateinit var binding: FragmentVendorDetailsBinding

    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var appProgressBar: AgileLoader

    private val vendorVieModel: VendorVieModel by viewModels()

    private var currentView: View? = null

    private var vendorDetails:VendorDetailsData? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_vendor_details, container, false)
            binding = FragmentVendorDetailsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        appProgressBar = AgileLoader(requireContext())
        binding.progressView.isVisible = true
        if (arguments?.getString("id") != null) {
            vendorVieModel.getVendorDetails(arguments?.getString("id").toString())
        }
    }

    private fun setTabLayoutAndViewPager(vendorData: VendorDetailsData) {
        binding.vendorViewPager.isSaveEnabled = false
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_organisation).setText("Organisation"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_packages).setText("Packages"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_trainer).setText("Trainer"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_star_badge).setText("Star Icon"))


        val fragmentManager = childFragmentManager
        val viewPagerAdapter = VendorPagerAdapter(fragmentManager, lifecycle, vendorData)
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
        vendorVieModel.vendorDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgressBar.dismiss()
                    if (response.data!!.success) {
                        binding.progressView.isVisible = false
                        setVendorData(response.data.data[0])
                    } else {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                            .show()

                    }
                }
                is Resource.Loading -> {
                    appProgressBar.show()
                }
                is Resource.Error -> {
                    appProgressBar.dismiss()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setVendorData(vendorData: VendorDetailsData) {
        setTabLayoutAndViewPager(vendorData)
        vendorDetails = vendorData
        with(binding) {
            Glide.with(requireContext()).applyDefaultRequestOptions(requestOption())
                .load(vendorData.profile).into(vendorLogo)
            vendorName.text = vendorData.name
            totalPackage.text = "${vendorData.total_package} Packages"
            totalMembers.text = "${vendorData.total_member} Members"
            totalOrganisations.text = "${vendorData.total_organization} Organisations"
            if(!vendorData.latitude.isNullOrEmpty()&&!vendorData.longitude.isNullOrEmpty()){

            distanceKm.text = distance(session.userCurrentLat!!.toDouble(),session.userCurrentLong!!.toDouble(),vendorData.latitude.toDouble(),vendorData.longitude.toDouble()).toString()
                val params:MutableMap<String,String> = HashMap()
                params["destinations"] = "${vendorData.latitude},${vendorData.longitude}"
                params["origins"] = "${session.userCurrentLat},${session.userCurrentLong}"
                params["key"] = DISTANCE_MATRIX_KEY
                commonViewModel.getDistance(params)
                commonViewModel.distance.observe(viewLifecycleOwner){response->
                    when(response){
                        is Resource.Success->{
                            if(response.data?.status=="OK"){
                                distanceKm.isVisible = true
                                distanceKm.text = response.data.rows[0].elements[0].distance.text
                            }
                        }
                        is Resource.Loading->{

                        }
                        is Resource.Error->{}
                    }
                }
            }
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@VendorDetailsFragment)
            distanceKm.setOnClickListener(this@VendorDetailsFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backArrow -> {
                findNavController().popBackStack()
            }

            R.id.distanceKm -> {
                val latitude = vendorDetails?.latitude
                val longitude = vendorDetails?.longitude
                val uri= "http://maps.google.com/maps?saddr=${session.userCurrentLat},${session.userCurrentLong}&daddr=$latitude,$longitude"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                requireActivity().startActivity(intent)
            }
        }

    }
}