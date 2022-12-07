package com.gym.gymapp.ui.vendors.organisations.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gym.gymapp.ui.vendors.model.VendorDetailsData
import com.gym.gymapp.ui.vendors.vendorDetailsFragment.*

class OrganisationPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val vendorData: VendorDetailsData
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> VendorPackagesFragment(vendorData.packages_list)
            1 -> VendorTrainerFragment(vendorData.trainer_list)
            2 -> VendorStarIconFragment(vendorData.start_list)
            3 -> GalleryFragment(vendorData.image_gallerys)

            else -> {
                VendorPackagesFragment(vendorData.packages_list)
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}