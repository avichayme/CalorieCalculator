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

class MealFragment : Fragment(), View.OnClickListener {
    private val foodList = ArrayList<Food>()
    private lateinit var mealDateEditText: EditText
    private lateinit var mealTimeEditText: EditText
    private lateinit var addMealButton: Button
    private lateinit var testButton: Button
    private lateinit var selectedFoodItemsListView: ListView
    private val mealDiary = Firebase.database.getReference("meal_diary")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.fragment_meal, container, false)
        Log.d(TAG.TAG, "[onCreate] initialize activity")

        addMealButton = inputFragmentView.findViewById(R.id.add_meal_button)
        testButton = inputFragmentView.findViewById(R.id.test_button)
        selectedFoodItemsListView = inputFragmentView.findViewById(R.id.selected_food_items_list_view)
        mealDateEditText = inputFragmentView.findViewById(R.id.meal_date_edit_text)
        mealTimeEditText = inputFragmentView.findViewById(R.id.meal_time_edit_Text)

        addMealButton.setOnClickListener(this)
        testButton.setOnClickListener(this)
        mealDateEditText.addTextChangedListener(DateTextWatcher())
        return inputFragmentView
    }

    override fun onClick(v: View?) {
        when (v) {
            addMealButton -> addMealToDB()
            testButton -> {
                val cdd = SearchFoodDialog(activity!!, android.R.style.Theme_NoTitleBar_Fullscreen)
                cdd.show()

                val foodItemsListView: ListView = cdd.findViewById(R.id.food_items_list_view)
                foodItemsListView.onItemClickListener =
                    OnItemClickListener { _, _, position, _ ->
                        Toast.makeText(activity!!.applicationContext, "Click ListItem Number $position", Toast.LENGTH_LONG).show()
                        foodItemChangeMode(foodItemsListView.adapter.getItem(position) as Food)
                    }

                cdd.setOnCancelListener {
                    val adapter = FoodAdapter(activity!!.applicationContext, foodList)
                    selectedFoodItemsListView.adapter = adapter
                }
            }
        }
    }

    private fun foodItemChangeMode(food: Food) {
        val f = foodList.stream().filter{ it == food }.findFirst()
        if (f.isPresent) {
            foodList.remove(f.get())
        }
        else {
            foodList.add(food)
        }
    }

    private fun addMealToDB() {
        val userID = MainActivity.getUserID()
        val userMealDiary = mealDiary.child(userID!!).child(getLogTime())
        userMealDiary.setValue(foodList)
        mealDiary.push()
    }

    private fun getLogTime(): String {
        return "${mealDateEditText.editableText}_${mealTimeEditText.editableText}"
    }

    object TAG {
        const val TAG = "MealFragment"
    }
}