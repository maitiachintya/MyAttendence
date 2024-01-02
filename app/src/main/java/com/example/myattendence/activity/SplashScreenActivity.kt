package com.example.myattendence.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myattendence.R
import com.example.myattendence.utils.Constant
import com.example.myattendence.utils.SharedPref

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val imageView: ImageView = findViewById(R.id.splash_img)
        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        Glide.with(this).asGif().load(R.raw.attendance).into(imageView)
        Handler(Looper.getMainLooper()).postDelayed({
            SharedPref.getInstance(this)
            if(SharedPref.getBoolean(Constant.islogin)){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        },2000)
    }
}