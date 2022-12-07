package com.gym.gymapp.data

import androidx.room.Database
import androidx.room.Ignore
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gym.gymapp.ui.productDetails.model.PackageDetailsData
import com.gym.gymapp.ui.productDetails.model.WishListPackageData


@Database(
    entities = [PackageDetailsData::class, WishListPackageData::class],
    version = 5
)

@TypeConverters(Converter::class)
abstract class AppDataBase:RoomDatabase() {
    abstract fun getAppDao() :AppDatabaseDao
}