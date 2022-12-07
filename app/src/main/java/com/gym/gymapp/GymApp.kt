package com.gym.gymapp

import android.app.Application
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GymApp:Application(){

    private val ONESIGNAL_APP_ID = "8fcfe614-07b6-48f4-b959-50d91606a101"
    override fun onCreate() {
        super.onCreate()
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}