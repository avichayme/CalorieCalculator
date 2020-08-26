package com.meiri.caloriecalculator

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class SignInActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var user: User
    private val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Log.d(TAG, "[onCreate] initialize activity")
        user = User(intent.getStringExtra("user_id")!!)
        initializeCalendar()
//        getUserInfo()

        birthDateEditText.onFocusChangeListener = this
        birthDateEditText.setOnClickListener(this)
        signInButton.setOnClickListener(this)
    }

    private fun getUserInfo() {

        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        when (v) {
            birthDateEditText -> pickADate()
            signInButton -> setUserInfo()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            birthDateEditText -> if (hasFocus) pickADate()
        }
    }

    private fun initializeCalendar() {
        // calender class's instance and get current date , month and year from calender
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        // date picker dialog
        datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day -> // set date in the edit text
                birthDateEditText.setText("$day/${month + 1}/$year")
            }, mYear, mMonth, mDay
        )

        Log.d(TAG, "[initializeCalendar] TEST - mYear = $mYear, mMonth = $mMonth mDay = $mDay")
        datePickerDialog.datePicker.maxDate = c.timeInMillis
        c.set(1970, 1, 1)
        Log.d(TAG, "[initializeCalendar] TEST - mYear = $mYear, mMonth = $mMonth mDay = $mDay")
        datePickerDialog.datePicker.minDate = c.timeInMillis
    }

    private fun pickADate() {
        datePickerDialog.show()
    }

    private fun setUserInfo() {
        user.birthDate =
            LocalDate.parse(birthDateEditText.text.toString().replace("/", "-"), formatter)
        user.height = heightEditText.text.toString().toInt()
        user.weight = weightEditText.text.toString().toInt()
        user.gender = findViewById<RadioButton>(genderLayout.checkedRadioButtonId).text.toString()
        user.activityFactor =
            findViewById<RadioButton>(activityFactorLayout.checkedRadioButtonId).text.toString()
                .toDouble()

        Log.d(TAG, "[setUserInfo] User info was updated:\n$user")
        user.saveToDatabase()
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}