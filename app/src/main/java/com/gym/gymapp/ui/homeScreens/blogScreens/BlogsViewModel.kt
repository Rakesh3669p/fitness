package com.gym.gymapp.ui.homeScreens.blogScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.network.Repository
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsDetailsModel
import com.gym.gymapp.ui.homeScreens.blogScreens.model.GetBlogsModel
import com.gym.gymapp.ui.productListing.model.FilterCategoryModel
import com.gym.gymapp.ui.productListing.model.FilterOrganizationModel
import com.gym.gymapp.ui.productListing.model.FilterVendorModel
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BlogsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val blogsList = SingleLiveEvent<Resource<GetBlogsModel>>()
    val blogsDetails = SingleLiveEvent<Resource<BlogsDetailsModel>>()


    fun getBlogs() = viewModelScope.launch { safeGetBlogsCall() }
    fun getBlogsDetails(id:String) = viewModelScope.launch { safeBlogDetailsCall(id) }


    private suspend fun safeGetBlogsCall() {
        blogsList.postValue(Resource.Loading())
        try {
            val response = repository.getBlogsList()
            blogsList.postValue(handleFilterVendor(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> blogsList.postValue(Resource.Error("Network Failure", null))
                else -> blogsList.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleFilterVendor(response: Response<GetBlogsModel>): Resource<GetBlogsModel> {
        if (response.isSuccessful) {
            response.body()?.let { blogsListResponse ->
                return Resource.Success(blogsListResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }



    private suspend fun safeBlogDetailsCall(id: String) {
        blogsDetails.postValue(Resource.Loading())
        try {
            val response = repository.getBlogDetails(id)
            blogsDetails.postValue(handleBlogDetails(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> blogsDetails.postValue(Resource.Error("Network Failure", null))
                else -> blogsDetails.postValue(Resource.Error("Conversion Error ${t.message}", null))
            }
        }
    }

    private fun handleBlogDetails(response: Response<BlogsDetailsModel>): Resource<BlogsDetailsModel> {
        if (response.isSuccessful) {
            response.body()?.let { blogDetailsResponse ->
                return Resource.Success(blogDetailsResponse)
            }
        }
        return Resource.Error(response.message(), null)
    }


}