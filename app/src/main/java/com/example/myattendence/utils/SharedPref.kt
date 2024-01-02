package com.example.myattendence.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPref private constructor(){
    companion object{
        private lateinit var sharedPreferences: SharedPreferences
        private val LOGIN = "login"
        fun getInstance(context: Context) : SharedPreferences {
            if(!Companion :: sharedPreferences.isInitialized){
                synchronized(SharedPref :: class.java){
                    if(!Companion :: sharedPreferences.isInitialized){
                        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
                    }
                }
            }
            return sharedPreferences
        }
        fun saveString(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        fun getString(key: String, defaultValue: String = ""): String {
            return sharedPreferences.getString(key, defaultValue) ?: defaultValue
        }

        fun saveInt(key: String, value: Int) {
            sharedPreferences.edit().putInt(key, value).apply()
        }

        fun getInt(key: String, defaultValue: Int = 0): Int {
            return sharedPreferences.getInt(key, defaultValue)
        }

        fun saveBoolean(key: String, value: Boolean) {
            sharedPreferences.edit().
            putBoolean(key, value).
            apply()
        }

        fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
            return sharedPreferences.getBoolean(key, defaultValue)
        }

        fun clearPreferences() {
            sharedPreferences.edit().clear().apply()
        }
    }
}