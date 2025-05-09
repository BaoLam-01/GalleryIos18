package com.example.galleryios18.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.galleryios18.common.Constant
import com.example.galleryios18.data.local.SharedPreferenceHelper
import com.tapbi.spark.launcherios18.data.repository.MediaRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSharePre(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreference(sharedPreferences: SharedPreferences): SharedPreferenceHelper {
        return SharedPreferenceHelper(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(): MediaRepository {
        return MediaRepository()
    }

}
