package com.gym.gymapp.network

import com.gym.gymapp.data.AppDatabaseDao
import com.gym.gymapp.ui.productDetails.model.PackageDetailsData
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class Repository @Inject constructor(private val dao: AppDatabaseDao) {

    suspend fun register(
        params: MutableMap<String, RequestBody>,
        profileImageBody: MultipartBody.Part?,
    ) = RetrofitInstance.api.register(params = params, image = profileImageBody)

    suspend fun login(
        params: MutableMap<String, String>
    ) = RetrofitInstance.api.login(params = params)

    suspend fun otp(
        params: MutableMap<String, String>
    ) = RetrofitInstance.api.otp(params = params)

    suspend fun socialLogin(
        params: MutableMap<String, String>
    ) = RetrofitInstance.api.socialLogin(params = params)

    suspend fun slider() = RetrofitInstance.api.slider()

    suspend fun packageList(
        params: MutableMap<String, String>,
    ) = RetrofitInstance.api.packageList(params)

    suspend fun getSelectPackageList() = RetrofitInstance.api.getSelectPackageList()

    suspend fun getVendorList() = RetrofitInstance.api.getVendorList()

    suspend fun getTrendingProducts() = RetrofitInstance.api.getTrendingProducts()

    suspend fun getNewProducts() = RetrofitInstance.api.getNewProducts()

    suspend fun getFilterCategory() = RetrofitInstance.api.getFilterCategory()

    suspend fun getFilterSubCategory() = RetrofitInstance.api.getFilterSubCategory()

    suspend fun getOrderHistory(token: String) = RetrofitInstance.api.getOrderHistory(token)

    suspend fun getFilterOrganisation(id: String) = RetrofitInstance.api.getFilterOrganisation(id)


    suspend fun getBlogsList() = RetrofitInstance.api.getBlogsList()

    suspend fun getBlogDetails(id: String) = RetrofitInstance.api.getBlogDetails(id)

    suspend fun getProductDetail(packageId: String, deviceId: String) =
        RetrofitInstance.api.getProductDetail(packageId, deviceId)

    suspend fun getCartData(userId: String, deviceId: String) =
        RetrofitInstance.api.getCartData(userId, deviceId)

    suspend fun applyCoupon(token: String, params: Map<String, String>) =
        RetrofitInstance.api.applyCoupon(token, params)

    suspend fun removeCartData(cartId: String) = RetrofitInstance.api.removeCartData(cartId)

    suspend fun addToCart(params: Map<String, String>) = RetrofitInstance.api.addToCart(params)

    suspend fun getRelatedProduct(id: String) = RetrofitInstance.api.getRelatedProducts(id)

    suspend fun getActivePacks(authToken: String) = RetrofitInstance.api.getActivePacks(authToken)

    suspend fun markAttendance(authToken: String, params: Map<String, String>) =
        RetrofitInstance.api.markAttendance(authToken, params)

    suspend fun getAttendanceStats(authToken: String, params: Map<String, String>) =
        RetrofitInstance.api.getAttendanceStats(authToken, params)

    suspend fun getVendorDetails(id: String) = RetrofitInstance.api.getVendorDetails(id)

    suspend fun getOrganisationDetails(id: String) = RetrofitInstance.api.getOrganisationDetails(id)

    suspend fun checkOut(token: String, params: Map<String, String>) =
        RetrofitInstance.api.checkOut(token, params)

    suspend fun getFAQ() = RetrofitInstance.api.getFAQ()

    suspend fun getCoupon() = RetrofitInstance.api.getCoupon()

    suspend fun updateProfile(
        token: String,
        params: MutableMap<String, RequestBody>,
        profileImageBody: MultipartBody.Part?
    ) = RetrofitInstance.api.updateProfile(token, profileImageBody, params)

    suspend fun getProfile(token: String) = RetrofitInstance.api.getProfile(token)
    suspend fun getSearchSuggestion(params: MutableMap<String, String>) =
        RetrofitInstance.api.getSearchSuggestion(params)

    suspend fun getReferCode(token: String) = RetrofitInstance.api.getReferCode(token)

    suspend fun getAchievements(token: String) = RetrofitInstance.api.getAchievements(token)

    suspend fun addAchievements(token: String, params: Map<String, String>) =
        RetrofitInstance.api.addAchievements(token, params)

    suspend fun updateAchievements(token: String, params: Map<String, String>) =
        RetrofitInstance.api.updateAchievement(token, params)

    suspend fun deleteAchievements(token: String, id: String) =
        RetrofitInstance.api.deleteAchievements(token, id)

    suspend fun getStarIcon(id: String) = RetrofitInstance.api.getStarIcon(id)

    suspend fun getTrainerDetails(id: String) = RetrofitInstance.api.getTrainerDetails(id)

    suspend fun getNotifications(token: String) = RetrofitInstance.api.getNotifications(token)

    suspend fun getOrganisationList(token: String,params: Map<String, String>) = RetrofitInstance.api.organisationList(token,params)

    suspend fun getSlugs(id: String) = RetrofitInstance.api.getSlugs(id)

    suspend fun getState() = RetrofitInstance.api.getState()

    suspend fun getAddress(token: String) = RetrofitInstance.api.getUserAddress(token)

    suspend fun addAddress(params: MutableMap<String, String>, token: String) =
        RetrofitInstance.api.addAddress(accessToken = token, params = params)

    suspend fun updateAddress(params: MutableMap<String, String>, token: String) =
        RetrofitInstance.api.updateAddress(accessToken = token, params = params)

    suspend fun removeAddress(id: String, token: String) =
        RetrofitInstance.api.removeAddress(accessToken = token, id)

    suspend fun getCities(id: String) = RetrofitInstance.api.getCities(id)


    suspend fun getFaqQuestion() = RetrofitInstance.api.getFaqQuestion()

    suspend fun getNextFaqQuestion(params: Map<String, String>) = RetrofitInstance.api.getNextFaqQuestion(params)

    suspend fun submitAllQuestions(token: String, params: Map<String, String>) = RetrofitInstance.api.submitAllQuestions(token,params)

    suspend fun organisationList(token: String, params: Map<String, String>) = RetrofitInstance.api.organisationList(token,params)

    suspend fun getDistance(params: Map<String, String>) = RetrofitInstanceDistanceMatrix.api.getDistance(params)

    suspend fun getFilterCities() = RetrofitInstance.api.getFilterCities()

    suspend fun reOrder(token: String,params: Map<String, String>) = RetrofitInstance.api.reOrder(token,params)

    suspend fun addProductInWishList(productData: WishListPackageData) = dao.addToWishList(productData = productData)

    fun getWishListData() = dao.getWishListData()

    suspend fun removeProductFromWishList(id: Int) = dao.removeProductFromWishList(id)

    suspend fun isProductAddedInWishList(id: Int) = dao.isProductAddedInWishList(id)

}