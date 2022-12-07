package com.gym.gymapp.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.cart.model.GetCartModel
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.checkOut.model.ApplyCouponModel
import com.gym.gymapp.ui.productDetails.model.PackageDetailModel
import com.gym.gymapp.ui.productDetails.model.PackageDetailsData
import com.gym.gymapp.ui.productDetails.model.cartModel.AddToCartModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
    private val session: SessionManager
) : ViewModel() {

    val cartProducts = SingleLiveEvent<Resource<List<PackageDetailsData>>>()
    val getCartData = SingleLiveEvent<Resource<GetCartModel>>()
    val removeCartData = SingleLiveEvent<Resource<AddToCartModel>>()
    val coupon = SingleLiveEvent<Resource<ApplyCouponModel>>()


    init {
//        getCartData()
    }

    fun applyCoupon(params: Map<String, String>) = viewModelScope.launch {
        safeApplyCouponCall(params)
    }

    private suspend fun safeApplyCouponCall(params: Map<String, String>) {
        coupon.postValue(Resource.Loading())

        try {
            val response = repository.applyCoupon("Bearer ${session.token}", params)

            if (response.isSuccessful) {
                response.body()?.let { productDetailsResponse ->
                    coupon.postValue(Resource.Success(productDetailsResponse))
                }
            } else {
                coupon.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> coupon.postValue(Resource.Error("Network Failure", null))
                else -> coupon.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    fun getCartData(userId: String, deviceId: String) = viewModelScope.launch {
        safeGetCartDataCall(userId, deviceId)
    }

    private suspend fun safeGetCartDataCall(userId: String, deviceId: String) {
        getCartData.postValue(Resource.Loading())

        try {
            val response = repository.getCartData(userId, deviceId)
            getCartData.postValue(handleCartData(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getCartData.postValue(Resource.Error("Network Failure", null))
                else -> getCartData.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleCartData(response: Response<GetCartModel>): Resource<GetCartModel> {
        if (response.isSuccessful) {
            response.body()?.let { productDetailsResponse ->
                return Resource.Success(productDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun removeProductFromCart(id: Int) = viewModelScope.launch {
        safeRemoveCartDataCall(id.toString())
    }


    private suspend fun safeRemoveCartDataCall(id: String) {
        removeCartData.postValue(Resource.Loading())
        try {
            val response = repository.removeCartData(id)
            removeCartData.postValue(handleRemoveCartData(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> removeCartData.postValue(Resource.Error("Network Failure", null))
                else -> removeCartData.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleRemoveCartData(response: Response<AddToCartModel>): Resource<AddToCartModel> {
        if (response.isSuccessful) {
            response.body()?.let { removeCartResponse ->
                return Resource.Success(removeCartResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


}