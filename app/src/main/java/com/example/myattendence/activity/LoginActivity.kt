package com.example.myattendence.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myattendence.databinding.ActivityLoginBinding
import com.example.myattendence.model.UserModel
import com.example.myattendence.utils.Constant
import com.example.myattendence.utils.ProgressDialogSingleton
import com.example.myattendence.utils.RetrofitInstance
import com.example.myattendence.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkRememberValue()
        binding.rlLogin.setOnClickListener {
            validateData()
        }

        binding.txtShow.setOnClickListener(View.OnClickListener {
            if(binding.userPassword.inputType == 144){
                binding.userPassword.setInputType(129)
                binding.txtShow.setText("Hide")
            }
            else{
                binding.userPassword.setInputType(144)
                binding.txtShow.setText("Show")
            }
            binding.userPassword.setSelection(binding.userPassword.text.length)
        })
    }

    private fun checkRememberValue() {
        val iscredentialSave = SharedPref.getBoolean(Constant.isSaved)
        if(iscredentialSave) {
            binding.userEmail.setText(SharedPref.getString(Constant.emailid))
            binding.userPassword.setText(SharedPref.getString(Constant.password))
            binding.reminderchkBox.isChecked = true
        }
    }

    private fun validateData() {
        val inputTextEmail = binding.userEmail.text.toString()
        val inputTextPassword = binding.userPassword.text.toString()
        if(inputTextEmail.isEmpty()){
            binding.userEmail.setError("Required")
            binding.userEmail.requestFocus()
            return
        }
        if (inputTextPassword.isEmpty()){
            binding.userPassword.setError("Required")
            binding.userPassword.requestFocus()
            return
        }
        if(binding.reminderchkBox.isChecked){
            SharedPref.saveBoolean(Constant.isSaved,true)
            SharedPref.saveString(Constant.emailid, binding.userEmail.text.toString())
            SharedPref.saveString(Constant.password, binding.userPassword.text.toString())
        }
            callLoginAPI(inputTextEmail, inputTextPassword)
    }

    private fun callLoginAPI(inputTextEmail: String, inputTextPassword: String) {
        val progressDialog = ProgressDialogSingleton.getInstance(this)
        progressDialog.show()
        RetrofitInstance.retrofit.getUser("login", inputTextEmail, inputTextPassword).enqueue(object : Callback<UserModel>{
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                progressDialog.dismiss()
                val userModel : UserModel? = response.body()!!
                if(userModel != null) {
                    if(userModel.status.equals("1") ) {
                        SharedPref.getInstance(this@LoginActivity)
                        SharedPref.saveBoolean(Constant.islogin, true)
                        val gson = Gson()
                        val data = gson.toJson(userModel)
                        SharedPref.saveString(Constant.userModel, data)
                        openDash()
                    }
                    else{
                        Toast.makeText(this@LoginActivity, "Please enter correct credintial", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@LoginActivity, "User Login Not successfully", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@LoginActivity, "Failure", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openDash() {
        val loginActivityIntent = Intent(this@LoginActivity, HomeActivity :: class.java)
        startActivity(loginActivityIntent)
        finish()
    }
}