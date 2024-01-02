package com.example.myattendence.activity

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.myattendence.databinding.ActivityCheckOutBinding
import java.text.SimpleDateFormat
import java.util.*

class CheckOutActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
    private lateinit var binding: ActivityCheckOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.backBtn.setOnClickListener(View.OnClickListener {
            val viewActivityIntent = Intent(this@CheckOutActivity, HomeActivity :: class.java)
            startActivity(viewActivityIntent)
        })

        val checkInTime = intent.getLongExtra("checkInTime", 0)
        binding.checkTimeButton.setOnClickListener(View.OnClickListener {
            showTimePicker(checkInTime)
        })
    }

    private fun showTimePicker(checkInTime: Long) {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val checkOutTime = calendar.timeInMillis
                if (checkOutTime - checkInTime < 30 * 60 * 1000) {
                    // Check-out time is not valid (less than 30 minutes after check-in)
                    Toast.makeText(this, "Check-out Time Must Be 30 Minutes After Check-in", Toast.LENGTH_LONG).show()
                } else if (checkOutTime - checkInTime < 6 * 60 * 60 * 1000) {
                    // Check-out time is less than 6 hours after check-in
                    Toast.makeText(this, "Check-out Time Must Be 6 Hours After Check-in", Toast.LENGTH_LONG).show()
                } else {
                    binding.checkTimeButton.text = "Check-out Time: ${timeFormatter.format(calendar.time)}"
                }
            },
            currentHour, currentMinute, false)
        timePickerDialog.show()
    }
}