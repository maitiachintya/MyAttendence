package com.example.myattendence.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myattendence.databinding.ActivityApplyLeaveBinding
import java.text.SimpleDateFormat
import java.util.*

class ApplyLeaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplyLeaveBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private var startDate: Date? = null
    private var endDate: Date? = null
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityApplyLeaveBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener(View.OnClickListener {
            val applyActivityIntent = Intent(this@ApplyLeaveActivity, HomeActivity :: class.java)
            startActivity(applyActivityIntent)
        })

        binding.strdtIcon.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(true)
        })

        binding.enddteIcon.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(false)
        })
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val currentDate = calendar.time
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.time
                if (isStartDate) {
                    if (selectedDate.before(currentDate)) {
                        Toast.makeText(this@ApplyLeaveActivity, "Start date should not be less than the current date",
                            Toast.LENGTH_LONG).show()
                        // Start date should not be less than the current date
                        // Here, we'll just set the current date as the start date
                    } else {
                        startDate = selectedDate
                    }
                } else {
                    if (startDate == null || selectedDate.before(startDate)) {
                        // End date cannot be less than the start date
                        // Here, we'll just set the start date as the end date
                        endDate = startDate
                    } else {
                        endDate = selectedDate
                    }
                }
                // Update the text on the button
                val formattedDate = dateFormat.format(selectedDate)
                if (isStartDate) {
                    binding.selecteddate.text = formattedDate
                } else {
                    binding.dateselected.text = formattedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (!isStartDate) {
            // If selecting an end date, set the minimum date to the start date (if selected)
            startDate?.let { datePickerDialog.datePicker.minDate = it.time }
        }
        else{
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        datePickerDialog.show()
    }
}