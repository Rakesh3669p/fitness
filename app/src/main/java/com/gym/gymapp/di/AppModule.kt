package com.gym.gymapp.di

import android.content.Context
import androidx.room.Room
import com.gym.gymapp.data.AppDataBase
import com.gym.gymapp.data.AppDatabaseDao
import com.gym.gymapp.network.Repository
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.AppProgressBar
import com.gym.gymapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRepository(dao:AppDatabaseDao) = Repository(dao)

    @Singleton
    @Provides
    fun provideAppProgress() = AppProgressBar()

    @Singleton
    @Provides
    fun provideAgileLoader(@ApplicationContext context: Context) = AgileLoader(context)

    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context) =
        SessionManager(context)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context):AppDataBase =
        Room.databaseBuilder(context,AppDataBase::class.java,"gym_app_db").fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun provideAppDao(db: AppDataBase) = db.getAppDao()

}