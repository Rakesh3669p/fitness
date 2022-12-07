package com.gym.gymapp.ui.ActivePacks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksModel
import com.gym.gymapp.ui.ActivePacks.model.AttendanceModel
import com.gym.gymapp.ui.ActivePacks.model.AttendanceStatsModel
import com.gym.gymapp.ui.loginSignUp.LoginScreen.model.LoginModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ActivePacksViewModel @Inject constructor(private val repository: Repository,private val session:SessionManager):ViewModel() {

    val activePacks = SingleLiveEvent<Resource<ActivePacksModel>>()
    val attendanceStats = SingleLiveEvent<Resource<AttendanceStatsModel>>()
    val markAttendance = SingleLiveEvent<Resource<AttendanceModel>>()
    val reOrder = SingleLiveEvent<Resource<LoginModel>>()

    init {
        viewModelScope.launch {
            safeActivePacksCall()
        }
    }

    fun getActivePacks() = viewModelScope.launch {
        safeActivePacksCall()
    }
    fun getAttendanceStat(params: Map<String, String>) = viewModelScope.launch {
        safeAttendanceStatCall(params)
    }

    private suspend fun safeActivePacksCall() {
        activePacks.postValue(Resource.Loading())
        try {
            val response = repository.getActivePacks("Bearer ${session.token}")
            activePacks.postValue(handleActivePackages(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> activePacks.postValue(Resource.Error("Network Failure", null))
                else -> activePacks.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }


    fun setMarkAttendance(params: Map<String, String>) = viewModelScope.launch {
        safeAttendanceMarkCall(params)
    }

    private suspend fun safeAttendanceMarkCall(params: Map<String, String>) {
        markAttendance.postValue(Resource.Loading())
        try {
            val response = repository.markAttendance("Bearer ${session.token}",params)
            if (response.isSuccessful) {
                response.body()?.let { activePacksResponse ->
                    markAttendance.postValue(Resource.Success(activePacksResponse))
                }
            }else{
            markAttendance.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> markAttendance.postValue(Resource.Error("Network Failure", null))
                else -> markAttendance.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleActivePackages(response: Response<ActivePacksModel>): Resource<ActivePacksModel> {
        if (response.isSuccessful) {
            response.body()?.let { activePacksResponse ->
                return Resource.Success(activePacksResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    private suspend fun safeAttendanceStatCall(params:Map<String,String>) {
        attendanceStats.postValue(Resource.Loading())
        try {
            val response = repository.getAttendanceStats("Bearer ${session.token}",params = params)
            attendanceStats.postValue(handleAttendanceState(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> attendanceStats.postValue(Resource.Error("Network Failure", null))
                else -> attendanceStats.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAttendanceState(response: Response<AttendanceStatsModel>): Resource<AttendanceStatsModel> {
        if (response.isSuccessful) {
            response.body()?.let { activePacksResponse ->
                return Resource.Success(activePacksResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun reOrderPack(params: Map<String, String>) = viewModelScope.launch {
        safeReOrderPacksCall(params)
    }



    private suspend fun safeReOrderPacksCall(params:Map<String,String>) {
        reOrder.postValue(Resource.Loading())
        try {
            val response = repository.reOrder("Bearer ${session.token}",params = params)
            reOrder.postValue(handleReOrderState(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> reOrder.postValue(Resource.Error("Network Failure", null))
                else -> reOrder.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleReOrderState(response: Response<LoginModel>): Resource<LoginModel> {
        if (response.isSuccessful) {
            response.body()?.let { activePacksResponse ->
                return Resource.Success(activePacksResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }
}