package com.gym.gymapp.ui.vendors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.productListing.model.FilterCategoryModel
import com.gym.gymapp.ui.productListing.model.FilterOrganizationModel
import com.gym.gymapp.ui.vendors.model.VendorDetailsModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VendorVieModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val vendorDetails = SingleLiveEvent<Resource<VendorDetailsModel>>()
    val organisationDetails = SingleLiveEvent<Resource<VendorDetailsModel>>()

    fun getVendorDetails(id:String) = viewModelScope.launch { safeVendorDetailsCall(id) }
    fun getOrganisationDetails(id:String) = viewModelScope.launch { safeOrganisationDetailsCall(id) }

    private suspend fun safeVendorDetailsCall(id: String) {
        vendorDetails.postValue(Resource.Loading())
        try {
            val response = repository.getVendorDetails(id)
            vendorDetails.postValue(handleVendorDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> vendorDetails.postValue(Resource.Error("Network Failure", null))
                else -> vendorDetails.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleVendorDetails(response: Response<VendorDetailsModel>): Resource<VendorDetailsModel>? {
        if (response.isSuccessful) {
            response.body()?.let { vendorDetailsResponse ->
                return Resource.Success(vendorDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    private suspend fun safeOrganisationDetailsCall(id: String) {
        organisationDetails.postValue(Resource.Loading())
        try {
            val response = repository.getOrganisationDetails(id)
            organisationDetails.postValue(handleOrganisationDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> organisationDetails.postValue(Resource.Error("Network Failure", null))
                else -> organisationDetails.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleOrganisationDetails(response: Response<VendorDetailsModel>): Resource<VendorDetailsModel>? {
        if (response.isSuccessful) {
            response.body()?.let { organisationDetailsResponse ->
                return Resource.Success(organisationDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}