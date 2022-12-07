package com.gym.gymapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gym.gymapp.ui.productDetails.model.PackageStartTime
import com.gym.gymapp.ui.productDetails.model.PriceDuration


class Converter {

    @TypeConverter
    fun restoreList(listOfString: String?): List<String?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun priceDuration(listOfString: String?): List<PriceDuration?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<PriceDuration?>?>() {}.type)
    }

    @TypeConverter
    fun priceDurationList(listOfString: List<PriceDuration?>?): String? {
        return Gson().toJson(listOfString)
    }


}