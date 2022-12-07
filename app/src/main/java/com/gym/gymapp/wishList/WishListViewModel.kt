package com.gym.gymapp.wishList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(private val repository: Repository): ViewModel(){

    val wishListData = MutableLiveData<List<WishListPackageData>>()

    init {

        getWishListData()
    }
    private fun getWishListData() = viewModelScope.launch {
        repository.getWishListData().collect {
            wishListData.postValue(it)
        }
    }

    fun removeWishListProduct(id:Int) = viewModelScope.launch {
        repository.removeProductFromWishList(id)
    }
}