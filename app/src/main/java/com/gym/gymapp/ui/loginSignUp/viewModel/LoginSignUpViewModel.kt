package com.gym.gymapp.ui.loginSignUp.viewModel

import android.R
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.loginSignUp.LoginScreen.model.LoginModel
import com.gym.gymapp.ui.loginSignUp.LoginScreen.model.SocialLoginModel
import com.gym.gymapp.ui.loginSignUp.RegisterScreen.model.RegisterModel
import com.gym.gymapp.ui.loginSignUp.otp.OTPModel
import com.gym.gymapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginSignUpViewModel @Inject constructor(private val repository: Repository,private val session: SessionManager) : ViewModel() {
    val register = SingleLiveEvent<Resource<RegisterModel>>()
    val login = SingleLiveEvent<Resource<LoginModel>>()
    val otp = SingleLiveEvent<Resource<OTPModel>>()
    val socialLogin = SingleLiveEvent<Resource<SocialLoginModel>>()


    private suspend fun safeRegisterCall(
        params: MutableMap<String, RequestBody>,
        profileImageBody: MultipartBody.Part?
    ) {
        register.postValue(Resource.Loading())

        try {
            val response = repository.register(params, profileImageBody)
            register.postValue(handleRegister(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> register.postValue(
                    Resource.Error(
                        "Network Failure ${t.message}",
                        null
                    )
                )
                else -> register.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleRegister(response: Response<RegisterModel>): Resource<RegisterModel> {
        if (response.isSuccessful) {
            response.body()?.let { registerResponse ->
                return Resource.Success(registerResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun validateAndRegister(
        name: String,
        email: String,
        userPhone: String,
        referCode: String,
        profileImage: File?
    ) {

        when {
            name.isEmpty() -> {
                register.postValue(Resource.Error("Please Enter Valid User Name"))
            }
            email.isEmpty() -> {
                register.postValue(Resource.Error("Please Enter Valid Email Id"))
            }
            userPhone.isEmpty() -> {
                register.postValue(Resource.Error("Please Enter Valid Phone No."))
            }
            else -> {
                val registerParams: MutableMap<String, RequestBody> = HashMap()
                registerParams["name"] = getRequestBodyFromString(name) as RequestBody
                registerParams["email"] = getRequestBodyFromString(email) as RequestBody
                registerParams["phone_number"] = getRequestBodyFromString(userPhone) as RequestBody
                registerParams["sponsor_code"] = getRequestBodyFromString(referCode) as RequestBody
                val profileImageBody = getRequestBodyFromFile("profile", profileImage)

                viewModelScope.launch {
                    safeRegisterCall(registerParams, profileImageBody)
                }
            }
        }
    }

    /************************************** LOGIN ***********************************************/

    private suspend fun safeLoginCall(
        params: MutableMap<String, String>
    ) {
        login.postValue(Resource.Loading())

        try {
            val response = repository.login(params)
            login.postValue(handleLogin(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> login.postValue(Resource.Error("Network Failure", null))
                else -> login.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleLogin(response: Response<LoginModel>): Resource<LoginModel> {
        if (response.isSuccessful) {
            response.body()?.let { loginResponse ->
                return Resource.Success(loginResponse)
            }
        } else if (!response.isSuccessful) {
            response.errorBody()?.let { errorBody ->
                val errorJson = JSONObject(errorBody.string())

                return Resource.Error(errorJson["message"].toString())
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun validateAndLogin(
        userPhoneEmail: String,
    ) {

        when {
            userPhoneEmail.isNullOrEmpty() -> {
                login.postValue(Resource.Error("Please Enter Valid Email/Phone No."))
            }
            else -> {
                val loginParams: MutableMap<String, String> = HashMap()
                loginParams["phone_number"] = userPhoneEmail.trim()
                viewModelScope.launch {
                    safeLoginCall(loginParams)
                }
            }
        }
    }


    /************************************** OTP ***********************************************/

    private suspend fun safeOTPCall(
        params: MutableMap<String, String>
    ) {
        otp.postValue(Resource.Loading())

        try {
            val response = repository.otp(params)
            otp.postValue(handleOTP(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> otp.postValue(Resource.Error("Network Failure", null))
                else -> otp.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleOTP(response: Response<OTPModel>): Resource<OTPModel>? {
        if (response.isSuccessful) {
            response.body()?.let { otpResponse ->
                return Resource.Success(otpResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    /************************************** Social Login ***********************************************/

    private suspend fun safeSocialLoginCall(
        params: MutableMap<String, String>
    ) {
        socialLogin.postValue(Resource.Loading())

        try {
            val response = repository.socialLogin(params)
            socialLogin.postValue(handleSocialLogin(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> socialLogin.postValue(Resource.Error("Network Failure", null))
                else -> socialLogin.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleSocialLogin(response: Response<SocialLoginModel>): Resource<SocialLoginModel> {
        if (response.isSuccessful) {
            response.body()?.let { otpResponse ->
                return Resource.Success(otpResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


    fun validateOTP(
        otpEdt: String,
    ) {

        when {
            otpEdt.isNullOrEmpty() -> {
                otp.postValue(Resource.Error("Please Enter Valid OTP..."))
            }
            else -> {
                val otpParams: MutableMap<String, String> = HashMap()
                otpParams["email_verification_code"] = otpEdt.trim()
                otpParams["player_id"] =  session.playerId.toString()
                otpParams["device_token"] = session.deviceId.toString()
                viewModelScope.launch {
                    safeOTPCall(otpParams)
                }
            }
        }
    }

    fun validateSocialLogin(
        loginType: String,
        name: String?,
        email: String?,
        oauthId: String?,
        googleImage: String?
    ) {

        if (name.toString().isEmpty() && email.toString().isEmpty() && oauthId.toString()
                .isEmpty()
        ) {
            socialLogin.postValue(Resource.Error("Something Went Wrong.. Please try again!"))
        } else {
            val socialParams: MutableMap<String, String> = HashMap<String, String>().apply {
                put("oauth_provider", loginType)
                put("oauth_uid", oauthId.toString())
                put("name", name.toString())
                put("email", email.toString())
                put("image", googleImage.toString())
                put("player_id", session.playerId.toString())
                put("device_token", session.deviceId.toString())
            }

            viewModelScope.launch {
                safeSocialLoginCall(socialParams)
            }

        }
    }

}