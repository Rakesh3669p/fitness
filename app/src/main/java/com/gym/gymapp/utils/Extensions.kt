package com.gym.gymapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gym.gymapp.R
import com.gym.gymapp.ui.account.model.ProfileData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DecimalFormat
import kotlin.math.*


//const val BASE_URL = "https://agilit-i.com/demo/api/"
const val BASE_URL = "https://zolistic.in/api/"
const val DISTANCE_MATRIX = "https://maps.googleapis.com/maps/api/"
const val DISTANCE_MATRIX_KEY = "AIzaSyB6ylNkUPcwkGLjC_7dgcemPsg-mU4irXs"
const val REQUEST_CODE_LOCATION = 101


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun getRequestBodyFromString(value: String): Any {
    return value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
}

fun getRequestBodyFromFile(key: String, file: File?): MultipartBody.Part? {
    var imagePart: MultipartBody.Part? = null
    if (file != null) {
        val imageBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        imagePart = MultipartBody.Part.createFormData(key, file.name, imageBody)
    }
    return imagePart
}


@SuppressLint("CheckResult")
fun requestOption(): RequestOptions {
    return RequestOptions().apply {
        placeholder(R.drawable.place_holder)
        error(R.drawable.place_holder)
        diskCacheStrategy(DiskCacheStrategy.ALL)
    }
}

fun isPermissionsGiven(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun openKeyBoard(context: Context) {
    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun closeKeyBoard(context: Context) {
    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun distance(
    sourceLat: Double,
    sourceLon: Double,
    destinationLat: Double,
    destinationLon: Double
): Double {
    val theta = sourceLon - destinationLon
    var dist = (sin(deg2rad(sourceLat))
            * sin(deg2rad(destinationLat))
            + (cos(deg2rad(sourceLat))
            * cos(deg2rad(destinationLat))
            * cos(deg2rad(theta))))
    dist = acos(dist)
    dist = rad2deg(dist)
    dist *= 60 * 1.1515
    return round(dist, 2)
}

fun round(value: Double, places: Int): Double {
    var value = value
    require(places >= 0)
    val factor = 10.0.pow(places.toDouble()).toLong()
    value *= factor
    val tmp = value.roundToInt()
    return tmp.toDouble() / factor
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

fun calculationByDistance(
    startLat: Double,
    startLong: Double,
    endLat: Double,
    endLong: Double
): Double {
    val Radius = 6371 // radius of earth in Km

    val dLat = Math.toRadians(startLong - startLat)
    val dLon = Math.toRadians(endLong - endLat)
    val a = (sin(dLat / 2) * sin(dLat / 2)
            + (cos(Math.toRadians(startLat))
            * cos(Math.toRadians(startLong)) * Math.sin(dLon / 2)
            * sin(dLon / 2)))
    val c = 2 * asin(sqrt(a))
    val valueResult = Radius * c
    val km = valueResult / 1
    val newFormat = DecimalFormat("####")
    val kmInDec: Int = Integer.valueOf(newFormat.format(km))
    val meter = valueResult % 1000
    val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
    Log.i(
        "Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec
    )
    return Radius * c
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = this.convertDpToPx(50F).roundToInt()
    return heightDiff > marginOfError
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

val updatedProfile = MutableLiveData<ProfileData>()

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.makeGone() {
    this.visibility = View.GONE
}