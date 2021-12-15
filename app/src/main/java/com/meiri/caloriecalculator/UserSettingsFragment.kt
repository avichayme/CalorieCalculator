package com.meiri.caloriecalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.core.view.children
import java.time.LocalDate


class UserSettingsFragment : Fragment(), View.OnClickListener {
    private lateinit var birthDateEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightLayout: EditText
    private lateinit var genderLayout: RadioGroup
    private lateinit var activityFactorLayout: RadioGroup
    private lateinit var registerButton: Button
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.activity_sign_in, container, false)

        user = MainActivity.getUser()
        birthDateEditText = inputFragmentView.findViewById(R.id.birth_date_edit_text)
        heightEditText = inputFragmentView.findViewById(R.id.height_edit_text)
        weightLayout = inputFragmentView.findViewById(R.id.weight_edit_text)
        genderLayout = inputFragmentView.findViewById(R.id.gender_layout)
        activityFactorLayout = inputFragmentView.findViewById(R.id.activity_factor_layout)
        registerButton = inputFragmentView.findViewById(R.id.register_button)

        registerButton.text = "Update!"
        setUserDetails()

        registerButton.setOnClickListener(this)
        return inputFragmentView
    }

    private fun setUserDetails() {
        birthDateEditText.setText(user.getBirthDateFormatted())
        heightEditText.setText(user.height.toString())
        weightLayout.setText(user.weight.toString())
        (genderLayout.children.filter { (it as RadioButton).text.toString() == user.gender }
            .first() as RadioButton).isChecked = true
        (activityFactorLayout.children.filter {
            (it as RadioButton).hint.toString().toDouble() == user.activityFactor
        }.first() as RadioButton).isChecked = true
    }

    private fun updateUserInfo() {
        user.birthDate = LocalDate.parse(birthDateEditText.text.toString().replace("/", "-"), user.formatter)
        user.height = heightEditText.text.toString().toLong()
        user.weight = weightLayout.text.toString().toLong()
        user.gender = (genderLayout.children.first { it.id == genderLayout.checkedRadioButtonId } as RadioButton).text.toString()
        user.activityFactor = (activityFactorLayout.children.first { it.id == activityFactorLayout.checkedRadioButtonId } as RadioButton).hint.toString().toDouble()
        user.saveToDatabase()
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> updateUserInfo()
        }
    }
}