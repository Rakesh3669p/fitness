package com.gym.gymapp.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.orders.model.OrderHistoryModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val repository: Repository,private val session: SessionManager) : ViewModel() {

    val orderHistory = SingleLiveEvent<Resource<OrderHistoryModel>>()

    init {
        viewModelScope.launch {
            safeCallOrdersHistory()
        }
    }

    suspend fun safeCallOrdersHistory() {
        orderHistory.postValue(Resource.Loading())
        try {
            val response = repository.getOrderHistory("Bearer ${session.token}")
            orderHistory.postValue(handleOrderHistory(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> orderHistory.postValue(Resource.Error("Network Failure", null))
                else -> orderHistory.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleOrderHistory(response: Response<OrderHistoryModel>): Resource<OrderHistoryModel> {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}