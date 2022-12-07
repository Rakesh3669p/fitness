package com.gym.gymapp.ui.packageListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.packageListing.model.PackageListModel
import com.gym.gymapp.ui.packageListing.model.VendorsModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VendorListViewModel @Inject constructor(private val repository: Repository):ViewModel() {

    val vendorList = SingleLiveEvent<Resource<VendorsModel>>()

    init {
        viewModelScope.launch { 
            getVendors()
        }
    }

    private suspend fun getVendors() {
        vendorList.postValue(Resource.Loading())

        try {
            val response = repository.getVendorList()
            vendorList.postValue(handleVendorsList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> vendorList.postValue(Resource.Error("Network Failure", null))
                else -> vendorList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }
    private fun handleVendorsList(response: Response<VendorsModel>): Resource<VendorsModel> {
        if (response.isSuccessful) {
            response.body()?.let { vendorsResponse ->
                return Resource.Success(vendorsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }
}