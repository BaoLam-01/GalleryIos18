package com.example.galleryios18.data.network

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {

        private fun getRetrofitInstance(url: String): Retrofit {
            return Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }


    }
}