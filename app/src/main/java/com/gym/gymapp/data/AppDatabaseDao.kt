package com.gym.gymapp.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gym.gymapp.ui.productDetails.model.PackageDetailsData
import com.gym.gymapp.ui.productDetails.model.WishListPackageData
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishList(productData: WishListPackageData)

    @Query("SELECT * from wishListTable")
    fun getWishListData(): Flow<List<WishListPackageData>>

    @Query("DELETE FROM wishListTable WHERE id =:id")
    suspend fun removeProductFromWishList(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM wishListTable WHERE id = :id)")
    suspend fun isProductAddedInWishList(id : Int) : Boolean
}