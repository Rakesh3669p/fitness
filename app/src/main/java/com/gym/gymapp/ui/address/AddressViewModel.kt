package com.gym.gymapp.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.address.model.AddAddressModel
import com.gym.gymapp.ui.address.model.CityModel
import com.gym.gymapp.ui.address.model.GetAddressModel
import com.gym.gymapp.ui.address.model.StateModel
import com.gym.gymapp.ui.productDetails.model.cartModel.AddToCartModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val repository: Repository,private val session: SessionManager) : ViewModel() {

    val state = SingleLiveEvent<Resource<StateModel>>()
    val city = SingleLiveEvent<Resource<CityModel>>()
    val addAddress = SingleLiveEvent<Resource<AddAddressModel>>()
    val addressList = SingleLiveEvent<Resource<GetAddressModel>>()

    val updateAddress = SingleLiveEvent<Resource<AddAddressModel>>()
    val deleteAddress = SingleLiveEvent<Resource<AddAddressModel>>()


    init {
        viewModelScope.launch {
            safeCallGetState()
        }
    }

    fun removeAddress(id:String)=viewModelScope.launch {
        safeCallRemoveAddress(id)
    }

    private suspend fun safeCallRemoveAddress(id: String) {
        deleteAddress.postValue(Resource.Loading())
        try {
            val response = repository.removeAddress(id,"Bearer ${session.token}")
            deleteAddress.postValue(handleRemoveAddress(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteAddress.postValue(Resource.Error("Network Failure", null))
                else -> deleteAddress.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleRemoveAddress(response: Response<AddAddressModel>): Resource<AddAddressModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }



    fun updateAddress(params: MutableMap<String, String>)=viewModelScope.launch {
        safeCallUpdateAddress(params)
    }

    private suspend fun safeCallUpdateAddress(params: MutableMap<String, String>) {
        updateAddress.postValue(Resource.Loading())
        try {
            val response = repository.updateAddress(params,"Bearer ${session.token}")
            updateAddress.postValue(handleUpdateAddress(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateAddress.postValue(Resource.Error("Network Failure", null))
                else -> updateAddress.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleUpdateAddress(response: Response<AddAddressModel>): Resource<AddAddressModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }



    fun addAddress(params: MutableMap<String, String>)=viewModelScope.launch {
        safeCallAddAddress(params)
    }

    private suspend fun safeCallAddAddress(params: MutableMap<String, String>) {
        addAddress.postValue(Resource.Loading())
        try {
            val response = repository.addAddress(params,"Bearer ${session.token}")
            addAddress.postValue(handleAddAddress(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> addAddress.postValue(Resource.Error("Network Failure", null))
                else -> addAddress.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAddAddress(response: Response<AddAddressModel>): Resource<AddAddressModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun getAddress()=viewModelScope.launch {
        safeCallGetAddress()
    }

    private suspend fun safeCallGetAddress() {
        addressList.postValue(Resource.Loading())
        try {
            val response = repository.getAddress("Bearer ${session.token}")
            addressList.postValue(handleAddressList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> addressList.postValue(Resource.Error("Network Failure", null))
                else -> addressList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAddressList(response: Response<GetAddressModel>): Resource<GetAddressModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun getCities(id:String)=viewModelScope.launch {
        safeCallGetCities(id)
    }

    private suspend fun safeCallGetState() {
        state.postValue(Resource.Loading())
        try {
            val response = repository.getState()
            state.postValue(handleStateList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> state.postValue(Resource.Error("Network Failure", null))
                else -> state.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleStateList(response: Response<StateModel>): Resource<StateModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safeCallGetCities(id: String) {
        city.postValue(Resource.Loading())
        try {
            val response = repository.getCities(id)
            city.postValue(handleCities(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> city.postValue(Resource.Error("Network Failure", null))
                else -> city.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleCities(response: Response<CityModel>): Resource<CityModel> {
        if (response.isSuccessful) {
            response.body()?.let { stateResponse ->
                return Resource.Success(stateResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}