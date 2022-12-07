package com.gym.gymapp.ui.homeScreens.homeFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentHomeBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.ui.homeScreens.homeFragment.adapter.HomeVendorListAdapter
import com.gym.gymapp.ui.homeScreens.homeFragment.adapter.PackageListAdapter
import com.gym.gymapp.ui.homeScreens.homeFragment.adapter.ProductListAdapter
import com.gym.gymapp.ui.homeScreens.homeFragment.adapter.SliderAdapter
import com.gym.gymapp.ui.loginSignUp.LoginSignUp
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {

    private var notificationCount: Int = 0
    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var sliderAdapter: SliderAdapter

    @Inject
    lateinit var packageListAdapter: PackageListAdapter

    @Inject
    lateinit var newProductListAdapter: ProductListAdapter

    @Inject
    lateinit var trendingProductListAdapter: ProductListAdapter

    @Inject
    lateinit var vendorProductListAdapter: HomeVendorListAdapter

    lateinit var agileLoader: AgileLoader

    private val commonViewModel: CommonViewModel by viewModels()

    private var currentView: View? = null

    private var trendingServiceId = ""
    private var newServiceProviderId = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_home, container, false)
            binding = FragmentHomeBinding.bind(currentView!!)
            init()
            setRecyclerView()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    @SuppressLint("CheckResult")
    private fun init() {
        agileLoader = AgileLoader(requireContext())
        setupCarousel()
        homeViewModel.getMainSliders()
        homeViewModel.getPackageList()
        homeViewModel.getNewProducts()
        homeViewModel.getTrendingProducts()
        homeViewModel.getVendorList()
        commonViewModel.getNotifications()
        val requestOptions = RequestOptions().apply {
            placeholder(R.drawable.user_icon)
            error(R.drawable.user_icon)
            diskCacheStrategy(DiskCacheStrategy.ALL)
        }

        Glide.with(requireContext()).setDefaultRequestOptions(requestOptions)
            .load(session.loginImage).into(binding.profile)


        binding.swipeToRefresh.setOnRefreshListener {
            homeViewModel.getMainSliders()
            homeViewModel.getPackageList()
            homeViewModel.getNewProducts()
            homeViewModel.getTrendingProducts()
            homeViewModel.getVendorList()
            commonViewModel.getNotifications()
        }

    }


    private fun setObserver() {

        homeViewModel.slider.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing=false
                    if (response.data?.success!!) {
                        sliderAdapter.differ.submitList(response.data.data)
                        binding.mainBanner.adapter = sliderAdapter
                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing)
                    agileLoader.show()
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                    agileLoader.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }

        homeViewModel.selectPackageList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing=false
                    if (response.data?.success!!) {

                        packageListAdapter.differ.submitList(response.data.data)
                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                    requireContext().showToast(response.message.toString())
                }
            }
        }


        homeViewModel.trendingProduct.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data?.success!!) {
                        binding.swipeToRefresh.isRefreshing=false
                        if (response.data.data.isEmpty()) {

                            binding.trendingProductsLbl.isVisible = false
                            binding.trendingProductsViewAll.isVisible = false
                            binding.trendingProductsRv.isVisible = false
                        } else {
                            trendingServiceId = response.data.data[0].category_id.toString()
                            binding.trendingProductsLbl.isVisible = true
                            binding.trendingProductsViewAll.isVisible = true
                            binding.trendingProductsRv.isVisible = true
                            trendingProductListAdapter.differ.submitList(response.data.data)

                            binding.trendingProductsRv.apply {
                                setHasFixedSize(true)
                                layoutManager =
                                    LinearLayoutManager(
                                        requireContext(),
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                adapter = trendingProductListAdapter
                            }
                        }


                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                    requireContext().showToast(response.message.toString())
                }
            }
        }
        homeViewModel.newProduct.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing=false
                    if (response.data?.success!!) {
                        if (response.data.data.isEmpty()) {
                            binding.newProductsLbl.isVisible = false
                            binding.newProductsViewAll.isVisible = false
                            binding.newProductsRv.isVisible = false
                        } else {
                            newServiceProviderId  = response.data.data[0].category_id.toString()
                            binding.newProductsLbl.isVisible = true
                            binding.newProductsViewAll.isVisible = true
                            binding.newProductsRv.isVisible = true

                            newProductListAdapter.differ.submitList(response.data.data)
                            binding.newProductsRv.apply {
                                setHasFixedSize(true)
                                layoutManager =
                                    LinearLayoutManager(
                                        requireContext(),
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                adapter = newProductListAdapter
                            }
                        }


                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                    requireContext().showToast(response.message.toString())
                }
            }
        }
        homeViewModel.vendorsList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing=false
                    agileLoader.dismiss()
                    if (response.data?.success!!) {
                        if (response.data.data.isNotEmpty()) {
                            binding.vendorLbl.isVisible = true
                            binding.vendorViewAll.isVisible = true
                            binding.vendorsRv.isVisible = true
                            vendorProductListAdapter.differ.submitList(response.data.data)
                            binding.vendorsRv.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                adapter = vendorProductListAdapter
                            }

                        } else {
                            binding.vendorLbl.isVisible = false
                            binding.vendorViewAll.isVisible = false
                            binding.vendorsRv.isVisible = false
                        }
                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                    agileLoader.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }

        commonViewModel.notifications.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing=false
                    agileLoader.dismiss()
                    if (response.data?.success!!) {
                        binding.notificationBadge.text = response.data.total_new_notification.toString()
                    }
                }
                is Resource.Loading -> { }

                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing=false
                }
            }
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            profile.setOnClickListener(this@HomeFragment)
            cart.setOnClickListener(this@HomeFragment)
            wishList.setOnClickListener(this@HomeFragment)
            navIcon.setOnClickListener(this@HomeFragment)
            newProductsViewAll.setOnClickListener(this@HomeFragment)
            trendingProductsViewAll.setOnClickListener(this@HomeFragment)
            vendorViewAll.setOnClickListener(this@HomeFragment)
            notification.setOnClickListener(this@HomeFragment)
            searchEdt.setOnClickListener(this@HomeFragment)

            packageListAdapter.setOnPackageClickListener { packageId ->
                val bundle = Bundle().apply {
                    putString("id", packageId.toString())
                }
//                findNavController().navigate(R.id.action_homeFragment_to_packageListFragment, bundle)
                findNavController().navigate(R.id.organisationListFragment, bundle)
            }

            trendingProductListAdapter.setOnPackageClickListener { packageId ->
                val bundle = Bundle().apply {
                    putString("id", packageId.toString())
                }
                findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
            }

            newProductListAdapter.setOnPackageClickListener { packageId ->
                val bundle = Bundle().apply {
                    putString("id", packageId.toString())
                }
                findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
//                findNavController().navigate(R.id.organisationListFragment, bundle)
            }

            vendorProductListAdapter.setOnVendorClickListener { vendor ->
                val bundle = Bundle().apply {
                    putString("id", vendor.toString())
                }

                findNavController().navigate(R.id.vendorDetailsFragment, bundle)
//                findNavController().navigate(R.id.organisationListFragment, bundle)
            }
        }
    }

    private fun setRecyclerView() {
        binding.packagesRv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = packageListAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile -> {
                if (session.isLogin!!) {
                    findNavController().navigate(R.id.accountFragment)
                } else {
                    startActivity(Intent(requireActivity(), LoginSignUp::class.java))
                    requireActivity().finish()
                }
            }
            R.id.cart -> {
                findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
            }
            R.id.searchEdt -> {
                findNavController().navigate(R.id.searchFragment)
            }
            R.id.navIcon -> {
                (activity as MainActivity).openCloseDrawer()
            }

            R.id.notification -> {
                findNavController().navigate(R.id.notificationFragment)
            }

            R.id.wishList -> {
                findNavController().navigate(R.id.action_homeFragment_to_wishListFragment)
            }

            R.id.newProductsViewAll -> {
                val bundle = Bundle().apply {
                    putString("serviceProviderId", newServiceProviderId)
                    putString("filterType", "new_package")
                }

                findNavController().navigate(R.id.packageListFragment, bundle)

            }

            R.id.trendingProductsViewAll -> {
                val bundle = Bundle().apply {
                    putString("serviceProviderId", trendingServiceId)
                    putString("filterType", "top_tranding")
                }
                findNavController().navigate(R.id.packageListFragment, bundle)

            }

            R.id.vendorViewAll -> {
                findNavController().navigate(R.id.action_homeFragment_to_vendorListFragment)
            }
        }
    }

    private fun setupCarousel() {

        binding.mainBanner.offscreenPageLimit = 1

        val nextItemVisiblePx = 26
        val currentItemHorizontalMarginPx = 42
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }

        binding.mainBanner.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            42
        )
        binding.mainBanner.addItemDecoration(itemDecoration)

    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(requireActivity())
        binding.cartCount.text = session.cartCount.toString()
    }
}