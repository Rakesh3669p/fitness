package com.gym.gymapp.ui.packageListing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentPackageFilterBinding
import com.gym.gymapp.databinding.FragmentPackageListBinding
import com.gym.gymapp.databinding.FragmentProductListBinding

class PackageListFragment : Fragment(R.layout.fragment_package_list) {

    lateinit var binding: FragmentPackageListBinding
    private var currentView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_package_list, container, false)
            binding = FragmentPackageListBinding.bind(currentView!!)
            init()
            setRecyclerView()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {}
    private fun setRecyclerView() {

    }
    private fun setObserver() {}
    private fun setOnClickListener() {}

}



