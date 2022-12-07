package com.gym.gymapp.ui.vendors.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gym.gymapp.ui.vendors.model.VendorDetailsData
import com.gym.gymapp.ui.vendors.vendorDetailsFragment.*

class VendorPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val vendorData: VendorDetailsData
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> OrganisationFragment(vendorData.organization_list)
            1 -> VendorPackagesFragment(vendorData.packages_list)
            2 -> VendorTrainerFragment(vendorData.trainer_list)
            3 -> VendorStarIconFragment(vendorData.start_list)
            else -> {
                OrganisationFragment(vendorData.organization_list)
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}