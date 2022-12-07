package com.gym.gymapp.ui.productDetails

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentProductDetailsBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.productDetails.adapter.PackageDurationAdapter
import com.gym.gymapp.ui.productDetails.adapter.ProductImageAdapter
import com.gym.gymapp.ui.productDetails.adapter.RelatedProductAdapter
import com.gym.gymapp.ui.productDetails.adapter.TimeDurationAdapter
import com.gym.gymapp.ui.productDetails.model.PackageDetailsData
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToLong


@AndroidEntryPoint
class ProductDetailsFragment : Fragment(R.layout.fragment_product_details), View.OnClickListener {

    private lateinit var binding: FragmentProductDetailsBinding
    private var currentView: View? = null
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()
    private val commonViewModel: CommonViewModel by viewModels()

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var relatedProductAdapter: RelatedProductAdapter

    @Inject
    lateinit var productImageAdapter: ProductImageAdapter

    @Inject
    lateinit var packageDurationAdapter: PackageDurationAdapter

    @Inject
    lateinit var timeDurationAdapter: TimeDurationAdapter

    @Inject
    lateinit var session: SessionManager

    private var packageData: PackageDetailsData = PackageDetailsData()

    private var packageId = ""

    private var productInWishList = false

    private var productInCart = false

    private var packageDurationPosition = 0
    private var packageTimePosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_product_details, container, false)
            binding = FragmentProductDetailsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        checkProductAddedInCart()

        return currentView!!
    }

    private fun checkProductAddedInCart() {
        productDetailsViewModel.isProductInWishList(packageId.toInt())
        productDetailsViewModel.isProductInWishList.observe(viewLifecycleOwner) {
            productInWishList = it
            setFavouriteIcon()
        }
    }

    private fun setFavouriteIcon() {
        if (productInWishList) {
            binding.favouritePackage.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.favouritePackage.setImageResource(R.drawable.ic_favorite)

            binding.favouritePackage.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.themeOrange
                )
            )
        }
    }

    private fun setRecyclerView() {
        binding.relatedProductRv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = relatedProductAdapter
        }
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        packageId = arguments?.getString("id").toString()

        if (packageId.isNotEmpty()) {
            productDetailsViewModel.getProductDetails(packageId, session.deviceId.toString())
            productDetailsViewModel.getRelatedProducts(packageId)
        }
    }

    private fun setObserver() {

        productDetailsViewModel.productDetail.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    packageData = response.data?.data!![0]
                    setProductData(packageData)
                }
                is Resource.Loading -> {
                    binding.loadingView.isVisible = true
                    appProgress.show()
                }
                is Resource.Error -> {
                    binding.loadingView.isVisible = false
                    requireContext().showToast(response.message.toString())
                }
            }
        }

        productDetailsViewModel.addToCart.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        appProgress.dismiss()
                        if (response.data!!.success) {
                            requireContext().showToast(response.data.message)
                            productInCart = true
                            binding.addToCartLbl.text =
                                if (productInCart) "Go To Cart" else "Add To Cart"
                            session.cartCount = session.cartCount!! + 1
                        } else {
                            requireContext().showToast(response.data.message)
                        }
                    }

                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    requireContext().showToast(response.message.toString())
                }
            }
        }

        productDetailsViewModel.relatedProducts.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    binding.loadingView.isVisible = false

                    if (response.data!!.data.isNotEmpty()) {
                        binding.noRelatedProducts.isVisible = false
                        relatedProductAdapter.differ.submitList(response.data.data)
                        setRecyclerView()
                    } else {
                        binding.noRelatedProducts.isVisible = true
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }
    }

    private fun setProductData(packageData: PackageDetailsData) {
        with(binding)
        {
            productImageAdapter.differ.submitList(packageData.package_gallry)
            packageImages.adapter = productImageAdapter
            packageName.text = packageData.name
            productInCart = packageData.cart_status
            binding.addToCartLbl.text = if (productInCart) "Go To Cart" else "Add To Cart"
//            binding.packageAddress.text = packageData.c
            if (!packageData.priceduration.isNullOrEmpty()) {
                packageData.priceduration!!.forEach {
                    if (it.status) {
                        packagePrice.text = "₹ ${it.price}/-"
                    }
                }

                packageDurationAdapter.differ.submitList(packageData.priceduration)
                binding.selectPackageMonthRv.apply {
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(requireContext(), 3)
                    adapter = packageDurationAdapter
                }
            }
            if (!packageData.package_start_time.isNullOrEmpty()) {
                binding.selectpackageTimeRv.isVisible = true
                binding.packageTime.isVisible = true
                timeDurationAdapter.differ.submitList(packageData.package_start_time)
                binding.selectpackageTimeRv.apply {
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(requireContext(), 3)
                    adapter = timeDurationAdapter
                }
            } else {
                binding.selectpackageTimeRv.isVisible = false
                binding.packageTime.isVisible = false
            }

            packageDesc.text = packageData.description
            vendorName.text = packageData.vendor_name
            vendorEmail.text = packageData.vendor_email


            val results = FloatArray(1)
            println(
                "Got Locations yeah ${
                    Location.distanceBetween(
                        session.userCurrentLat!!.toDouble(),
                        session.userCurrentLong!!.toDouble(),
                        packageData.latitude!!.toDouble(),
                        packageData.longitude!!.toDouble(),
                        results
                    )
                }"
            )
            packageLocationDistance.text = "${(results[0] * 0.001).roundToLong()} KM"

            val params:MutableMap<String,String> = HashMap()
            params["destinations"] = "${packageData.latitude},${packageData.longitude}"
            params["origins"] = "${session.userCurrentLat},${session.userCurrentLong!!.toDouble()}"
            params["key"] = DISTANCE_MATRIX_KEY
            commonViewModel.getDistance(params)
            commonViewModel.distance.observe(viewLifecycleOwner){response->
                when(response){
                    is Resource.Success->{
                        if(response.data?.status=="OK"){
                            packageLocationDistance.isVisible = true
                        packageLocationDistance.text = response.data.rows[0].elements[0].distance.text
                        }
                    }
                    is Resource.Loading->{

                    }
                    is Resource.Error->{}
                }
            }




            Glide.with(requireContext()).applyDefaultRequestOptions(requestOption())
                .load(packageData.vendor_profile).into(vendorImage)
        }

        TabLayoutMediator(binding.tabLayout, binding.packageImages) { _, _ -> }.attach()
    }

    private fun setOnClickListener() {
        with(binding) {
            favouritePackage.setOnClickListener(this@ProductDetailsFragment)
            backArrow.setOnClickListener(this@ProductDetailsFragment)
            sharePackage.setOnClickListener(this@ProductDetailsFragment)
            addToCart.setOnClickListener(this@ProductDetailsFragment)
            packageLocationDistance.setOnClickListener(this@ProductDetailsFragment)
            serviceProviderGroup.setOnClickListener(this@ProductDetailsFragment)
        }

        packageDurationAdapter.setOnPackageDurationClickListener { position ->
            packageDurationPosition = position
            binding.packagePrice.text = "₹ ${packageData.priceduration!![position].price}/-"
        }

        timeDurationAdapter.setOnPackageDurationClickListener { position ->
            packageTimePosition = position
        }
        relatedProductAdapter.setOnPackageListener {packageId->
            val bundle = Bundle().apply {
                putString("id", packageId.toString())
            }
            findNavController().popBackStack()
            findNavController().navigate(R.id.productDetailsFragment, bundle)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.favouritePackage -> {
                val wishListProductData = packageData.vendor_phone_number?.let {
                    packageData.vendor_profile?.let { it1 ->
                        WishListPackageData(
                            id = packageData.id,
                            category_id = packageData.category_id,
                            created_at = packageData.created_at,
                            description = packageData.description,
                            name = packageData.name,
                            package_gallry = packageData.package_gallry!!,
                            priceduration = packageData.priceduration!!,
                            service_provider_id = packageData.service_provider_id,
                            slug = packageData.slug,
                            sub_category_id = packageData.sub_category_id,
                            thumbnail = packageData.thumbnail,
                            vendor_email = packageData.vendor_email,
                            vendor_name = packageData.vendor_name,
                            vendor_phone_number = it,
                            vendor_profile = it1,
                        )
                    }
                }
                if (productInWishList) {
                    productInWishList = !productInWishList
                    lifecycleScope.launch {
                        appProgress.show()
                        delay(600)
                        appProgress.dismiss()
                        productDetailsViewModel.removeFromWishList(packageId.toInt())
                        requireContext().showToast("Removed From WishList")
                    }

                } else {
                    productInWishList = !productInWishList
                    lifecycleScope.launch {
                        appProgress.show()
                        delay(600)
                        appProgress.dismiss()
                        wishListProductData?.let { productDetailsViewModel.addToWishList(packageData = it) }
                        requireContext().showToast("Added in WishList")
                    }
                }
                setFavouriteIcon()

            }
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.sharePackage -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.addToCart -> {
                if (productInCart) {
                    findNavController().navigate(R.id.action_productDetailsFragment_to_cartFragment)
                } else {
                    val params: MutableMap<String, String> = HashMap()
                    params["user_id"] = session.loginId.toString()
                    params["device_id"] = session.deviceId!!
                    params["package_id"] = packageData.id.toString()
                    if (packageData.package_start_time?.isNotEmpty()  == true) {
                        params["package_strat_end_time"] =
                            "${packageData.package_start_time?.get(packageTimePosition)?.start_time}-${
                                packageData.package_start_time?.get(packageTimePosition)?.end_time
                            }"
                    }
                    params["duration"] = if (packageData.priceduration!!.isNotEmpty())
                        packageData.priceduration!![packageDurationPosition].month
                    else ""
                    params["package_price"] = if (packageData.priceduration!!.isNotEmpty())
                        packageData.priceduration!![packageDurationPosition].price
                    else ""

                    productDetailsViewModel.addToCart(params)
                }
            }

            R.id.packageLocationDistance -> {
                val latitude = packageData.latitude
                val longitude = packageData.longitude
                val uri =
                    "http://maps.google.com/maps?saddr=${session.userCurrentLat},${session.userCurrentLong}&daddr=$latitude,$longitude"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                requireActivity().startActivity(intent)
            }

            R.id.serviceProviderGroup -> {
                val bundle = Bundle().apply {
                    putString("id", packageData.service_provider_id)
                }

                findNavController().navigate(R.id.vendorDetailsFragment, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }
}