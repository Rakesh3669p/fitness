package com.gym.gymapp.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.achievements.model.AchievementsModel
import com.gym.gymapp.ui.achievements.model.AddDeleteAchievementsModel
import com.gym.gymapp.ui.address.model.AddAddressModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: Repository,
    private val session: SessionManager
) :ViewModel(){

    val achievements = SingleLiveEvent<Resource<AchievementsModel>>()
    val addAchievements = SingleLiveEvent<Resource<AddAddressModel>>()
    val deleteAchievements = SingleLiveEvent<Resource<AddDeleteAchievementsModel>>()
    val updateAchievements = SingleLiveEvent<Resource<AddDeleteAchievementsModel>>()


   init {
       getAchievements()
   }

    fun getAchievements() = viewModelScope.launch{
           safeAchievementsCall()
    }

    private suspend fun safeAchievementsCall() {
        achievements.postValue(Resource.Loading())
        try {
            val response = repository.getAchievements("Bearer ${session.token}")
            achievements.postValue(handleAchievements(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> achievements.postValue(Resource.Error("Network Failure", null))
                else -> achievements.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAchievements(response: Response<AchievementsModel>): Resource<AchievementsModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun addAchievements(params:Map<String,String>) = viewModelScope.launch{
        safeAddAchievementsCall(params)
    }

    private suspend fun safeAddAchievementsCall(params: Map<String, String>) {
        addAchievements.postValue(Resource.Loading())
        try {
            val response = repository.addAchievements("Bearer ${session.token}",params)
            addAchievements.postValue(handleAddAchievements(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> addAchievements.postValue(Resource.Error("Network Failure", null))
                else -> addAchievements.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleAddAchievements(response: Response<AddAddressModel>): Resource<AddAddressModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun deleteAchievements(id:String) = viewModelScope.launch{
        safeDeleteAchievementsCall(id)
    }

    private suspend fun safeDeleteAchievementsCall(id:  String) {
        deleteAchievements.postValue(Resource.Loading())
        try {
            val response = repository.deleteAchievements("Bearer ${session.token}",id)
            deleteAchievements.postValue(handleDeleteAchievements(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteAchievements.postValue(Resource.Error("Network Failure", null))
                else -> deleteAchievements.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleDeleteAchievements(response: Response<AddDeleteAchievementsModel>): Resource<AddDeleteAchievementsModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun updateAchievements(params:Map<String,String>) = viewModelScope.launch{
        safeUpdateAchievementsCall(params)
    }

    private suspend fun safeUpdateAchievementsCall(params: Map<String, String>) {
        updateAchievements.postValue(Resource.Loading())
        try {
            val response = repository.updateAchievements("Bearer ${session.token}",params)
            updateAchievements.postValue(handleUpdateAchievements(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateAchievements.postValue(Resource.Error("Network Failure", null))
                else -> updateAchievements.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleUpdateAchievements(response: Response<AddDeleteAchievementsModel>): Resource<AddDeleteAchievementsModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}