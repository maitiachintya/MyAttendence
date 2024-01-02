package com.example.myattendence.utils

import android.app.ProgressDialog
import android.content.Context

class ProgressDialogSingleton private constructor(context: Context){
    private val progressDialog : ProgressDialog = ProgressDialog(context)
    init {
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setMessage("Loading..")
    }
    fun show(){
        if(progressDialog.isShowing){
            progressDialog.show()
        }
    }

    fun dismiss(){
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
    }

    companion object{
        @Volatile
        private var instance : ProgressDialogSingleton? = null
        fun getInstance(context: Context) : ProgressDialogSingleton{
            return instance ?: synchronized(this){
                instance ?: ProgressDialogSingleton(context).also {
                    instance = it
                }
            }
        }
    }
}