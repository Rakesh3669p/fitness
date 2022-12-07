package com.gym.gymapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DataStoreManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "GYM_DATASTORE")

    companion object {

        val NAME = stringPreferencesKey("NAME")
        val PHONE_NUMBER = stringPreferencesKey("PHONE_NUMBER")
        val ADDRESS = stringPreferencesKey("ADDRESS")
        val IS_LOGIN = booleanPreferencesKey("ADDRESS")

    }

    suspend fun setLogin(status:Boolean){
        context.dataStore.edit {
            it[IS_LOGIN] = status
        }
    }

    fun isLogin(): Flow<Boolean> = context.dataStore.data.map{
            it[IS_LOGIN] ?: false
    }

    suspend fun saveToDataStore() {
        context.dataStore.edit {

            it[NAME] = "name"
            it[PHONE_NUMBER] = "phoneNumber"
            it[ADDRESS] = "address"

        }
    }

    suspend fun getFromDataStore() = context.dataStore.data.map {

         it[NAME] ?: ""
    }
}