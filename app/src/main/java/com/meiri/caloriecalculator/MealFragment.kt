package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MealFragment : Fragment(), View.OnClickListener {
    private val foodList = ArrayList<Food>()
    private lateinit var searchFoodButton: Button
    private lateinit var addMealButton: Button
    private lateinit var searchFoodEditText: EditText
    private lateinit var foodItemsListView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.fragment_meal, container, false)
        Log.d(TAG, "[onCreate] initialize activity")

        searchFoodButton = inputFragmentView.findViewById(R.id.search_food_button)
        addMealButton = inputFragmentView.findViewById(R.id.add_meal_button)
        searchFoodEditText = inputFragmentView.findViewById(R.id.search_food_edit_text)
        foodItemsListView = inputFragmentView.findViewById(R.id.food_items_list_view)

        searchFoodButton.setOnClickListener(this)
        addMealButton.setOnClickListener(this)
        return inputFragmentView
    }

    private fun searchFood() {
        val ingredient = searchFoodEditText.text.toString()
        val queue = Volley.newRequestQueue(activity)
        val url = "https://api.edamam.com/api/food-database/parser?app_id=${APP_ID}&app_key=${APP_KEY}&category=generic-foods&ingr=$ingredient"

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> tmpFunc(response) },
            { error -> Log.d(TAG, "got error: ${error.message}") })
        queue.add(stringRequest)
    }

    override fun onClick(v: View?) {
        when (v) {
            searchFoodButton -> searchFood()
            addMealButton -> addMeallToDB()
        }
    }

    private fun addMeallToDB() {
        TODO("Not yet implemented")
    }

    private fun tmpFunc(response: String) {
        Log.d(TAG, "got error: $response")
        val foodList = Food.getRecipesFromRequest(response)
        val adapter = activity?.applicationContext?.let { FoodAdapter(it, foodList) }
        foodItemsListView.adapter = adapter
    }

    companion object {
        private const val TAG = "MealActivity"
        private const val APP_KEY = "865f808ac524e6095b7bcd64bf434bd4"
        private const val APP_ID = "e6379b64"
    }
}