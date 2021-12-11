package com.meiri.caloriecalculator

//import android.R
import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment

class UserSettingsFragment : Fragment() {
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

        user = User(MainActivity.getUserID()!!)
        birthDateEditText = inputFragmentView.findViewById(R.id.birth_date_edit_text)
        heightEditText = inputFragmentView.findViewById(R.id.height_edit_text)
        weightLayout = inputFragmentView.findViewById(R.id.weight_edit_text)
        genderLayout = inputFragmentView.findViewById(R.id.gender_layout)
        activityFactorLayout = inputFragmentView.findViewById(R.id.activity_factor_layout)
        registerButton = inputFragmentView.findViewById(R.id.register_button)
        setUserDetails()

        return inputFragmentView
    }

    private fun setUserDetails() {
        birthDateEditText.setText(user.getBirthDateFormatted())
        heightEditText.setText(user.height.toString())
        weightLayout.setText(user.weight.toString())
        for (i in 0 until genderLayout.childCount) {
            if (user.gender == (genderLayout.getChildAt(i) as RadioButton).text.toString()) {
                genderLayout.check(genderLayout.getChildAt(i).id)
                break
            }
        }
        for (i in 0 until activityFactorLayout.childCount) {
            if (user.activityFactor == (activityFactorLayout.getChildAt(i) as RadioButton).text.toString().toDouble()) {
                activityFactorLayout.check(activityFactorLayout.getChildAt(i).id)
                break
            }
        }
    }
}