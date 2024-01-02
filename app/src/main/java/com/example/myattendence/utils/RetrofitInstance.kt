package com.example.myattendence.utils

import com.example.myattendence.interfaceclass.APIInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ConstanceClass.base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface :: class.java)
    }
   /* val retrofit1 by lazy {
        Retrofit.Builder()
            .baseUrl(ConstanceClass.base_URL1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InterfaceAPI :: class.java)
    }*/
}