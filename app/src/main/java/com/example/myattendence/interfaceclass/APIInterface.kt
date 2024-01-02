package com.example.myattendence.interfaceclass

import com.example.myattendence.model.CheckInModel
import com.example.myattendence.model.CheckOutModel
import com.example.myattendence.model.ProfileModel
import com.example.myattendence.model.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("/api.php?")
    open fun getUser( @Query("action") action : String,
                        @Query("username") username : String,
                            @Query("password") password : String) : Call<UserModel>

    @GET("/api.php?")
    open fun chkdIn(@Query("action") action : String,
                      @Query("ecode") ecode : String,
                      @Query("timezone") timezone : String,
                      @Query("actiontype") actiontype : String) : Call<CheckInModel>

    @GET("/api.php?")
    open fun chkdOut(@Query("action") action: String,
                        @Query("ecode") ecode: String,
                            @Query("timezone") timezone: String,
                                @Query("actiontype") actiontype: String) : Call<CheckOutModel>

    @GET("/api.php?")
    open fun viewProfile(@Query("action") action : String,
                            @Query("ecode") ecode : String) : Call<ProfileModel>
}