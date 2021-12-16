package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MealFragment : Fragment(), View.OnClickListener {
    private val foodList = ArrayList<Food>()
    private lateinit var mealDateEditText: EditText
    private lateinit var mealTimeEditText: EditText
    private lateinit var addMealButton: Button
    private lateinit var composeMealButton: Button
    private lateinit var selectedFoodItemsListView: ListView
    private val mealDiary = Firebase.database.getReference("meal_diary")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.fragment_meal, container, false)
        Log.d(TAG, "[onCreateView] initialize activity")

        addMealButton = inputFragmentView.findViewById(R.id.add_meal_button)
        composeMealButton = inputFragmentView.findViewById(R.id.compose_meal_button)
        selectedFoodItemsListView = inputFragmentView.findViewById(R.id.selected_food_items_list_view)
        mealDateEditText = inputFragmentView.findViewById(R.id.meal_date_edit_text)
        mealTimeEditText = inputFragmentView.findViewById(R.id.meal_time_edit_Text)

        addMealButton.setOnClickListener(this)
        composeMealButton.setOnClickListener(this)
        mealDateEditText.addTextChangedListener(DateTextWatcher())
        setCurrentDateTime()
        return inputFragmentView
    }

    private fun setCurrentDateTime() {
        mealDateEditText.setText(LocalDate.now().format(MainActivity.getUser().formatter).replace("-", "/"))
        mealTimeEditText.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("H:m")))
    }

    override fun onClick(v: View?) {
        when (v) {
            addMealButton -> addMealToDB()
            composeMealButton -> {
                val cdd = SearchFoodDialog(requireActivity(), android.R.style.Theme_NoTitleBar_Fullscreen)
                cdd.show()

                val foodItemsListView: ListView = cdd.findViewById(R.id.food_items_list_view)
                foodItemsListView.onItemClickListener =
                    OnItemClickListener { _, _, position, _ ->
                        foodItemChangeMode(foodItemsListView.adapter.getItem(position) as Food)
                    }

                cdd.setOnCancelListener {
                    val adapter = FoodAdapter(requireActivity().applicationContext, foodList)
                    selectedFoodItemsListView.adapter = adapter
                }
            }
        }
    }

    private fun foodItemChangeMode(food: Food) {
        val f = foodList.stream().filter{ it == food }.findFirst()
        if (!f.isPresent) {
            foodList.add(food)
        }
    }

    private fun addMealToDB() {
        val userID = MainActivity.getUser().userId
        val logTime = getLogTime()
        if (logTime != null) {
            val userMealDiary = mealDiary.child(userID).child(logTime)
            userMealDiary.setValue(foodList)
            mealDiary.push()
            foodList.clear()
            val adapter = FoodAdapter(requireActivity().applicationContext, foodList)
            selectedFoodItemsListView.adapter = adapter
        }
    }

    private fun getLogTime(): String? {
        if (!validDateAndTime()) {
            Toast.makeText(context, "Date and Time are not valid", Toast.LENGTH_SHORT).show()
            return null
        }
        val date = LocalDate.parse(mealDateEditText.editableText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        return "${date}/food/${mealTimeEditText.editableText}"
    }

    private fun validDateAndTime(): Boolean {
        if (mealDateEditText.editableText.isBlank() || mealTimeEditText.editableText.isBlank())
            return false
        return true
    }

    companion object {
        const val TAG = "MealFragment"
    }
}