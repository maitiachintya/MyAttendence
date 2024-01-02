package com.example.myattendence.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myattendence.R
import com.example.myattendence.databinding.ActivityProfileBinding
import com.example.myattendence.model.CheckOutModel
import com.example.myattendence.model.ProfileModel
import com.example.myattendence.utils.Constant
import com.example.myattendence.utils.ProgressDialogSingleton
import com.example.myattendence.utils.RetrofitInstance
import com.example.myattendence.utils.SharedPref
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val imageUrl = "https://www.w3schools.com/w3images/avatar2.png"
    val Request_PH = 2907
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener(View.OnClickListener {
            val profileActivityIntent = Intent(this@ProfileActivity, HomeActivity :: class.java)
            startActivity(profileActivityIntent)
        })

        //Picasso Library Build
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.avatarprofile) // Optional placeholder image while loading
            .error(R.drawable.warning) // Optional error image if the load fails
            .into(binding.imgCard)

        callAPI()

        binding.txtContact.setOnClickListener(View.OnClickListener {
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission( this@ProfileActivity, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(Manifest.permission.CALL_PHONE),Request_PH)
                }
                else{
                    val number = binding.txtContact.text.toString()
                    val call = Uri.parse("tel: " + number)
                    val callActivityIntent = Intent(Intent.ACTION_CALL, call)
                    startActivity(callActivityIntent)
                }
            }
            else{
                val number = binding.txtContact.text.toString()
                val call = Uri.parse("tel: " + number)
                val callActivityIntent = Intent(Intent.ACTION_CALL, call)
                startActivity(callActivityIntent)
            }
        })
    }

    private fun callAPI() {
        val progressDialog = ProgressDialogSingleton.getInstance(this)
        progressDialog.show()
        RetrofitInstance.retrofit.viewProfile("employeedetail", "2").enqueue(object : Callback<ProfileModel>{
            override fun onResponse(call: Call<ProfileModel>, response: Response<ProfileModel>) {
                progressDialog.dismiss()
                val profileModel : ProfileModel? = response.body()!!
                if(profileModel != null){
                    if(profileModel.status.equals("1")){
                        SharedPref.getInstance(this@ProfileActivity)
                        SharedPref.saveBoolean(Constant.clickImg, true)
                        val gson = Gson()
                        val data = gson.toJson(profileModel)
                        SharedPref.saveString(Constant.clickImg, data)
                        Toast.makeText(this@ProfileActivity,"Profile Button logged successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this@ProfileActivity, "Please checked proper ID", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@ProfileActivity, "Something error!!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@ProfileActivity, "Failure", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            Request_PH -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val number = binding.txtContact.text.toString()
                    val call = Uri.parse("tel: " + number)
                    val callActivityIntent = Intent(Intent.ACTION_CALL, call)
                    startActivity(callActivityIntent)
                } else {
                    Toast.makeText(this@ProfileActivity, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}