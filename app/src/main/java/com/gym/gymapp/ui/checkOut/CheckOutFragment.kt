package com.gym.gymapp.ui.checkOut

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.cart.CartViewModel
import com.gym.gymapp.cart.adapter.CartAdapter
import com.gym.gymapp.cart.model.GetCartData
import com.gym.gymapp.databinding.FragmentCheckOutBinding
import com.gym.gymapp.ui.address.AddressViewModel
import com.gym.gymapp.ui.address.model.AddressData
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.utils.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class CheckOutFragment : Fragment(R.layout.fragment_check_out), View.OnClickListener {

    private lateinit var binding: FragmentCheckOutBinding

    private var currentView: View? = null

    private val cartViewModel: CartViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()

    @Inject
    lateinit var cartAdapter: CartAdapter

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader

    private var cartIds = ""

    private var addressId = ""

    private var totalPrice = 0

    private var addressData: List<AddressData> = ArrayList()

    private var cartData: List<GetCartData> = ArrayList()

    private var addressIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_check_out, container, false)
            binding = FragmentCheckOutBinding.bind(currentView!!)
            init()
            setObservers()
            setOnClickListener()
        }
        return currentView
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())

        if (arguments?.getString("addressPosition") != null && arguments?.getString("addressPosition") != "") {
            addressIndex = arguments?.getString("addressPosition")!!.toInt()
        }

        cartViewModel.getCartData(session.loginId.toString(), session.deviceId.toString())
        addressViewModel.getAddress()

        with(binding) {
            placeOrderBtn.isVisible = false
            mainLayout.isVisible = false
        }
    }

    private fun setObservers() {
        cartViewModel.getCartData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.isLoading.isVisible = false

                    showViews(true)

                    appProgress.dismiss()
                    if (response.data!!.success) {
                        setCartData(response.data.data)
                    } else {
                        requireContext().showToast("Something went wrong!!")
                    }

                }
                is Resource.Loading -> {
                    appProgress.show()
                    showViews(false)
                    binding.isLoading.isVisible = true
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    showViews(false)
                    binding.isLoading.isVisible = false

                }
            }
        }

        addressViewModel.addressList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.isLoading.isVisible = false
                    showViews(true)
                    if (response.data!!.success) {
                        addressData = response.data.data
                        setAddressData(addressData, addressIndex)
                    } else {
                        requireContext().showToast("Something went wrong!!")
                    }

                }
                is Resource.Loading -> {
                    appProgress.show()
                    showViews(false)
                    binding.isLoading.isVisible = true
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    showViews(false)
                    binding.isLoading.isVisible = false
                    requireContext().showToast(response.message.toString())

                }
            }
        }
    }

    private fun setAddressData(data: List<AddressData>, i: Int) {
        if (data.isNotEmpty()) {
            binding.address.text =
                "${data[i].address}\n${data[i].city_name} ${data[i].state_name}, ${data[i].pincode},\nmobile: ${data[i].alternate_phone}"
        }
    }

    private fun setCartData(cartData: List<GetCartData>) {
        cartData.forEach {
            cartIds = if (cartIds.isEmpty()) {
                it.id.toString()
            } else {
                "$cartIds,${it.id}"
            }
        }
        cartAdapter.differ.submitList(cartData)
        cartData.forEach {
            totalPrice += it.package_price.toDouble().toInt()
        }

        binding.packageListRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
        binding.subTotal.text = "${totalPrice}/-"
        binding.couponAmt.text = "0/-"
        binding.totalAmount.text = "₹ ${totalPrice}/-"
        binding.userName.text = session.loginName
        binding.userEmail.text = session.loginEmail
        Glide.with(this).applyDefaultRequestOptions(requestOption())
            .load(session.loginImage).into(binding.userProfileImage)
    }

    private fun showViews(type: Boolean) {
        binding.placeOrderBtn.isVisible = type
        binding.mainLayout.isVisible = type
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@CheckOutFragment)
        binding.placeOrderBtn.setOnClickListener(this@CheckOutFragment)
        binding.editAddress.setOnClickListener(this@CheckOutFragment)
        binding.applyCoupon.setOnClickListener(this@CheckOutFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }

            R.id.placeOrderBtn -> {
                gotoPaymentGateway()
            }
            R.id.applyCoupon -> {
                applyCoupon()
            }

            R.id.editAddress -> {


                val bundle = Bundle().apply {
                    putString("from", "checkOut")
                }
                findNavController().navigate(R.id.action_checkOutFragment_to_addressListFragment, bundle)
//                startActivity(Intent(this@CheckOutActivity, MainActivity::class.java).putExtra("from","checkOut"))
            }
        }
    }

    private fun applyCoupon() {
        val params:MutableMap<String,String> = HashMap()
        params["user_id"] = session.loginId.toString()
        params["coupon_code"] = binding.couponEdt.text.toString()
        params["net_amount"] = totalPrice.toString()
        cartViewModel.applyCoupon(params)
        cartViewModel.coupon.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Success->{
                    appProgress.dismiss()
                    if(response.data!!.success){
                        requireContext().showToast(response.data.message)
                        binding.couponAmt.text = response.data.coupon_amount.toString().toDouble().roundToInt().toString()
                        binding.subTotal.text = response.data.sub_total.toString().toDouble().roundToInt().toString()
                        binding.totalAmount.text = response.data.paid_amount.toString().toDouble().roundToInt().toString()
                        totalPrice = response.data.paid_amount.toString().toDouble().roundToInt()
                    }else{
                        requireContext().showToast(response.data.message)
                    }

                }
                is Resource.Loading->{
                    appProgress.show()
                }
                is Resource.Error->{
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun gotoPaymentGateway() {
        if(addressData.isNotEmpty()){
            val checkout = Checkout()
            checkout.setKeyID(getString(R.string.razor_pay_api_key))
            val json = JSONObject()

            session.cartId = cartIds
            session.checkOutAmount = totalPrice.toString()
            session.checkOutAddressId = addressData[addressIndex].id.toString()

            try {
                json.put("name", "Gym Test")
                json.put("description", "Test payment")
                json.put("theme.color", "#e65419")
                json.put("currency", "INR")
                json.put("amount", "${totalPrice}00")
                json.put("prefill.contact", session.mobile)
                json.put("prefill.email", session.loginEmail)
                json.put("image", "https://images.unsplash.com/photo-1601923876380-7e5b12e09eeb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=880&q=80")

                // open razorpay to checkout activity
                checkout.open(requireActivity(), json)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }else{
            requireActivity().showToast("Please Select or Add Address")
        }
    }
}