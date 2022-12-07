package com.gym.gymapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SessionManager(var mcxt: Context) {

    companion object {
        val PREF_NAME = "AGILE"
        val PREF_GENERAL = "PREF_GENERAL"
        val KEY_ISLOGIN = "isloggedin"
        const val KEY_LOGIN_IMAGE = "loginimage"
        const val KEY_LOGIN_NAME = "loginname"
        const val KEY_LOGIN_EMAIL = "loginemail"
        const val KEY_LOGIN_ID = "loginid"
        const val KEY_TOKEN = "token"
        const val KEY_DEVICE_ID = "deviceid"
        const val KEY_PLAYER_ID = "playerid"
        const val KEY_LATITUDE = "user_current_lat"
        const val KEY_LONGITUDE = "user_current_lon"
        const val KEY_CART_COUNT = "cart_count"

        const val KEY_PINCODE = "pincode"
        const val KEY_CITY = "city"
        const val KEY_STATE = "state"
        const val KEY_COUNTRY = "country"
        const val KEY_ADDRESS = "address"
        const val KEY_MOBILE = "mobile"

        const val KEY_CART_ID = "cart_id"
        const val KEY_CHECK_AMOUNT = "check_amount"
        const val KEY_CHECK_ADDRESS_ID = "address_id"

    }


    var generalEditor: SharedPreferences.Editor
    var generalPref: SharedPreferences

    private var PRIVATE_MODE = 0

    init {


        generalPref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        generalEditor = generalPref.edit()

    }

    var isLogin: Boolean?
        get() = generalPref.getBoolean(KEY_ISLOGIN, false)
        set(status) {
            generalEditor.putBoolean(KEY_ISLOGIN, status!!)
            generalEditor.commit()
        }

    var loginImage: String?
        get() = generalPref.getString(KEY_LOGIN_IMAGE, "")
        set(image) {
            generalEditor.putString(KEY_LOGIN_IMAGE, image!!)
            generalEditor.commit()
        }

    var loginName: String?
        get() = generalPref.getString(KEY_LOGIN_NAME, "")
        set(image) {
            generalEditor.putString(KEY_LOGIN_NAME, image!!)
            generalEditor.commit()
        }

    var loginEmail: String?
        get() = generalPref.getString(KEY_LOGIN_EMAIL, "")
        set(image) {
            generalEditor.putString(KEY_LOGIN_EMAIL, image!!)
            generalEditor.commit()
        }

    var loginId: String?
        get() = generalPref.getString(KEY_LOGIN_ID, "")
        set(image) {
            generalEditor.putString(KEY_LOGIN_ID, image!!)
            generalEditor.commit()
        }

    var token: String?
        get() = generalPref.getString(KEY_TOKEN, "")
        set(token) {
            generalEditor.putString(KEY_TOKEN, token!!)
            generalEditor.commit()
        }
    var deviceId: String?
        get() = generalPref.getString(KEY_DEVICE_ID, "")
        set(token) {
            generalEditor.putString(KEY_DEVICE_ID, token!!)
            generalEditor.commit()
        }

      var playerId: String?
        get() = generalPref.getString(KEY_PLAYER_ID, "")
        set(playerId) {
            generalEditor.putString(KEY_PLAYER_ID, playerId!!)
            generalEditor.commit()
        }

    var userCurrentLat: String?
        get() = generalPref.getString(KEY_LATITUDE, "")
        set(image) {
            generalEditor.putString(KEY_LATITUDE, image!!)
            generalEditor.commit()
        }

    var userCurrentLong: String?
        get() = generalPref.getString(KEY_LONGITUDE, "")
        set(image) {
            generalEditor.putString(KEY_LONGITUDE, image!!)
            generalEditor.commit()
        }


    var isLocationEnabled: Boolean?
        get() = generalPref.getBoolean(KEY_LONGITUDE, false)
        set(status) {
            generalEditor.putBoolean(KEY_LONGITUDE, status!!)
            generalEditor.commit()
        }


    var cartCount: Int?
        get() = generalPref.getInt(KEY_CART_COUNT, 0)
        set(count) {
            generalEditor.putInt(KEY_CART_COUNT, count!!)
            generalEditor.commit()
        }

    var mobile: String?
        get() = generalPref.getString(KEY_MOBILE, "")
        set(mobile) {
            generalEditor.putString(KEY_MOBILE, mobile!!)
            generalEditor.commit()
        }
    var city: String?
        get() = generalPref.getString(KEY_CITY, "")
        set(city) {
            generalEditor.putString(KEY_CITY, city!!)
            generalEditor.commit()
        }

    var pinCode: String?
        get() = generalPref.getString(KEY_PINCODE, "")
        set(pincode) {
            generalEditor.putString(KEY_PINCODE, pincode!!)
            generalEditor.commit()
        }


    var address: String?
        get() = generalPref.getString(KEY_ADDRESS, "")
        set(pincode) {
            generalEditor.putString(KEY_ADDRESS, pincode!!)
            generalEditor.commit()
        }

    var state: String?
        get() = generalPref.getString(KEY_STATE, "")
        set(state) {
            generalEditor.putString(KEY_STATE, state!!)
            generalEditor.commit()
        }

    var country: String?
        get() = generalPref.getString(KEY_COUNTRY, "")
        set(country) {
            generalEditor.putString(KEY_COUNTRY, country!!)
            generalEditor.commit()
        }

    var checkOutAmount: String?
        get() = generalPref.getString(KEY_CHECK_AMOUNT, "")
        set(country) {
            generalEditor.putString(KEY_CHECK_AMOUNT, country!!)
            generalEditor.commit()
        }
    var checkOutAddressId: String?
        get() = generalPref.getString(KEY_CHECK_ADDRESS_ID, "")
        set(country) {
            generalEditor.putString(KEY_CHECK_ADDRESS_ID, country!!)
            generalEditor.commit()
        }

    var cartId: String?
        get() = generalPref.getString(KEY_CART_ID, "")
        set(country) {
            generalEditor.putString(KEY_CART_ID, country!!)
            generalEditor.commit()
        }
}