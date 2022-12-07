package com.gym.gymapp.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentAddressFormBinding
import com.gym.gymapp.ui.address.model.CityData
import com.gym.gymapp.ui.address.model.StateData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressFormFragment : Fragment(R.layout.fragment_address_form), View.OnClickListener {

    private lateinit var binding: FragmentAddressFormBinding

    private var currentView: View? = null

    @Inject
    lateinit var session: SessionManager

    
    lateinit var agileLoader: AgileLoader

    private val addressViewModel: AddressViewModel by viewModels()

    private var stateId = ""
    private var cityId = ""
    private var from = ""
    private var addressId = ""

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_address_form, container, false)
            binding = FragmentAddressFormBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        agileLoader = AgileLoader(requireContext())
        binding.completeAddressEdt.setText(session.address)
        agileLoader.show()
        if (arguments?.getString("from") == "editAddress") {
            from = arguments?.getString("from").toString()
            addressId = arguments?.getString("addressId").toString()
            binding.completeAddressEdt.setText(arguments?.getString("address").toString())
            stateId = arguments?.getString("stateId").toString()
            cityId = arguments?.getString("cityId").toString()
            binding.pincodeEdt.setText(arguments?.getString("pinCode").toString())
            binding.mobileEdt.setText(arguments?.getString("phone").toString())
        }
    }



    private fun setObserver() {
        addressViewModel.state.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    agileLoader.dismiss()
                    setStateSpinner(response.data?.data)
                }
                is Resource.Loading -> {
                    agileLoader.show()
                }
                is Resource.Error -> {
                    agileLoader.dismiss()
                }
            }
        }
        addressViewModel.city.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    agileLoader.dismiss()
                    setCitySpinner(response.data?.data)
                }
                is Resource.Loading -> {
                    agileLoader.show()
                }
                is Resource.Error -> {
                    agileLoader.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }
    }

    private fun setCitySpinner(cityData: List<CityData>?) {

        val citySpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cityData!!)
        binding.citySpinner.adapter = citySpinnerAdapter
        /*if (cityId.isNotEmpty()) {
            cityData.forEachIndexed { index, it ->
                    if (it.id == cityId.toInt()) {
                    binding.citySpinner.setSelection(index)
                }
            }
        }*/
        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val selectedObject = binding.citySpinner.selectedItem as CityData
                if (from != "editAddress") {
                    cityId = selectedObject.id.toString()
                }

            }
        }
    }

    private fun setStateSpinner(stateData: List<StateData>?) {
        val stateSpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, stateData!!)
        binding.stateSpinner.adapter = stateSpinnerAdapter
        if (stateId.isNotEmpty()) {
            stateData.forEachIndexed { index, it ->
                if (it.id == stateId.toInt()) {
                    binding.stateSpinner.setSelection(index)
                }
            }
        }
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                val selectedObject = binding.stateSpinner.selectedItem as StateData
                stateId = selectedObject.id.toString()
                addressViewModel.getCities(selectedObject.id.toString())
            }
        }
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@AddressFormFragment)
        binding.submit.setOnClickListener(this@AddressFormFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                requireActivity().onBackPressed()
            }
            R.id.submit -> {
                setAddress()

//                findNavController().navigate(R.id.checkOutFragment)
//                startActivity(Intent(requireActivity(),CheckOutActivity::class.java))
            }
        }
    }

    private fun setAddress() {
        var completeAddress = binding.completeAddressEdt.text.toString()
        var pincode = binding.pincodeEdt.text.toString()
        var phone = binding.mobileEdt.text.toString()

        if (completeAddress.isEmpty() || pincode.isEmpty() || phone.isEmpty()) {
            requireContext().showToast("Please fill all required fields")
        } else {
            callAddEditAddress(completeAddress,pincode,phone)

        }

    }

    private fun callAddEditAddress(completeAddress: String, pincode: String, phone: String) {

        var params: MutableMap<String, String> = HashMap()
        if(from=="editAddress"){
            params["address_id"] = addressId
            params["address"] = completeAddress
            params["state_id"] = stateId
            params["city_id"] = cityId
            params["pincode"] = pincode
            params["alternate_phone"] = phone
            params["address_type"] = "Home"
            addressViewModel.updateAddress(params)

            addressViewModel.updateAddress.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        agileLoader.dismiss()
                        if (response.data!!.success) {
                            findNavController().navigate(R.id.action_addressFormFragment_to_addressListFragment)
                            requireContext().showToast("Your Address has been updated Successfully")
                        } else {
                            requireContext().showToast(response.data.message)
                        }
                    }

                    is Resource.Loading -> {
                        agileLoader.show()
                    }
                    is Resource.Error -> {
                        agileLoader.dismiss()
                        requireContext().showToast(response.message.toString())
                    }
                }
            }
        }else{
            params["address"] = completeAddress
            params["state_id"] = stateId
            params["city_id"] = cityId
            params["pincode"] = pincode
            params["alternate_phone"] = phone
            params["address_type"] = "Home"
            addressViewModel.addAddress(params)

            addressViewModel.addAddress.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        agileLoader.dismiss()
                        if (response.data!!.success) {
                            findNavController().popBackStack()
//                            findNavController().navigate(R.id.action_addressFormFragment_to_addressListFragment)
                            requireContext().showToast("Your Address has been added Successfully")
                        } else {
                            requireContext().showToast(response.data.message)
                        }
                    }

                    is Resource.Loading -> {
                        agileLoader.show()
                    }
                    is Resource.Error -> {
                        agileLoader.dismiss()
                        requireContext().showToast(response.message.toString())
                    }
                }
            }
        }
    }
}