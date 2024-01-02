package com.example.myattendence.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.myattendence.databinding.ActivityViewLeaveBinding

class ViewLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewLeaveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityViewLeaveBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.backBtn.setOnClickListener(View.OnClickListener {
            val viewActivityIntent = Intent(this@ViewLeaveActivity, HomeActivity::class.java)
            startActivity(viewActivityIntent)
        })
    }
}