package com.gym.gymapp.ui.organisationsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentOrganisationListBinding
import com.gym.gymapp.databinding.FragmentStarIconBinding
import com.gym.gymapp.ui.ActivePacks.adapter.ActivePacksAdapter
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.organisationsList.adapter.CitiesFilterAdapter
import com.gym.gymapp.ui.organisationsList.adapter.OrganisationListAdapter
import com.gym.gymapp.ui.organisationsList.model.FilterCitiesData
import com.gym.gymapp.ui.organisationsList.model.OrganisationData
import com.gym.gymapp.ui.starIcon.model.StarIconData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrganisationListFragment : Fragment(R.layout.fragment_organisation_list), View.OnClickListener {
    lateinit var binding: FragmentOrganisationListBinding

    private var currentView: View? = null

    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var organisationListAdapter: OrganisationListAdapter

    @Inject
    lateinit var citiesFilterAdapter: CitiesFilterAdapter

    private val organisationData: MutableList<OrganisationData> = ArrayList()

    var pageNo = 1

    var categoryId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_organisation_list, container, false)
            binding = FragmentOrganisationListBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        appProgress = AgileLoader(requireContext())
        categoryId = arguments?.getString("id")?.toInt() ?: 0
        val params: MutableMap<String, String> = HashMap()
        params["page"] = pageNo.toString()
        params["category_id"] = categoryId.toString()
        commonViewModel.getOrganisationList(params)
        commonViewModel.getFilterCities()

        binding.swipeToRefresh.setOnRefreshListener {
            commonViewModel.getOrganisationList(params)
            commonViewModel.getFilterCities()
            setObserver()
        }

        binding.cartCount.text = session.cartCount.toString()

    }

    private fun setObserver() {

        commonViewModel.organisationList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    binding.swipeToRefresh.isRefreshing = false
                    if (response.data!!.success) {

                        if (response.data.data.isEmpty()) {
                            if (pageNo == 1) {
                                binding.noOrganisationsLbl.isVisible = true
                                binding.organisationRv.isVisible = false
                                organisationData.clear()
                            }

                        } else {
                            binding.noOrganisationsLbl.isVisible = false
                            binding.organisationRv.isVisible = true
                            if (pageNo == 1) {
                                organisationData.clear()
                                organisationData.addAll(response.data.data)
                                organisationListAdapter.differ.submitList(organisationData)
                                setOrganisationListData()
                            } else {
                                organisationData.addAll(response.data.data)
                                organisationListAdapter.differ.submitList(organisationData)
                            }
                        }
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing) {
                        appProgress.show()
                    }
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    appProgress.dismiss()
                }
            }
        }

        commonViewModel.filterCities.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    binding.swipeToRefresh.isRefreshing = false
                    if (response.data!!.success) {
                        setCitiesRv(response.data.data)

                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing) {
                        appProgress.show()
                    }
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    appProgress.dismiss()
                }
            }
        }
    }


    private fun setCitiesRv(data: List<FilterCitiesData>) {
        citiesFilterAdapter.differ.submitList(data)
        binding.cityFiltersRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = citiesFilterAdapter
        }
    }

    private fun setOrganisationListData() {
        binding.organisationRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = organisationListAdapter
        }.addOnScrollListener(scrollListener)
    }

    var isLoading = false
    var isScrolling = false


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val recycleLayoutManager =
                    binding.organisationRv.layoutManager as LinearLayoutManager
                if (!isLoading) {
                    if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == organisationListAdapter.itemCount - 1) {
                        pageNo++
                        val params: MutableMap<String, String> = HashMap()
                        params["page"] = pageNo.toString()
                        params["category_id"] = categoryId.toString()
                        commonViewModel.getOrganisationList(params)
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

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@OrganisationListFragment)
        binding.favourite.setOnClickListener(this@OrganisationListFragment)
        binding.search.setOnClickListener(this@OrganisationListFragment)
        binding.cart.setOnClickListener(this@OrganisationListFragment)
        binding.cityImage.setOnClickListener(this@OrganisationListFragment)
        organisationListAdapter.setOnOrganisationClickListener {

            val bundle = Bundle().apply {
                putString("organisationId", it.id.toString())
                putString("id", it.category_id)
                putString("from", "organisation")
            }

            findNavController().navigate(R.id.packageListFragment, bundle)
        }

        citiesFilterAdapter.setOnCityClickListener {
            pageNo=1
            val params: MutableMap<String, String> = HashMap()
            params["page"] = pageNo.toString()
            params["City_id"] = it.city_id.toString()
            params["category_id"] = categoryId.toString()
            params["user_latitude"] = ""
            params["user_longitude"] = ""
            commonViewModel.getOrganisationList(params)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }

            R.id.favourite -> {
                findNavController().navigate(R.id.wishListFragment)
            }

            R.id.search -> {
                findNavController().navigate(R.id.searchFragment)
            }

            R.id.cart -> {
                findNavController().navigate(R.id.cartFragment)
            }

            R.id.cityImage -> {
                pageNo = 1
                val params: MutableMap<String, String> = HashMap()
                params["page"] = pageNo.toString()
                params["City_id"] = ""
                params["category_id"] = categoryId.toString()
                params["user_latitude"] = session.userCurrentLat.toString()
                params["user_longitude"] = session.userCurrentLong.toString()
                commonViewModel.getOrganisationList(params)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cartCount.text = session.cartCount.toString()
        setObserver()
    }
}