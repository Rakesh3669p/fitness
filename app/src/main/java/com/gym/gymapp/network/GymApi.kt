package com.gym.gymapp.network

import com.gym.gymapp.cart.model.GetCartModel
import com.gym.gymapp.ui.organisationsList.model.OrganisationListModel
import com.gym.gymapp.ui.checkOut.model.CheckOutModel
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksModel
import com.gym.gymapp.ui.ActivePacks.model.AttendanceModel
import com.gym.gymapp.ui.ActivePacks.model.AttendanceStatsModel
import com.gym.gymapp.ui.account.model.GetProfileModel
import com.gym.gymapp.ui.account.model.UpdateProfileModel
import com.gym.gymapp.ui.achievements.model.AchievementsModel
import com.gym.gymapp.ui.achievements.model.AddDeleteAchievementsModel
import com.gym.gymapp.ui.address.model.AddAddressModel
import com.gym.gymapp.ui.address.model.CityModel
import com.gym.gymapp.ui.address.model.GetAddressModel
import com.gym.gymapp.ui.address.model.StateModel
import com.gym.gymapp.ui.chatBoat.model.ChatModel
import com.gym.gymapp.ui.chatBoat.model.SubmitAllChatModel
import com.gym.gymapp.ui.checkOut.model.ApplyCouponModel
import com.gym.gymapp.ui.commonViewModel.model.DistanceMatrixModel
import com.gym.gymapp.ui.coupon.model.GetCouponModel
import com.gym.gymapp.ui.slugs.model.FaqModel
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsDetailsModel
import com.gym.gymapp.ui.homeScreens.blogScreens.model.GetBlogsModel
import com.gym.gymapp.ui.productDetails.model.PackageDetailModel
import com.gym.gymapp.ui.productDetails.model.RelatedProductModel
import com.gym.gymapp.ui.homeScreens.homeFragment.model.HomeProductListModel
import com.gym.gymapp.ui.packageListing.model.PackageListModel
import com.gym.gymapp.ui.homeScreens.homeFragment.model.SliderModel
import com.gym.gymapp.ui.homeScreens.searchScreen.model.SearchSuggestionModel
import com.gym.gymapp.ui.loginSignUp.LoginScreen.model.LoginModel
import com.gym.gymapp.ui.loginSignUp.LoginScreen.model.SocialLoginModel
import com.gym.gymapp.ui.loginSignUp.RegisterScreen.model.RegisterModel
import com.gym.gymapp.ui.loginSignUp.otp.OTPModel
import com.gym.gymapp.ui.noification.model.NotificationModel
import com.gym.gymapp.ui.orders.model.OrderHistoryModel
import com.gym.gymapp.ui.organisationsList.model.FilterCitiesModel
import com.gym.gymapp.ui.packageListing.model.VendorsModel
import com.gym.gymapp.ui.productDetails.model.cartModel.AddToCartModel
import com.gym.gymapp.ui.productListing.model.*
import com.gym.gymapp.ui.slugs.model.SlugModel
import com.gym.gymapp.ui.socialRefer.model.ReferCodeModel
import com.gym.gymapp.ui.starIcon.model.StarIconModel
import com.gym.gymapp.ui.trainer.model.TrainerModel
import com.gym.gymapp.ui.vendors.model.VendorDetailsModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GymApi {

    @POST("login")
    suspend fun login(
        @Body params: MutableMap<String, String>
    ): Response<LoginModel>

    @Multipart
    @POST("register")
    suspend fun register(
        @Part image: MultipartBody.Part?,
        @PartMap params: MutableMap<String, RequestBody>
    ): Response<RegisterModel>

    @POST("otp_verification")
    suspend fun otp(
        @Body params: MutableMap<String, String>
    ): Response<OTPModel>

    @POST("social_media_login")
    suspend fun socialLogin(
        @Body params: MutableMap<String, String>
    ): Response<SocialLoginModel>

    @GET("get_slider")
    suspend fun slider(
    ): Response<SliderModel>

    @FormUrlEncoded
    @POST("get_category_wise_packages")
    suspend fun packageList(
        @FieldMap params: MutableMap<String, String>,
    ): Response<ProductListModel>

    @GET("get_category")
    suspend fun getSelectPackageList(): Response<PackageListModel>

    @GET("get_top_trading_packages")
    suspend fun getTrendingProducts(): Response<HomeProductListModel>

    @GET("get_new_packages")
    suspend fun getNewProducts(): Response<HomeProductListModel>

    @GET("get_all_vendors")
    suspend fun getVendorList(): Response<VendorsModel>


    @GET("get_blogs")
    suspend fun getBlogsList(): Response<GetBlogsModel>

    @GET("view_blogs/{id}")
    suspend fun getBlogDetails(@Path("id") id: String): Response<BlogsDetailsModel>

    @GET("get_category")
    suspend fun getFilterCategory(): Response<FilterCategoryModel>

    @GET("get_sub_category")
    suspend fun getFilterSubCategory(): Response<FilterSubCategoryModel>

    @GET("order_history")
    suspend fun getOrderHistory(@Header("authorization") accessToken: String): Response<OrderHistoryModel>

    @GET("get_all_organizations/{id}")
    suspend fun getFilterOrganisation(@Path("id") id: String): Response<FilterOrganizationModel>

    @FormUrlEncoded
    @POST("view_package")
    suspend fun getProductDetail(
        @Field("package_id") packageId: String,
        @Field("device_id") deviceId: String
    ): Response<PackageDetailModel>

    @FormUrlEncoded
    @POST("get_cart_list")
    suspend fun getCartData(
        @Field("user_id") userId: String,
        @Field("device_id") deviceId: String
    ): Response<GetCartModel>

    @FormUrlEncoded
    @POST("applay_coupon")
    suspend fun applyCoupon(
        @Header("authorization") accessToken: String,
        @FieldMap params: Map<String, String>
    ): Response<ApplyCouponModel>

    @FormUrlEncoded
    @POST("remove_cart_item")
    suspend fun removeCartData(
        @Field("cart_id") userId: String,
    ): Response<AddToCartModel>

    @GET("related_package/{id}")
    suspend fun getRelatedProducts(@Path("id") id: String): Response<RelatedProductModel>


    @GET("active_package")
    suspend fun getActivePacks(
        @Header("authorization") accessToken: String
    ): Response<ActivePacksModel>

    @POST("attendance_history")
    suspend fun getAttendanceStats(
        @Header("authorization") accessToken: String,
        @Body params: Map<String, String>
    ): Response<AttendanceStatsModel>

    @GET("get_vendor_detail/{id}")
    suspend fun getVendorDetails(@Path("id") id: String): Response<VendorDetailsModel>

    @GET("get_faq")
    suspend fun getFAQ(): Response<FaqModel>

    @GET("get_coupon_detail")
    suspend fun getCoupon(): Response<GetCouponModel>


    @Multipart
    @POST("update_profile")
    suspend fun updateProfile(
        @Header("authorization") accessToken: String,
        @Part image: MultipartBody.Part?,
        @PartMap params: MutableMap<String, RequestBody>
    ): Response<UpdateProfileModel>



    @GET("get_referal_code")
    suspend fun getReferCode(@Header("authorization") accessToken: String): Response<ReferCodeModel>

    @GET("get_achievement")
    suspend fun getAchievements(@Header("authorization") accessToken: String): Response<AchievementsModel>

    @FormUrlEncoded
    @POST("add_achievement")
    suspend fun addAchievements(@Header("authorization") accessToken: String,@FieldMap params: Map<String, String>): Response<AddAddressModel>

    @FormUrlEncoded
    @POST("attendance")
    suspend fun markAttendance(@Header("authorization") accessToken: String,@FieldMap params: Map<String, String>): Response<AttendanceModel>

    @FormUrlEncoded
    @POST("update_achievement")
    suspend fun updateAchievement(@Header("authorization") accessToken: String,@FieldMap params: Map<String, String>): Response<AddDeleteAchievementsModel>

    @GET("delete_achievement/{id}")
    suspend fun deleteAchievements(@Header("authorization") accessToken: String,@Path("id") id:String): Response<AddDeleteAchievementsModel>

    @GET("star_icon_detail/{id}")
    suspend fun getStarIcon(@Path("id") id: String): Response<StarIconModel>

    @GET("trainer_detail/{id}")
    suspend fun getTrainerDetails(@Path("id") id: String): Response<TrainerModel>

    @FormUrlEncoded
    @POST("get_pages")
    suspend fun getSlugs(@Field("id")id:String): Response<SlugModel>

    @GET("get_state")
    suspend fun getState(): Response<StateModel>

    @FormUrlEncoded
    @POST("get_city")
    suspend fun getCities(@Field("state_id")id:String): Response<CityModel>

    @GET("view_organization/{id}")
    suspend fun getOrganisationDetails(@Path("id") id: String): Response<VendorDetailsModel>

    @FormUrlEncoded
    @POST("check_out")
    suspend fun checkOut(
        @Header("Authorization")  accessToken: String,
        @FieldMap params: Map<String, String>
    ): Response<CheckOutModel>

    @GET("get_user_notification")
    suspend fun getNotifications(
        @Header("Authorization") accessToken: String,
    ): Response<NotificationModel>

    @FormUrlEncoded
    @POST("add_to_cart")
    suspend fun addToCart(@FieldMap params: Map<String, String>): Response<AddToCartModel>

    @FormUrlEncoded
    @POST("add_address")
    suspend fun addAddress(
        @Header("authorization") accessToken: String,
        @FieldMap params: Map<String, String>): Response<AddAddressModel>

    @FormUrlEncoded
    @POST("update_address")
    suspend fun updateAddress(
        @Header("authorization") accessToken: String,
        @FieldMap params: Map<String, String>): Response<AddAddressModel>

    @GET("get_user_address")
    suspend fun getUserAddress(
        @Header("authorization") accessToken: String): Response<GetAddressModel>

    @GET("remove_address/{id}")
    suspend fun removeAddress(
        @Header("authorization") accessToken: String,@Path("id") id:String): Response<AddAddressModel>

    @GET("get_profile")
    suspend fun getProfile(@Header("authorization") accessToken: String): Response<GetProfileModel>

    @FormUrlEncoded
    @POST("get_search_suggestion")
    suspend fun getSearchSuggestion(@FieldMap params: Map<String, String>): Response<SearchSuggestionModel>

    @GET("get_question_answers")
    suspend fun getFaqQuestion(): Response<ChatModel>


    @POST("get_next_question_answers")
    suspend fun getNextFaqQuestion(@QueryMap params: Map<String, String>): Response<ChatModel>

    @FormUrlEncoded
    @POST("chat_post_data")
    suspend fun submitAllQuestions(
        @Header("authorization") accessToken: String,
        @FieldMap params: Map<String, String>): Response<SubmitAllChatModel>

    @FormUrlEncoded
    @POST("get_category_wise_organization")
    suspend fun organisationList(
        @Header("authorization") accessToken: String,
        @FieldMap params: Map<String, String>): Response<OrganisationListModel>

    @GET("distancematrix/json")
    suspend fun getDistance(@QueryMap params: Map<String, String>): Response<DistanceMatrixModel>

    @GET("get_city_filter")
    suspend fun getFilterCities(): Response<FilterCitiesModel>

    @FormUrlEncoded
    @POST("reorder_package")
    suspend fun reOrder(@Header("authorization") accessToken: String, @FieldMap params: Map<String, String>): Response<LoginModel>

}