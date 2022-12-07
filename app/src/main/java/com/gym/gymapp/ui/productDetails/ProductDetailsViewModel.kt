package com.gym.gymapp.ui.productDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.productDetails.model.PackageDetailModel
import com.gym.gymapp.ui.productDetails.model.RelatedProductModel
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import com.gym.gymapp.ui.productDetails.model.cartModel.AddToCartModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(private val repository: Repository):ViewModel() {

    val productDetail = SingleLiveEvent<Resource<PackageDetailModel>>()
    val relatedProducts = SingleLiveEvent<Resource<RelatedProductModel>>()

    val addToCart = SingleLiveEvent<Resource<AddToCartModel>>()
    val isProductInWishList = MutableLiveData<Boolean>()


    fun getProductDetails(packageId: String,deviceId: String) = viewModelScope.launch {
        safeGetProductDetailsCall(packageId,deviceId)
    }

    fun getRelatedProducts(id:String) = viewModelScope.launch {
        safeGetRelatedProductCall(id)
    }

    private suspend fun safeGetProductDetailsCall(packageId: String,deviceId: String) {
        productDetail.postValue(Resource.Loading())

        try {
            val response = repository.getProductDetail(packageId,deviceId)
            productDetail.postValue(handleProductDetails(response))
        }catch (t:Throwable){
            when (t) {
                is IOException -> productDetail.postValue(Resource.Error("Network Failure", null))
                else -> productDetail.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleProductDetails(response: Response<PackageDetailModel>): Resource<PackageDetailModel> {
        if (response.isSuccessful) {
            response.body()?.let { productDetailsResponse ->
                return Resource.Success(productDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


   private suspend fun safeGetRelatedProductCall(id: String) {
        relatedProducts.postValue(Resource.Loading())

        try {
            val response = repository.getRelatedProduct(id)
            relatedProducts.postValue(handleRelatedProduct(response))
        }catch (t:Throwable){
            when (t) {
                is IOException -> relatedProducts.postValue(Resource.Error("Network Failure", null))
                else -> relatedProducts.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleRelatedProduct(response: Response<RelatedProductModel>): Resource<RelatedProductModel> {
        if (response.isSuccessful) {
            response.body()?.let { productDetailsResponse ->
                return Resource.Success(productDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun addToCart(params:Map<String,String>) = viewModelScope.launch {
        safeAddToCartCall(params)
    }

    private suspend fun safeAddToCartCall(params: Map<String, String>) {
        addToCart.postValue(Resource.Loading())

        try {
            val response = repository.addToCart(params = params)
            addToCart.postValue(handleAddToCart(response))
        }catch (t:Throwable){
            when (t) {
                is IOException -> addToCart.postValue(Resource.Error("Network Failure", null))
                else -> addToCart.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAddToCart(response: Response<AddToCartModel>): Resource<AddToCartModel> {
        if (response.isSuccessful) {
            response.body()?.let { addToCartResponse ->
                return Resource.Success(addToCartResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun isProductInWishList(id:Int) = viewModelScope.launch {
        isProductInWishList.postValue(repository.isProductAddedInWishList(id))
    }
    fun addToWishList(packageData: WishListPackageData) = viewModelScope.launch {
        repository.addProductInWishList(packageData)
    }

    fun removeFromWishList(id: Int) = viewModelScope.launch {
        repository.removeProductFromWishList(id)
    }
}
