package com.gym.gymapp.cart

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gym.gymapp.R
import com.gym.gymapp.cart.adapter.CartAdapter
import com.gym.gymapp.cart.model.GetCartData
import com.gym.gymapp.databinding.FragmentCartBinding
import com.gym.gymapp.ui.loginSignUp.LoginSignUp
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart), View.OnClickListener {

    private var cartItemPosition: Int = 0
    private lateinit var binding: FragmentCartBinding
    private var currentView: View? = null

    private var cartData:MutableList<GetCartData> = ArrayList()

    @Inject
    lateinit var cartAdapter: CartAdapter

    @Inject
    lateinit var session: SessionManager

    private val cartViewModel: CartViewModel by viewModels()

    @Inject
    lateinit var appProgressBar: AgileLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_cart, container, false)
            binding = FragmentCartBinding.bind(currentView!!)
            init()
            setObservers()
            setOnClickListener()
        }
        return currentView
    }

    private fun init() {
        appProgressBar = AgileLoader(requireContext())
        binding.progressView.isVisible = true
        val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        cartViewModel.getCartData(session.loginId.toString(), deviceId = deviceId)
        binding.swipeToRefresh.setOnRefreshListener {
            cartViewModel.getCartData(session.loginId.toString(), deviceId = deviceId)

        }

    }

    private fun setObservers() {

        cartViewModel.getCartData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing =false
                    appProgressBar.dismiss()
                    if (response.data!!.success) {
                        cartData = response.data.data as MutableList<GetCartData>
                        setCartData(response.data.data)
                    } else {
                        requireContext().showToast("Something went wrong, please try Again!")
                    }
                }

                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing)
                    appProgressBar.show()
                }

                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing =false
                    appProgressBar.dismiss()
                }
            }
        }


        cartViewModel.removeCartData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgressBar.dismiss()
                    if (response.data!!.success) {
                        cartData.removeAt(cartItemPosition)
                        setCartData(cartData)
                        session.cartCount = session.cartCount!!-1

                    } else {
                        requireContext().showToast("Something went wrong, please try Again!")
                    }
                }

                is Resource.Loading -> {
                    appProgressBar.show()
                }

                is Resource.Error -> {
                    appProgressBar.dismiss()
                }
            }
        }
    }

    private fun setCartData(cartData: List<GetCartData>) {

        binding.progressView.isVisible = false
        cartAdapter.differ.submitList(cartData)
        var totalPrice = 0
        if (cartData.isEmpty()) {
            binding.progressView.isVisible = true
            binding.noItemsInCart.isVisible = true
        } else {
            binding.noItemsInCart.isVisible = false
            cartData.forEach { packageDetails ->
                totalPrice += packageDetails.package_price.toDouble().toInt()
            }
            binding.cartValue.text = "₹ ${totalPrice}/-"

        }
        setCartRecycleView()
    }

    private fun setCartRecycleView(){
        binding.cartRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@CartFragment)
            checkOut.setOnClickListener(this@CartFragment)
        }


        cartAdapter.setOnProductListener {it
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }

            findNavController().navigate(R.id.productDetailsFragment, bundle)
        }


        cartAdapter.setOnRemoveProductListener {it,position->
            cartItemPosition = position
            showAlertOnRemove(it)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.checkOut -> {
                if (session.isLogin!!) {
                    findNavController().navigate(R.id.checkOutFragment)
//                    startActivity(Intent(requireActivity(), CheckOutActivity::class.java))
                } else {
                    showBottomSheetForLogin()
                }
            }
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun showAlertOnRemove(id: Int) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure want remove package?")
        alertDialog.setCancelable(true)

        alertDialog.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            cartViewModel.removeProductFromCart(id)
            dialog.cancel()
        }

        alertDialog.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11: AlertDialog = alertDialog.create()
        alert11.show()
    }

    private fun showBottomSheetForLogin() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login_signup)
        val loginBtn = bottomSheetDialog.findViewById<Button>(R.id.loginBtn)
        val signUpBtn = bottomSheetDialog.findViewById<Button>(R.id.signUpBtn)
        loginBtn?.setOnClickListener {
            startActivity(
                Intent(requireActivity(), LoginSignUp::class.java).putExtra(
                    "from",
                    "Login"
                )
            )
        }
        signUpBtn?.setOnClickListener {
            startActivity(
                Intent(requireActivity(), LoginSignUp::class.java).putExtra(
                    "from",
                    "SignUp"
                )
            )
        }
        bottomSheetDialog.show()
    }

}