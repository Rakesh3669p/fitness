package com.gym.gymapp.ui.chatBoat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.chatBoat.model.ChatModel
import com.gym.gymapp.ui.chatBoat.model.SubmitAllChatModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatBoatViewModel @Inject constructor(private val session: SessionManager,private val repository: Repository) : ViewModel() {

    val getFaqQuestion = SingleLiveEvent<Resource<ChatModel>>()
    val getNextFaqQuestion = SingleLiveEvent<Resource<ChatModel>>()
    val submitAllQuestions = SingleLiveEvent<Resource<SubmitAllChatModel>>()

    fun getFaqQuestion() = viewModelScope.launch {
        safeGetFaqQuestionCall()
    }

    private suspend fun safeGetFaqQuestionCall() {
        getFaqQuestion.postValue(Resource.Loading())
        try {
            val response = repository.getFaqQuestion()
            getFaqQuestion.postValue(handleFaqQuestion(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getFaqQuestion.postValue(Resource.Error("Network Failure", null))
                else -> getFaqQuestion.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleFaqQuestion(response: Response<ChatModel>): Resource<ChatModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

    fun getNextFaqQuestion(params: Map<String, String>) = viewModelScope.launch {
        safeGetNextFaqQuestionCall(params)
    }

    private suspend fun safeGetNextFaqQuestionCall(params: Map<String, String>) {
        getFaqQuestion.postValue(Resource.Loading())
        try {
            val response = repository.getNextFaqQuestion(params)
            getNextFaqQuestion.postValue(handleNextFaqQuestion(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getNextFaqQuestion.postValue(
                    Resource.Error(
                        "Network Failure",
                        null
                    )
                )
                else -> getNextFaqQuestion.postValue(
                    Resource.Error(
                        "Conversion Error ${t.message}",
                        null
                    )
                )
            }
        }
    }

    private fun handleNextFaqQuestion(response: Response<ChatModel>): Resource<ChatModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun submitAllQuestions(params: Map<String, String>) = viewModelScope.launch {
        safeSubmitAllQuestionsCall(params)
    }

    private suspend fun safeSubmitAllQuestionsCall(params: Map<String, String>) {
        submitAllQuestions.postValue(Resource.Loading())
        try {
            val response = repository.submitAllQuestions("Bearer ${session.token}",params)
            submitAllQuestions.postValue(handleSubmitAllQuestions(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> submitAllQuestions.postValue(
                    Resource.Error("Network Failure", null)
                )
                else -> submitAllQuestions.postValue(
                    Resource.Error("Conversion Error ${t.message}", null)
                )
            }
        }
    }

    private fun handleSubmitAllQuestions(response: Response<SubmitAllChatModel>): Resource<SubmitAllChatModel> {
        if (response.isSuccessful) {
            response.body()?.let { achievementsResponse ->
                return Resource.Success(achievementsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }

}