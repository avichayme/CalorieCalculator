package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var birthDateEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var  genderLayout: RadioGroup
    private lateinit var  activityFactorLayout: RadioGroup
    private lateinit var user: User
    private val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Log.d(TAG, "[onCreate] initialize activity")
        user = User(intent.getStringExtra("user_id")!!)
//        getUserInfo()

        birthDateEditText = findViewById(R.id.birthDateEditText)
        registerButton = findViewById(R.id.registerButton)
        genderLayout = findViewById(R.id.genderLayout)
        activityFactorLayout = findViewById(R.id.activityFactorLayout)

        birthDateEditText.addTextChangedListener(DateTextWatcher())
        registerButton.setOnClickListener(this)
    }

    private fun getUserInfo() {

        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> setUserInfo()
        }
    }

    private fun setUserInfo() {
        user.birthDate =
            LocalDate.parse(birthDateEditText.text.toString().replace("/", "-"), formatter)
        user.height = findViewById<EditText>(R.id.heightEditText).text.toString().toInt()
        user.weight = findViewById<EditText>(R.id.weightEditText).text.toString().toInt()
        user.gender = findViewById<RadioButton>(genderLayout.checkedRadioButtonId).text.toString()
        user.activityFactor =
            findViewById<RadioButton>(activityFactorLayout.checkedRadioButtonId).text.toString()
                .toDouble()

        Log.d(TAG, "[setUserInfo] User info was updated:\n$user")
        user.saveToDatabase()
        finish()
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}