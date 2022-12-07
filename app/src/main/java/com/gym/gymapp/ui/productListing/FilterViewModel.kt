package com.gym.gymapp.ui.productListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.productListing.model.FilterCategoryModel
import com.gym.gymapp.ui.productListing.model.FilterOrganizationModel
import com.gym.gymapp.ui.productListing.model.FilterSubCategoryModel
import com.gym.gymapp.ui.productListing.model.FilterVendorModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val filterCategory = SingleLiveEvent<Resource<FilterCategoryModel>>()
    val filterSubCategory = SingleLiveEvent<Resource<FilterSubCategoryModel>>()
    val filterVendor = SingleLiveEvent<Resource<FilterVendorModel>>()
    val filterOrganisation = SingleLiveEvent<Resource<FilterOrganizationModel>>()



    init {
//        getFilterCategory()
//        getFilterOrganisation()
//        getFilterVendor()
    }

    fun getFilterCategory() = viewModelScope.launch { safeFilterCategoryCall() }
    fun getFilterSubCategory() = viewModelScope.launch { safeFilterSubCategoryCall() }
    fun getFilterOrganisation(id:String) = viewModelScope.launch { safeFilterOrganisationCall(id) }



    private suspend fun safeFilterCategoryCall() {
        filterCategory.postValue(Resource.Loading())
        try {
            val response = repository.getFilterCategory()
            filterCategory.postValue(handleFilterCategory(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> filterCategory.postValue(Resource.Error("Network Failure", null))
                else -> filterCategory.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleFilterCategory(response: Response<FilterCategoryModel>): Resource<FilterCategoryModel> {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


 private suspend fun safeFilterSubCategoryCall() {
        filterSubCategory.postValue(Resource.Loading())
        try {
            val response = repository.getFilterSubCategory()
            filterSubCategory.postValue(handleFilterSubCategory(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> filterSubCategory.postValue(Resource.Error("Network Failure", null))
                else -> filterSubCategory.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleFilterSubCategory(response: Response<FilterSubCategoryModel>): Resource<FilterSubCategoryModel> {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safeFilterOrganisationCall(id: String) {
        filterOrganisation.postValue(Resource.Loading())
        try {
            val response = repository.getFilterOrganisation(id)
            filterOrganisation.postValue(handleFilterOrganisation(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> filterOrganisation.postValue(Resource.Error("Network Failure", null))
                else -> filterOrganisation.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleFilterOrganisation(response: Response<FilterOrganizationModel>): Resource<FilterOrganizationModel>? {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


}