package com.gym.gymapp.ui.productListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.productListing.model.ProductData
import com.gym.gymapp.ui.productListing.model.ProductListModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val packageList = SingleLiveEvent<Resource<List<ProductData>>>()
    private val packageListData = ArrayList<ProductData>()
    fun getPackageList(
        params: MutableMap<String, String>,
    ) = viewModelScope.launch {
        safePackageListCall(
            params
        )
    }

    private suspend fun safePackageListCall(
        params: MutableMap<String, String>,

    ) {
        if(params["page"]?.toInt() == 1){

            packageListData.clear()
        }
        packageList.postValue(Resource.Loading())
        try {
            val response = repository.packageList(
                params
            )
            packageList.postValue(handlePackageList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> packageList.postValue(Resource.Error("Network Failure", null))
                else -> packageList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handlePackageList(response: Response<ProductListModel>): Resource<List<ProductData>> {
        if (response.isSuccessful) {
            response.body()?.let { registerResponse ->
                return if (registerResponse.data.isEmpty()) {
                    Resource.Success(registerResponse.data)
                } else {
                    packageListData.addAll(registerResponse.data)
                    Resource.Success(packageListData)

                }
            }
        }
        return Resource.Error(response.message(), null)
    }
}