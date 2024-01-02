package com.example.myattendence.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ak.ColoredDate
import com.ak.EventObjects
import com.ak.KalendarView
import com.example.myattendence.R
import com.example.myattendence.databinding.ActivityHomeBinding
import com.example.myattendence.model.CheckInModel
import com.example.myattendence.model.CheckOutModel
import com.example.myattendence.model.UserModel
import com.example.myattendence.utils.Constant
import com.example.myattendence.utils.ProgressDialogSingleton
import com.example.myattendence.utils.RetrofitInstance
import com.example.myattendence.utils.SharedPref
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    var datesColors: kotlin.collections.ArrayList<ColoredDate> = ArrayList()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var profileImageView : ImageView
    private lateinit var checkInButton : Button
    private val handler = Handler()
    private lateinit var checkedOutButton: Button
    private lateinit var timePicker: TimePicker
    private lateinit var userModel: UserModel
    private lateinit var popupwindow : PopupWindow
    var selectedCheckINTime = ""
    var selectedCheckedINHour = 0
    var selectedCheckedINMinute = 0
    var selectedCheckOutTime = ""
    var selectedCheckedOutHour = 0
    var selectedCheckedOutMinute = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For full Screen Visible
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        datesColors.add(ColoredDate(Date(),resources.getColor(R.color.red_holiday)))
        binding.calenderpic.setColoredDates(datesColors)
        val events: MutableList<EventObjects> = ArrayList()
        events.add(EventObjects("meeting", Date()))
        binding.calenderpic.setEvents(events)
        binding.calenderpic.setDateSelector(KalendarView.DateSelector {
             fun onDateClicked(selectedDate : Date){
                Log.d("DateSel",selectedDate.toString());
            }
        })

        /*binding.calenderpic.setMonth { changedMonth ->
            Log.d(
                "Changed",
                "month changed $changedMonth"
            )
        }*/
        val tempCal = Calendar.getInstance()
        tempCal[Calendar.DATE] = 11
        val events2: MutableList<EventObjects> = ArrayList()
        events2.add(EventObjects("meeting", tempCal.time))
        tempCal[Calendar.DATE] = 15
        val datesColors2: MutableList<ColoredDate> = ArrayList()
        datesColors2.add(ColoredDate(tempCal.time, resources.getColor(R.color.red_holiday)))

        binding.timechkin.setOnClickListener(View.OnClickListener {
            openCheckInPopUp()
        })

        binding.timechkout.setOnClickListener(View.OnClickListener {
            openCheckOutPopUp()
        })

        profileImageView = binding.napvw.getHeaderView(0).findViewById(R.id.profilepic)
        profileImageView.setOnClickListener(View.OnClickListener {
            val homeActivityIntent = Intent(this@HomeActivity, ProfileActivity :: class.java)
            startActivity(homeActivityIntent)
        })

        val gson = Gson()
        val data = SharedPref.getString(Constant.userModel, "")
        userModel = gson.fromJson(data, UserModel :: class.java)

        //Drawer layout open
        binding.drawlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        showUserDetails(userModel)

        binding.dotmenu.setOnClickListener(View.OnClickListener {
            binding.drawlayout.openDrawer(Gravity.LEFT)
        })

        binding.napvw.setNavigationItemSelectedListener(this)
    }

    private fun openCheckOutPopUp() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupview = inflater.inflate(R.layout.checked_out_popup,null)
        checkedOutButton = popupview.findViewById(R.id.chk_out_btn)
        var mcurrentTime = Calendar.getInstance()
        selectedCheckedOutHour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        selectedCheckedOutMinute = mcurrentTime.get(Calendar.MINUTE)
        selectedCheckOutTime = "Hour: "+ selectedCheckedOutHour + " Minute : "+ selectedCheckedOutMinute
        val checkOutTime = selectedCheckedOutHour.toString() +":"+selectedCheckedOutMinute.toString()
        SharedPref.saveString(Constant.CHECK_OUT_TIME_KEY, checkOutTime)
        timePicker = popupview.findViewById(R.id.out_timePicker)
        timePicker.setOnTimeChangedListener(TimePicker.OnTimeChangedListener{ timePicker, hour, minute ->
            updateCheckoutTimeAvailability()
            selectedCheckedOutHour = hour
            selectedCheckedOutMinute = minute
            selectedCheckOutTime = "Hour: "+ hour + " Minute : "+ minute
        })

        checkedOutButton.setOnClickListener(View.OnClickListener {
            mcurrentTime = Calendar.getInstance()
            mcurrentTime?.set(Calendar.HOUR_OF_DAY, selectedCheckedOutHour)
            mcurrentTime?.set(Calendar.MINUTE, selectedCheckedOutMinute)
            if(!selectedCheckOutTime.equals("")){
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedCheckedOutHour,selectedCheckedOutMinute)
                binding.tvwTimechkout.setText("Selected Time:  "+selectedTime.toString())
                popupwindow.dismiss()
            }
            else{
                Toast.makeText(this, "Please select check in time", Toast.LENGTH_LONG).show()
            }
        })
        popupwindow = PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        popupwindow.showAtLocation(popupview,0,0,0)
        val closeButton = popupview.findViewById<ImageView>(R.id.close_btn)
        closeButton.setOnClickListener(View.OnClickListener {
            popupwindow.dismiss()
        })
    }

    private fun updateCheckoutTimeAvailability() {
        val selectedCheckInTime = getSelectedTime(binding.timechkin)
        val selectedCheckOutTime = getSelectedTime(binding.timechkout)
        val currentTime = Calendar.getInstance()
        if (selectedCheckInTime != null) {
            val timeDifference = calculateTimeDifference(selectedCheckInTime, currentTime)
            if (timeDifference >= 8) {
                updateCheckoutTimeLabel(selectedCheckInTime)
                callCheckOutAPI()
            } else {
                setDefaultCheckoutTimeLabel()
                Toast.makeText(this@HomeActivity, "You can't select check-out time less than 8 hours.", Toast.LENGTH_LONG).
                show()
            }
        }
    }

    private fun getSelectedTime(timechkin: ImageView): Calendar {
        val hour = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.hour
        } else {
            timePicker.currentHour
        }
        val minute = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.minute
        } else {
            timePicker.currentMinute
        }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
           // set(Calendar.AM_PM, selectedCheckedINMinute)
        }
    }

    private fun calculateTimeDifference(selectedCheckInTime: Calendar, selectedCheckOutTime: Calendar) : Int{
        val format = SimpleDateFormat("HH:mm a")
        val checkInTime = SharedPref.getString(Constant.CHECK_IN_TIME_KEY)
        val checkOutTime = SharedPref.getString(Constant.CHECK_OUT_TIME_KEY)
        val date1 = format.parse(checkInTime)
        val date2 = format.parse(checkOutTime)
        val difference = date2.time - date1.time
        val numOfHour : Int = (difference / (1000 * 60 * 60)).toInt()
        return numOfHour
    }

    private fun setDefaultCheckoutTimeLabel() {
        binding.timechkout.contentDescription = "Select Check Out time"
    }

    private fun updateCheckoutTimeLabel(selectedCheckInTime: Calendar) {
        val minCheckoutTime = Calendar.getInstance().apply {
            time = selectedCheckInTime.time
            add(Calendar.HOUR_OF_DAY, 8)
        }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(minCheckoutTime.time)
        binding.timechkout.contentDescription = "Select Check Out time (Minimum: $formattedTime)"
    }

    private fun callCheckOutAPI() {
        val progressDialog = ProgressDialogSingleton.getInstance(this)
        progressDialog.show()
        RetrofitInstance.retrofit.chkdOut(Constant.checkedOut, userModel.ecode, Constant.timeZone, Constant.actionsType).enqueue(object : Callback<CheckOutModel>{
            override fun onResponse(call: Call<CheckOutModel>, response: Response<CheckOutModel>) {
                progressDialog.dismiss()
                val checkOutModel : CheckOutModel? = response.body()!!
                if(checkOutModel != null){
                    if(checkOutModel.status.equals("1")){
                        SharedPref.getInstance(this@HomeActivity)
                        SharedPref.saveBoolean(Constant.isCheckedInPref, true)
                        val gson = Gson()
                        val data = gson.toJson(checkOutModel)
                        SharedPref.saveString(Constant.isCheckedInPref, data)
                        Toast.makeText(this@HomeActivity,"Check OUT Time logged successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this@HomeActivity, "Please checked valid time", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@HomeActivity, "Time does not set successfully", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CheckOutModel>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@HomeActivity, "Failure", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openCheckInPopUp() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupview = inflater.inflate(R.layout.check_in_popup,null)
        checkInButton = popupview.findViewById(R.id.chk_in_btn)
        var mcurrentTime = Calendar.getInstance()
        selectedCheckedINHour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        selectedCheckedINMinute = mcurrentTime.get(Calendar.MINUTE)
        selectedCheckINTime = "Hour: "+ selectedCheckedINHour + " Minute : "+ selectedCheckedINMinute
        timePicker = popupview.findViewById(R.id.timePicker)
        timePicker.setOnTimeChangedListener(TimePicker.OnTimeChangedListener{ timePicker, hour, minute ->
            binding.timechkout.visibility = View.VISIBLE
            binding.tvwTimechkout.visibility = View.VISIBLE
            selectedCheckedINHour = hour
            selectedCheckedINMinute = minute
            val checktime = selectedCheckedINHour.toString() +":"+selectedCheckedINMinute.toString()
            SharedPref.saveString(Constant.CHECK_IN_TIME_KEY, checktime)
            selectedCheckINTime = "Hour: "+ hour + " Minute : "+ minute
        })

        checkInButton.setOnClickListener(View.OnClickListener {
            mcurrentTime = Calendar.getInstance()
            mcurrentTime?.set(Calendar.HOUR_OF_DAY, selectedCheckedINHour)
            mcurrentTime?.set(Calendar.MINUTE, selectedCheckedINMinute)
            if(!selectedCheckINTime.equals("")){
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedCheckedINHour,selectedCheckedINMinute)
                binding.tvwTimechkin.setText("Selected Time:  "+selectedTime)
                popupwindow.dismiss()
                callCheckInAPI()
            }
            else{
                Toast.makeText(this, "Please select check in time", Toast.LENGTH_LONG).show()
            }
        })
        popupwindow = PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        // Show the pop-up window
        popupwindow.showAtLocation(popupview, ViewGroup.LayoutParams.MATCH_PARENT, 0, 0)
        val closeButton = popupview.findViewById<ImageView>(R.id.close_btn)
        closeButton.setOnClickListener(View.OnClickListener {
            popupwindow.dismiss()
        })
    }

    private fun callCheckInAPI() {
        val progressDialog : ProgressDialog = ProgressDialog(this)
        progressDialog.show()
        RetrofitInstance.retrofit.chkdIn(Constant.checkedIn,userModel.ecode,Constant.timeZone, Constant.actionType).enqueue(object : Callback<CheckInModel>{
            override fun onResponse(call : Call<CheckInModel>, response: Response<CheckInModel>) {
                progressDialog.dismiss()
                val checkInModel : CheckInModel? = response.body()!!
                if(checkInModel != null){
                    if(checkInModel.status.equals("1")){
                        SharedPref.getInstance(this@HomeActivity)
                        SharedPref.saveBoolean(Constant.isCheckedInPref, true)
                        val gson = Gson()
                        val data = gson.toJson(checkInModel)
                        SharedPref.saveString(Constant.isCheckedInPref, data)
                        Toast.makeText(this@HomeActivity,"Your Check-IN Time selected successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this@HomeActivity, "Please checked valid time", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@HomeActivity, "Time does not set successfully", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call : Call<CheckInModel>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@HomeActivity, "Failure", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showUserDetails(userModel : UserModel?) {
        val header = binding.napvw.getHeaderView(0)
        val username =header.findViewById<TextView>(R.id.user_name)
        username?.text = "Mr. "+ userModel?.name
    }

    //Logout Process
    private fun logout() {
        var email = ""
        var password = ""
        val iscredentialSave = SharedPref.getBoolean(Constant.isSaved)
        if(iscredentialSave){
             email = SharedPref.getString(Constant.emailid)
             password = SharedPref.getString(Constant.password)
        }
        SharedPref.clearPreferences()
        SharedPref.saveString(Constant.emailid, email)
        SharedPref.saveString(Constant.password, password)
        SharedPref.saveBoolean(Constant.isSaved, iscredentialSave)
        val loginActivityIntent = Intent(this@HomeActivity, LoginActivity :: class.java)
        loginActivityIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
        loginActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginActivityIntent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.views ->{
                val homeActivityIntent = Intent(this@HomeActivity, ViewLeaveActivity :: class.java)
                startActivity(homeActivityIntent)
            }
        }
        when(item.itemId){
            R.id.website ->{
                val homeActivityIntent = Intent(this@HomeActivity, ApplyLeaveActivity :: class.java)
                startActivity(homeActivityIntent)
            }
        }
        when(item.itemId){
            R.id.chkin ->{
                val checkIntent = Intent(this@HomeActivity, CheckInActivity :: class.java)
                startActivity(checkIntent)
            }
        }
        when(item.itemId){
            R.id.chkout ->{
                val checkOutActivityIntent = Intent(this@HomeActivity, CheckOutActivity :: class.java)
                startActivity(checkOutActivityIntent)
            }
        }
        when (item.itemId){
            R.id.logout -> {
                logout()
            }
        }
        binding.drawlayout.closeDrawer(GravityCompat.START);
        return true;
    }
}