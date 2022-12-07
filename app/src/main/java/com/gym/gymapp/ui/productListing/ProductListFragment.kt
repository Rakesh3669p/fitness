package com.gym.gymapp.ui.productListing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import com.google.gson.JsonObject
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentProductListBinding
import com.gym.gymapp.ui.productListing.adapter.FilterCategoryAdapter
import com.gym.gymapp.ui.productListing.adapter.FilterOrganisationAdapter
import com.gym.gymapp.ui.productListing.adapter.FilterSubCategoryAdapter
import com.gym.gymapp.ui.productListing.adapter.PackageListAdapter
import com.gym.gymapp.ui.productListing.model.FilterCategoryData
import com.gym.gymapp.ui.productListing.model.FilterSubCategoryData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ProductListFragment : Fragment(R.layout.fragment_product_list), View.OnClickListener {

    private val filterSubCatListData: MutableList<FilterSubCategoryData> = ArrayList()
    private lateinit var categoryFiltersListData: List<FilterCategoryData>
    private val filterViewModel: FilterViewModel by viewModels()

    @Inject
    lateinit var filterCategoryAdapter: FilterCategoryAdapter

    @Inject
    lateinit var filterSubCategoryAdapter: FilterSubCategoryAdapter

    @Inject
    lateinit var filterOrganisationAdapter: FilterOrganisationAdapter


    private var isOrganisation: String = "No"

    private var organisationId: String = ""

    private lateinit var binding: FragmentProductListBinding

    private val packageViewModel: PackageViewModel by viewModels()

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var packageListAdapter: PackageListAdapter

    private var currentView: View? = null
    var categoryId: String = ""
    var sortSelectedId: Int = 0
    var pageNo = 1

    @Inject
    lateinit var session: SessionManager
    private var vendorFilters: String = ""
    private var categoryFilters: String = ""
    private var subCategoryFilters: String = ""
    private var organizationFilters: String = ""
    private var filterType: String = ""
    private var sortPrice = ""
    private var searchKey = ""
    private var serviceProviderId = ""

    private var vendorFiltersList: MutableList<String> = ArrayList()
    private var categoryFiltersList: MutableList<String> = ArrayList()
    private var subCategoryFiltersList: MutableList<String> = ArrayList()
    private var organizationFiltersList: MutableList<String> = ArrayList()
    private var locationRange = "0.0"
    private var locationRangeFilter = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_product_list, container, false)
            binding = FragmentProductListBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
            setRecyclerView()
        }
        setCartCountObserver()

        return currentView!!
    }

    private fun setCartCountObserver() {
        binding.cartCount.text = session.cartCount.toString()

    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        filterViewModel.getFilterCategory()
        filterSubCategoryAdapter.differ.submitList(filterSubCatListData)

        sortSelectedId = 2131362586
        serviceProviderId = arguments?.getString("serviceProviderId") ?: ""
        searchKey = arguments?.getString("search") ?: ""
        categoryId = arguments?.getString("id") ?: ""
        organisationId = arguments?.getString("organisationId") ?: ""
        isOrganisation =
            if (arguments?.getString("from").toString() == "organisation") "Yes" else "No"
        categoryFilters = arguments?.getString("filterCategories") ?: ""
        subCategoryFilters = arguments?.getString("filterSubCategories") ?: ""
        filterType = arguments?.getString("filterType") ?: ""

        callProductListApi()

        if (isOrganisation == "No") {
            filterViewModel.getFilterOrganisation(categoryId)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            callProductListApi()
        }

        organizationFiltersList.add(organisationId)
        categoryFiltersList.add(categoryId)

    }

    private fun callProductListApi() {
        val params: MutableMap<String, String> = HashMap()
        params["page"] = pageNo.toString()
        params["search_key"] = searchKey
        params["category_id"] = categoryId
        params["sub_category_id"] = subCategoryFilters
        params["vendor_id"] = vendorFilters
        params["organization_id"] = organisationId
        params["is_organization"] = isOrganisation
        params["price_sorting"] = sortPrice
        params["name_sorting"] = ""
        params["filter_type"] = filterType
        params["location_range"] = locationRangeFilter.toString()
        params["device_latitude"] = session.userCurrentLat.toString()
        params["device_longitude"] = session.userCurrentLong.toString()
        packageViewModel.getPackageList(params)
    }

    private fun setObserver() {
        packageViewModel.packageList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing = false

                    if (pageNo == 1 && response.data!!.isEmpty()) {
                        binding.noProductsLbl.isVisible = true
                        binding.packageRv.isVisible = false
                        packageListAdapter.differ.submitList(response.data)
                        appProgress.dismiss()
                    } else {
                        binding.noProductsLbl.isVisible = false
                        isLoading = false
                        if (response.data!!.isNotEmpty()) {
                            binding.packageRv.isVisible = true
                            setRecyclerView()
                            packageListAdapter.differ.submitList(response.data)
                        }
                        appProgress.dismiss()
                        if (pageNo == 1) {
                            packageListAdapter.notifyDataSetChanged()
                        }
                    }
                }

                is Resource.Loading -> {

                    if (!binding.swipeToRefresh.isRefreshing) {
                        appProgress.show()
                    }
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }


        filterViewModel.filterCategory.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.data!!.isNotEmpty()) {

                        filterCategoryAdapter.differ.submitList(response.data.data)
                        categoryFiltersListData = response.data.data
                        categoryFiltersListData.forEach {
                            if (it.id.toString() == categoryId) {
                                filterSubCategoryAdapter.differ.submitList(it.sub_category)
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }
        }

        filterViewModel.filterOrganisation.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.data!!.isNotEmpty()) {
                        filterOrganisationAdapter.differ.submitList(response.data.data)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@ProductListFragment)
            sort.setOnClickListener(this@ProductListFragment)
            filter.setOnClickListener(this@ProductListFragment)
            favourite.setOnClickListener(this@ProductListFragment)
            search.setOnClickListener(this@ProductListFragment)
            cart.setOnClickListener(this@ProductListFragment)


            filterCategoryAdapter.setOnFilterSelectListener { it, status ->
                if (status) {
                    if (!categoryFiltersList.contains(it.toString()))
                        categoryFiltersList.add(it.toString())

                    categoryFiltersListData.forEach { catFilter ->
                        if (catFilter.id.toString() == it.toString()) {
                            filterSubCatListData.addAll(catFilter.sub_category)
                            filterSubCategoryAdapter.differ.submitList(filterSubCatListData)
                            filterSubCategoryAdapter.notifyDataSetChanged()
                        }
                    }

                } else {
                    categoryFiltersList.remove(it.toString())
                    categoryFiltersListData.forEach { catFilter ->
                        if (catFilter.id.toString() == it.toString()) {
                            filterSubCatListData.removeAll(catFilter.sub_category)
                            filterSubCategoryAdapter.differ.submitList(filterSubCatListData)
                            filterSubCategoryAdapter.notifyDataSetChanged()
                            if (filterSubCatListData.isEmpty()) subCategoryFiltersList.clear()
                        }
                    }
                }
            }

            filterSubCategoryAdapter.setOnFilterSelectListener { it, status ->
                if (status) {
                    if (!subCategoryFiltersList.contains(it.toString()))
                        subCategoryFiltersList.add(it.toString())
                } else {
                    subCategoryFiltersList.remove(it.toString())
                }
            }


            filterOrganisationAdapter.setOnFilterSelectListener { it, status ->
                if (status) {
                    if (!organizationFiltersList.contains(it.toString()))
                        organizationFiltersList.add(it.toString())
                } else {
                    organizationFiltersList.remove(it.toString())
                }
            }

        }

        packageListAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }

            findNavController().navigate(
                R.id.productDetailsFragment,
                bundle
            )
        }
    }

    private fun setRecyclerView() {
        binding.packageRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = packageListAdapter
        }.addOnScrollListener(scrollListener)
    }


    var isLoading = false
    var isScrolling = false


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val recycleLayoutManager = binding.packageRv.layoutManager as LinearLayoutManager
                if (!isLoading) {
                    if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == packageListAdapter.itemCount - 1) {
                        pageNo++
                        callProductListApi()
                        isLoading = true
                    }
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.sort -> {
                showBottomSheetForSort()
            }
            R.id.filter -> {
                /*val bundle = Bundle().apply {
                    putString("id", categoryId)
                    putString("serviceProviderId", serviceProviderId)
                }
                findNavController().navigate(
                    R.id.action_packageListFragment_to_packageFilterFragment,
                    bundle
                )*/

                showBottomSheetForFilter()

            }
            R.id.favourite -> {
                findNavController().navigate(R.id.action_packageListFragment_to_wishListFragment)


            }
            R.id.search -> {
                findNavController().navigate(R.id.searchFragment)
            }
            R.id.cart -> {
                findNavController().navigate(R.id.action_packageListFragment_to_cartFragment)
            }
        }
    }

    private fun showBottomSheetForSort() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_sort)
        val sortRadioGroup = bottomSheetDialog.findViewById<RadioGroup>(R.id.sortRadioGroup)
        sortRadioGroup?.check(sortSelectedId)
        sortRadioGroup?.setOnCheckedChangeListener { p0, _ ->
            println(p0.checkedRadioButtonId)
            sortSelectedId = p0.checkedRadioButtonId
            val selectedRadioButton = bottomSheetDialog.findViewById<RadioButton>(sortSelectedId)
            pageNo = 1
            if (selectedRadioButton?.text == getString(R.string.a_to_z)) {
                sortPrice = "ASC"
                callProductListApi()
            } else {
                sortPrice = "DESC"
                callProductListApi()
            }

            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showBottomSheetForFilter() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter)
        val organisationRv = bottomSheetDialog.findViewById<RecyclerView>(R.id.organisationList)
        val categoryFilterRv =
            bottomSheetDialog.findViewById<RecyclerView>(R.id.categoriesFilterList)
        val subCategoryFilterRv =
            bottomSheetDialog.findViewById<RecyclerView>(R.id.subCategoriesFilterList)

        val rangeSlider = bottomSheetDialog.findViewById<RangeSlider>(R.id.rangeSlider)
        val applyFilters = bottomSheetDialog.findViewById<MaterialButton>(R.id.apply)
        val distance = bottomSheetDialog.findViewById<TextView>(R.id.distance)
        val closeIcon = bottomSheetDialog.findViewById<ImageView>(R.id.closeIcon)


        val organisationView = bottomSheetDialog.findViewById<Group>(R.id.organisationView)
        val categoriesView = bottomSheetDialog.findViewById<Group>(R.id.categoriesView)

        closeIcon?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        organisationView?.isVisible = isOrganisation == "No"
        categoriesView?.isVisible = isOrganisation == "No"

        distance?.text = "$locationRange KM"
        rangeSlider?.setValues(locationRange.toFloat())
        rangeSlider?.setLabelFormatter { value ->
            locationRangeFilter = value.toDouble()
            locationRange = value.toString()
            distance?.text = "$value KM"
            "$value KM"
        }


        organisationRv?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = filterOrganisationAdapter

        }

        categoryFilterRv?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = filterCategoryAdapter

        }


        subCategoryFilterRv?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = filterSubCategoryAdapter
        }

        applyFilters?.setOnClickListener {
            applyFilter()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun applyFilter() {
        pageNo = 1
        categoryId = java.lang.String.join(",", categoryFiltersList)
        vendorFilters = java.lang.String.join(",", vendorFiltersList)
        subCategoryFilters = java.lang.String.join(",", subCategoryFiltersList)
        organisationId = java.lang.String.join(",", organizationFiltersList)

        callProductListApi()
    }
}

