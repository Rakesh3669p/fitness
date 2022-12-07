package com.gym.gymapp.ui.productListing

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentPackageFilterBinding
import com.gym.gymapp.ui.productListing.adapter.*
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.AppProgressBar
import com.gym.gymapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.M)
@AndroidEntryPoint
class PackageFilterFragment : Fragment(R.layout.fragment_package_filter), View.OnClickListener {

    private lateinit var binding: FragmentPackageFilterBinding
    private var currentView: View? = null
    private val filterViewModel: FilterViewModel by viewModels()

    @Inject
    lateinit var filterListAdapter: PackageFilterListAdapter

    @Inject
    lateinit var filterSubCategoryAdapter: FilterSubCategoryAdapter

    @Inject
    lateinit var filterOrganisationAdapter: FilterOrganisationAdapter

    @Inject
    lateinit var filterCategoryAdapter: FilterCategoryAdapter

    @Inject
    lateinit var filterVendorAdapter: FilterVendorAdapter

    lateinit var appProgress: AgileLoader

    private var vendorFilters: MutableList<String> = ArrayList()
    private var categoryFilters: MutableList<String> = ArrayList()
    private var subCategoryFilters: MutableList<String> = ArrayList()
    private var organizationFilters: MutableList<String> = ArrayList()

    private var categoryId = ""
    private var serviceProviderId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_package_filter, container, false)
            binding = FragmentPackageFilterBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }

        return currentView!!
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        with(binding) {

            /*if (!arguments?.getString("serviceProviderId").isNullOrEmpty()) {
                categoryId = arguments?.getString("id").toString()
                serviceProviderId = arguments?.getString("serviceProviderId") ?: ""
                filterViewModel.getFilterVendor()
                filterViewModel.getFilterCategory("1")
                filterViewModel.getFilterOrganisation(serviceProviderId)
            }*/

            filterViewModel.getFilterCategory()
            filterViewModel.getFilterSubCategory()

            binding.filterByCategory.setTextColor(requireContext().getColor(R.color.themeOrange))
            filterOrganisationRv.isVisible = false
            filterVendorRv.isVisible = false
            filterSubCategoryRv.isVisible = false
            filterCategoryRv.isVisible = true
        }

    }

    private fun setObserver() {
        /*  filterViewModel.filterVendor.observe(viewLifecycleOwner) { response ->
              when (response) {
                  is Resource.Success -> {
                      if (response.data?.data!!.isNotEmpty()) {
                          filterVendorAdapter.differ.submitList(response.data.data)
                      }
                  }
                  is Resource.Loading -> {
                      appProgress.show()

                  }
                  is Resource.Error -> {
                  }
              }
          }*/

        filterViewModel.filterCategory.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.data!!.isNotEmpty()) {
                        filterCategoryAdapter.differ.submitList(response.data.data)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }
        }
        filterViewModel.filterSubCategory.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.data!!.isNotEmpty()) {
                        filterSubCategoryAdapter.differ.submitList(response.data.data)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }
        }

        /* filterViewModel.filterOrganisation.observe(viewLifecycleOwner) { response ->
             when (response) {
                 is Resource.Success -> {
                     appProgress.dismiss()
                     if (response.data?.data!!.isNotEmpty()) {
                         filterOrganisationAdapter.differ.submitList(response.data.data)

                     }
                 }
                 is Resource.Loading -> {

                 }
                 is Resource.Error -> {
                     appProgress.dismiss()
                 }
             }
         }*/
        setRecyclerView()

    }

    private fun setOnClickListener() {
        with(binding) {
            filterByOrganisation.setOnClickListener(this@PackageFilterFragment)
            filterByCategory.setOnClickListener(this@PackageFilterFragment)
            filterBySubCategory.setOnClickListener(this@PackageFilterFragment)
            applyFilerCV.setOnClickListener(this@PackageFilterFragment)
            backArrow.setOnClickListener(this@PackageFilterFragment)
        }


        filterCategoryAdapter.setOnFilterSelectListener { it, status ->
            if (status) {
                categoryFilters.add(it.toString())
            } else {
                categoryFilters.remove(it.toString())
            }
        }

        filterSubCategoryAdapter.setOnFilterSelectListener { it, status ->
            if (status) {
                subCategoryFilters.add(it.toString())
            } else {
                subCategoryFilters.remove(it.toString())
            } }
    }

    private fun setRecyclerView() {

        /* binding.filterOrganisationRv.apply {
             setHasFixedSize(true)
             layoutManager = LinearLayoutManager(requireContext())
             adapter = filterOrganisationAdapter
         }

         binding.filterVendorRv.apply {
             setHasFixedSize(true)
             layoutManager = LinearLayoutManager(requireContext())
             adapter = filterVendorAdapter
         }*/

        binding.filterCategoryRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filterCategoryAdapter
        }


        binding.filterSubCategoryRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filterSubCategoryAdapter
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.filterByCategory -> {
                setSelectionColor(
                    binding.filterByCategory,
                    binding.filterBySubCategory
                )
                with(binding) {
                    filterCategoryRv.isVisible = true
                    filterSubCategoryRv.isVisible = false
                }
            }

            R.id.filterBySubCategory -> {
                setSelectionColor(
                    binding.filterBySubCategory,
                    binding.filterByCategory
                )
                with(binding) {
                    filterSubCategoryRv.isVisible = true
                    filterCategoryRv.isVisible = false
                }
            }


            R.id.applyFilerCV -> {
                findNavController().popBackStack()
                findNavController().popBackStack()

                val bundle = Bundle().apply {
                    putString("filterVendors", java.lang.String.join(",", vendorFilters))
                    putString("filterCategories", java.lang.String.join(",", categoryFilters))
                    putString("filterSubCategories", java.lang.String.join(",", subCategoryFilters))
                    putString("filterOrganization", java.lang.String.join(",", organizationFilters))
                    putString("id", categoryId)
                    putString("serviceProviderId", serviceProviderId)
                    putString("from", "filter")
                }
                findNavController().navigate(R.id.packageListFragment, bundle)
            }
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun setSelectionColor(
        selectedFilter: TextView,
        selectedFilter1: TextView
    ) {
        selectedFilter.setTextColor(requireContext().getColor(R.color.themeOrange))
        selectedFilter1.setTextColor(requireContext().getColor(R.color.black))
    }
}
