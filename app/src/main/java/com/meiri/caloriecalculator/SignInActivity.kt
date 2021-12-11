package com.meiri.caloriecalculator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity


class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var birthDateEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var genderLayout: RadioGroup
    private lateinit var activityFactorLayout: RadioGroup
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Log.d(TAG, "[onCreate] initialize activity")
        user = User(intent.getStringExtra("user_id")!!)

        birthDateEditText = findViewById(R.id.birth_date_edit_text)
        registerButton = findViewById(R.id.register_button)
        genderLayout = findViewById(R.id.gender_layout)
        activityFactorLayout = findViewById(R.id.activity_factor_layout)

        birthDateEditText.addTextChangedListener(DateTextWatcher())
        registerButton.setOnClickListener(this)
    }

    private fun returnUserInfo() {
        val userData: HashMap<String, Any> = HashMap()
        userData["birth_date"] = birthDateEditText.text.toString()
        userData["height"] = findViewById<EditText>(R.id.height_edit_text).text.toString().toLong()
        userData["weight"] = findViewById<EditText>(R.id.weight_edit_text).text.toString().toLong()
        userData["gender"] = findViewById<RadioButton>(genderLayout.checkedRadioButtonId).text.toString()
        userData["activity_factor"] = findViewById<RadioButton>(activityFactorLayout.checkedRadioButtonId).text.toString().toDouble()

        val resultIntent = Intent()
        resultIntent.putExtra("user_data", userData)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> returnUserInfo()
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}