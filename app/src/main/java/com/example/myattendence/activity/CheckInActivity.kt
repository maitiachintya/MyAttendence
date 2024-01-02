package com.example.myattendence.activity

import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.myattendence.databinding.ActivityCheckInBinding
import com.example.myattendence.model.CheckInModel
import com.example.myattendence.utils.ConstanceClass
import com.example.myattendence.utils.Constant
import com.example.myattendence.utils.RetrofitInstance
import com.example.myattendence.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CheckInActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
    private lateinit var binding: ActivityCheckInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.backBtn.setOnClickListener(View.OnClickListener {
            val viewActivityIntent = Intent(this@CheckInActivity, HomeActivity::class.java)
            startActivity(viewActivityIntent)
        })
        binding.checkTimeButton.setOnClickListener(View.OnClickListener {
            showTimePicker()
        })
    }

    private fun showTimePicker() {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val checkInTime = calendar.timeInMillis
                // Create an Intent to pass the check-in time to the CheckOutActivity
                val intent = Intent(this, CheckOutActivity::class.java)
                intent.putExtra("checkInTime", checkInTime)
                startActivity(intent)
                binding.checkTimeButton.text = "Check-in Time: ${timeFormatter.format(calendar.time)}"
               /* if(isStartTime){
                    binding.selectetime.text = timeFormatter.toString()
                }*/
            },
            currentHour, currentMinute, false
        )
        timePickerDialog.show()
    }
}