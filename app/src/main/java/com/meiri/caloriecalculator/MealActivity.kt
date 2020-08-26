package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_meal.*

class MealActivity : AppCompatActivity(), View.OnClickListener {
    private val foodList = ArrayList<Food>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        Log.d(TAG, "[onCreate] initialize activity")

        searchFoodButton.setOnClickListener(this)
        addMealButton.setOnClickListener(this)
    }

    private fun searchFood() {
        val ingredient = searchFoodEditText.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.edamam.com/api/food-database/parser?app_id=$APP_ID&app_key=$APP_KEY&category=generic-foods&ingr=$ingredient"

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
//            composeMealButton -> tmpFunc()
        }
    }

    private fun addMeallToDB() {
        TODO("Not yet implemented")
    }

    private fun tmpFunc(response: String) {
        Log.d(TAG, "got error: $response")
        val foodList = Food.getRecipesFromRequest(response, this)
        val adapter = FoodAdapter(this, foodList)
        foodItemsListView.adapter = adapter
    }

    companion object {
        private const val TAG = "MealActivity"
        private const val APP_KEY = "865f808ac524e6095b7bcd64bf434bd4"
        private const val APP_ID = "e6379b64"
    }
}
