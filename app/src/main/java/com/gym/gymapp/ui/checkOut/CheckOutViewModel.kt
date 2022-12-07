package com.gym.gymapp.ui.checkOut

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.ui.checkOut.model.CheckOutModel
import com.gym.gymapp.network.Repository
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val checkOut = SingleLiveEvent<Resource<CheckOutModel>>()

    fun checkOutApi(token: String,params: Map<String, String>) = viewModelScope.launch {
        safeCheckOutCall(token,params)
    }

    private suspend fun safeCheckOutCall(token:String,params:Map<String, String>) {
        checkOut.postValue(Resource.Loading())
        try {
            val response = repository.checkOut("Bearer $token",params = params)
            checkOut.postValue(handleBlogDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> checkOut.postValue(Resource.Error("Network Failure", null))
                else -> checkOut.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleBlogDetails(response: Response<CheckOutModel>): Resource<CheckOutModel> {
        if (response.isSuccessful) {
            response.body()?.let { checkOutResponse ->
                return Resource.Success(checkOutResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}