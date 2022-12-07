package com.gym.gymapp.ui.commonViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.account.model.GetProfileModel
import com.gym.gymapp.ui.account.model.UpdateProfileModel
import com.gym.gymapp.ui.commonViewModel.model.DistanceMatrixModel
import com.gym.gymapp.ui.coupon.model.GetCouponModel
import com.gym.gymapp.ui.homeScreens.searchScreen.model.SearchSuggestionModel
import com.gym.gymapp.ui.noification.model.NotificationModel
import com.gym.gymapp.ui.organisationsList.model.FilterCitiesModel
import com.gym.gymapp.ui.organisationsList.model.OrganisationListModel
import com.gym.gymapp.ui.slugs.model.FaqModel
import com.gym.gymapp.ui.slugs.model.SlugModel
import com.gym.gymapp.ui.socialRefer.model.ReferCodeModel
import com.gym.gymapp.ui.starIcon.model.StarIconModel
import com.gym.gymapp.ui.trainer.model.TrainerModel
import com.gym.gymapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val repository: Repository,
    private val session: SessionManager
) : ViewModel() {
    val faq = SingleLiveEvent<Resource<FaqModel>>()
    val pages = SingleLiveEvent<Resource<SlugModel>>()

    val starIcon = SingleLiveEvent<Resource<StarIconModel>>()
    val trainerIcon = SingleLiveEvent<Resource<TrainerModel>>()

    val getCouponCode = SingleLiveEvent<Resource<GetCouponModel>>()
    val referCode = SingleLiveEvent<Resource<ReferCodeModel>>()

    val updateProfile = SingleLiveEvent<Resource<UpdateProfileModel>>()
    val profile = SingleLiveEvent<Resource<GetProfileModel>>()
    val searchSuggestions = SingleLiveEvent<Resource<SearchSuggestionModel>>()

    val notifications = SingleLiveEvent<Resource<NotificationModel>>()

    val organisationList = SingleLiveEvent<Resource<OrganisationListModel>>()
    val distance = SingleLiveEvent<Resource<DistanceMatrixModel>>()

    val filterCities = SingleLiveEvent<Resource<FilterCitiesModel>>()


    fun getFAQ() = viewModelScope.launch {
        safeFAQCall()
    }

    fun getSlugs(id: String) {
        viewModelScope.launch { safeSlugsCall(id) }
    }


    private suspend fun safeFAQCall() {
        faq.postValue(Resource.Loading())
        try {
            val response = repository.getFAQ()
            faq.postValue(handleBlogDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> faq.postValue(Resource.Error("Network Failure", null))
                else -> faq.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    fun updateProfile(params: MutableMap<String, RequestBody>, imageFile: File) {

        val profileImageBody = if (imageFile.path.isEmpty()) {
            getRequestBodyFromFile("profile", null)
        } else {
            getRequestBodyFromFile("profile", imageFile)
        }

        viewModelScope.launch { safeUpdateProfileCall(params, profileImageBody) }
    }


    private suspend fun safeUpdateProfileCall(
        params: MutableMap<String, RequestBody>,
        profileImageBody: MultipartBody.Part?
    ) {
        updateProfile.postValue(Resource.Loading())
        try {
            val response =
                repository.updateProfile("Bearer ${session.token}", params, profileImageBody)
            if (response.isSuccessful) {
                response.body()?.let { response ->
                    updateProfile.postValue(Resource.Success(response))
                }
            } else {
                updateProfile.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateProfile.postValue(Resource.Error("Network Failure", null))
                else -> updateProfile.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    fun getProfile() = viewModelScope.launch { safeGetProfileCall() }

    private suspend fun safeGetProfileCall() {
        profile.postValue(Resource.Loading())
        try {
            val response =
                repository.getProfile("Bearer ${session.token}")
            if (response.isSuccessful) {
                response.body()?.let { response ->
                    profile.postValue(Resource.Success(response))
                }
            } else {
                profile.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> profile.postValue(Resource.Error("Network Failure", null))
                else -> profile.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    fun getSearchSuggestions(text: String) = viewModelScope.launch {
    val params:MutableMap<String,String> = HashMap()
        params["search_key"] = text
        safeGetSearchSuggestionsCall(params)
    }

    private suspend fun safeGetSearchSuggestionsCall(params: MutableMap<String, String>) {
        searchSuggestions.postValue(Resource.Loading())
        try {
            val response =
                repository.getSearchSuggestion(params)
            if (response.isSuccessful) {
                response.body()?.let { response ->
                    searchSuggestions.postValue(Resource.Success(response))
                }
            } else {
                searchSuggestions.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchSuggestions.postValue(Resource.Error("Network Failure", null))
                else -> searchSuggestions.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    fun getCoupon() {
        viewModelScope.launch { safeCouponCall() }
    }


    private suspend fun safeCouponCall() {
        getCouponCode.postValue(Resource.Loading())
        try {
            val response = repository.getCoupon()
            if (response.isSuccessful) {
                response.body()?.let { response ->
                    getCouponCode.postValue(Resource.Success(response))
                }
            } else {
                getCouponCode.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getCouponCode.postValue(Resource.Error("Network Failure", null))
                else -> getCouponCode.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    fun getReferCode() {
        viewModelScope.launch { safeReferCodeCall() }
    }


    private suspend fun safeReferCodeCall() {
        referCode.postValue(Resource.Loading())
        try {
            val response = repository.getReferCode("Bearer ${session.token}")
            if (response.isSuccessful) {
                response.body()?.let { response ->
                    referCode.postValue(Resource.Success(response))
                }
            } else {
                referCode.postValue(Resource.Error(response.message(), null))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> referCode.postValue(Resource.Error("Network Failure", null))
                else -> referCode.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleBlogDetails(response: Response<FaqModel>): Resource<FaqModel> {
        if (response.isSuccessful) {
            response.body()?.let { faqResponse ->
                return Resource.Success(faqResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    private suspend fun safeSlugsCall(id: String) {
        pages.postValue(Resource.Loading())
        try {
            val response = repository.getSlugs(id)
            pages.postValue(handleSlugs(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> pages.postValue(Resource.Error("Network Failure", null))
                else -> pages.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleSlugs(response: Response<SlugModel>): Resource<SlugModel> {
        if (response.isSuccessful) {
            response.body()?.let { slugResponse ->
                return Resource.Success(slugResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun getStarIconDetails(id: String) {
        viewModelScope.launch { safeStarIconCall(id) }
    }


    private suspend fun safeStarIconCall(id: String) {
        starIcon.postValue(Resource.Loading())
        try {
            val response = repository.getStarIcon(id)
            starIcon.postValue(handleStarIcon(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> starIcon.postValue(Resource.Error("Network Failure", null))
                else -> starIcon.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleStarIcon(response: Response<StarIconModel>): Resource<StarIconModel> {
        if (response.isSuccessful) {
            response.body()?.let { starResponse ->
                return Resource.Success(starResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun getTrainerDetails(id: String) {
        viewModelScope.launch { safeTrainerCall(id) }
    }


    private suspend fun safeTrainerCall(id: String) {
        trainerIcon.postValue(Resource.Loading())
        try {
            val response = repository.getTrainerDetails(id)
            trainerIcon.postValue(handleTrainerDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> trainerIcon.postValue(Resource.Error("Network Failure", null))
                else -> trainerIcon.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleTrainerDetails(response: Response<TrainerModel>): Resource<TrainerModel> {
        if (response.isSuccessful) {
            response.body()?.let { trainerResponse ->
                return Resource.Success(trainerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }



    fun getNotifications() {
        viewModelScope.launch { safeNotificationsCall() }
    }


    private suspend fun safeNotificationsCall() {
        notifications.postValue(Resource.Loading())
        try {
            val response = repository.getNotifications("Bearer ${session.token}")
            notifications.postValue(handleNotificationsDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> notifications.postValue(Resource.Error("Network Failure", null))
                else -> notifications.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleNotificationsDetails(response: Response<NotificationModel>): Resource<NotificationModel> {
        if (response.isSuccessful) {
            response.body()?.let { trainerResponse ->
                return Resource.Success(trainerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun getOrganisationList(params: MutableMap<String, String>) {
        viewModelScope.launch { safeOrganisationListCall(params) }
    }

    private suspend fun safeOrganisationListCall(params: Map<String, String>) {
        organisationList.postValue(Resource.Loading())
        try {
            val response = repository.getOrganisationList("Bearer ${session.token}", params = params)
            organisationList.postValue(handleOrganisationList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> organisationList.postValue(Resource.Error("Network Failure", null))
                else -> organisationList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleOrganisationList(response: Response<OrganisationListModel>): Resource<OrganisationListModel> {
        if (response.isSuccessful) {
            response.body()?.let { trainerResponse ->
                return Resource.Success(trainerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun getDistance(params: MutableMap<String, String>) {
        viewModelScope.launch { safeDistanceCall(params) }
    }

    private suspend fun safeDistanceCall(params: Map<String, String>) {
        distance.postValue(Resource.Loading())
        try {
            val response = repository.getDistance(params = params)
            distance.postValue(handleDistanceList(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> distance.postValue(Resource.Error("Network Failure", null))
                else -> distance.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleDistanceList(response: Response<DistanceMatrixModel>): Resource<DistanceMatrixModel> {
        if (response.isSuccessful) {
            response.body()?.let { trainerResponse ->
                return Resource.Success(trainerResponse)
            }
        }

        return Resource.Error(response.message(), null)
    }

    fun getFilterCities() {
        viewModelScope.launch { safeFilterCitiesCall() }
    }

    private suspend fun safeFilterCitiesCall() {
        filterCities.postValue(Resource.Loading())
        try {
            val response = repository.getFilterCities()
            filterCities.postValue(handleFilterCities(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> distance.postValue(Resource.Error("Network Failure", null))
                else -> distance.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleFilterCities(response: Response<FilterCitiesModel>): Resource<FilterCitiesModel> {
        if (response.isSuccessful) {
            response.body()?.let { trainerResponse ->
                return Resource.Success(trainerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


}