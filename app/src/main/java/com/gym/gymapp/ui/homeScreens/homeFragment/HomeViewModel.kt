package com.gym.gymapp.ui.homeScreens.homeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.homeScreens.homeFragment.model.HomeProductListModel
import com.gym.gymapp.ui.packageListing.model.PackageListModel
import com.gym.gymapp.ui.homeScreens.homeFragment.model.SliderModel
import com.gym.gymapp.ui.packageListing.model.VendorsModel
import com.gym.gymapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository,private val session: SessionManager) : ViewModel() {

    val slider = SingleLiveEvent<Resource<SliderModel>>()
    val selectPackageList = SingleLiveEvent<Resource<PackageListModel>>()

    val trendingProduct = SingleLiveEvent<Resource<HomeProductListModel>>()
    val newProduct = SingleLiveEvent<Resource<HomeProductListModel>>()
    val vendorsList = SingleLiveEvent<Resource<VendorsModel>>()

    val cartCount = MutableLiveData<Int>()

    init {
        getCartCount()
    }

    fun getMainSliders() = viewModelScope.launch { safeSliderCall() }
    fun getPackageList() = viewModelScope.launch { safePackageListCall() }
    fun getTrendingProducts() = viewModelScope.launch { safeTrendingProductsCall() }
    fun getNewProducts() = viewModelScope.launch { safeNewProductsCall() }
    fun getVendorList() = viewModelScope.launch { safeVendorListCall() }

    private suspend fun safeTrendingProductsCall() {
        trendingProduct.postValue(Resource.Loading())
        try {
            val response = repository.getTrendingProducts()
            trendingProduct.postValue(handleTrendingProducts(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> trendingProduct.postValue(Resource.Error("Network Failure", null))
                else -> trendingProduct.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleTrendingProducts(response: Response<HomeProductListModel>): Resource<HomeProductListModel> {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    private suspend fun safeNewProductsCall() {
        newProduct.postValue(Resource.Loading())
        try {
            val response = repository.getNewProducts()
            newProduct.postValue(handleNewProducts(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newProduct.postValue(Resource.Error("Network Failure", null))
                else -> newProduct.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleNewProducts(response: Response<HomeProductListModel>): Resource<HomeProductListModel> {
        if (response.isSuccessful) {
            response.body()?.let { newProductsResponse ->
                return Resource.Success(newProductsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safeVendorListCall() {
        vendorsList.postValue(Resource.Loading())
        try {
            val response = repository.getVendorList()
            vendorsList.postValue(handleVendorsLists(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> vendorsList.postValue(Resource.Error("Network Failure", null))
                else -> vendorsList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleVendorsLists(response: Response<VendorsModel>): Resource<VendorsModel> {
        if (response.isSuccessful) {
            response.body()?.let { vendorResponse ->
                return Resource.Success(vendorResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safeSliderCall() {
        slider.postValue(Resource.Loading())
        try {
            val response = repository.slider()
            slider.postValue(handleSlider(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> slider.postValue(Resource.Error("Network Failure", null))
                else -> slider.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleSlider(response: Response<SliderModel>): Resource<SliderModel> {
        if (response.isSuccessful) {
            response.body()?.let { registerResponse ->
                return Resource.Success(registerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safePackageListCall() {
        selectPackageList.postValue(Resource.Loading())
        try {
            val response = repository.getSelectPackageList()
            selectPackageList.postValue(handlePackageList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> selectPackageList.postValue(
                    Resource.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> selectPackageList.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handlePackageList(response: Response<PackageListModel>): Resource<PackageListModel> {
        if (response.isSuccessful) {
            response.body()?.let { packageListResponse ->
                return Resource.Success(packageListResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    private fun getCartCount() = viewModelScope.launch {
        try {
            val response = repository.getCartData(session.loginId!!,session.deviceId!!)
            if (response.isSuccessful) {
                response.body()?.let { packageListResponse ->
                    cartCount.postValue(packageListResponse.data.size)
                    session.cartCount = packageListResponse.data.size
                }
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> selectPackageList.postValue(
                    Resource.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> selectPackageList.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }
}