package com.gym.gymapp.ui.address

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentAddressListBinding
import com.gym.gymapp.databinding.FragmentBlogDetailsBinding
import com.gym.gymapp.ui.address.adapter.AddressListAdapter
import com.gym.gymapp.ui.address.model.AddressData
import com.gym.gymapp.ui.homeScreens.blogScreens.BlogsViewModel
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsDetailsData
import com.gym.gymapp.ui.vendors.adapter.VendorPagerAdapter
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddressListFragment : Fragment(R.layout.fragment_address_list), View.OnClickListener {
    private lateinit var binding: FragmentAddressListBinding

    private var currentView: View? = null

    private val addressViewModel: AddressViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager


    lateinit var agileLoader: AgileLoader

    @Inject
    lateinit var addressAdapter: AddressListAdapter

    private var from = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_address_list, container, false)
            binding = FragmentAddressListBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        agileLoader = AgileLoader(requireContext())
        if (arguments?.getString("from") == "checkOut") {
            from = "checkOut"
        }
    }

    private fun setObserver() {
        addressViewModel.addressList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    agileLoader.dismiss()
                    if (response.data!!.success) {
                        setAddressData(response.data.data)
                    } else {

                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    agileLoader.show()
                }
                is Resource.Error -> {
                    agileLoader.dismiss()
                }
            }
        }
    }

    private fun setAddressData(addressData: List<AddressData>) {
        addressAdapter.differ.submitList(addressData)
        binding.addressRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
        if (from == "checkOut") {
            addressAdapter.setAddressfrom("checkOut")
        }
        binding.noAddressLbl.isVisible = addressData.isEmpty()
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@AddressListFragment)
        binding.addNewAddress.setOnClickListener(this@AddressListFragment)

        addressAdapter.setOnEditAddressClickListener { id, addressData ->

            val bundle = Bundle().apply {
                putString("from", "editAddress")
                putString("addressId", id.toString())
                putString("address", addressData.address)
                putString("stateId", addressData.state_id)
                putString("cityId", addressData.city_id)
                putString("pinCode", addressData.pincode)
                putString("phone", addressData.alternate_phone)
            }

            findNavController().navigate(R.id.addressFormFragment, bundle)
        }

        addressAdapter.setOnDeleteAddressClickListener {
            showAlertOnRemove(it)
        }

        addressAdapter.setOnAddressClick {
            val bundle = Bundle().apply {
                putString("addressPosition", it.toString())
            }
            findNavController().navigate(
                R.id.action_addressListFragment_to_checkOutFragment,
                bundle
            )
        }

    }

    private fun showAlertOnRemove(id: Int) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure want remove address?")
        alertDialog.setCancelable(true)

        alertDialog.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            addressViewModel.removeAddress(id.toString())
            addressViewModel.deleteAddress.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {

                        addressViewModel.getAddress()
                    }
                    is Resource.Loading -> {
                        agileLoader.show()
                    }
                    is Resource.Error -> {
                        agileLoader.dismiss()
                    }
                }
            }
            dialog.cancel()
        }

        alertDialog.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11: AlertDialog = alertDialog.create()
        alert11.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                if (from == "checkOut") {
                    findNavController().navigate(R.id.action_addressListFragment_to_checkOutFragment)
                } else {
                    findNavController().popBackStack()
                }
            }

            R.id.addNewAddress -> {

                    findNavController().navigate(R.id.addressFormFragment)


            }
        }
    }

    override fun onResume() {
        addressViewModel.getAddress()
        setObserver()
        super.onResume()
    }
}
