package com.meiri.caloriecalculator

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class SearchFoodDialog(c: Activity, themeResId: Int) : Dialog(c, themeResId), View.OnClickListener{
    private lateinit var searchFoodButton: Button
    private lateinit var searchFoodEditText: EditText
    private lateinit var foodItemsListView: ListView
    private var foodList: ArrayList<Food> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.search_food_dialog)

        searchFoodButton = findViewById(R.id.search_food_button)
        searchFoodEditText = findViewById(R.id.search_food_edit_text)
        foodItemsListView = findViewById(R.id.food_items_list_view)

        searchFoodButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            searchFoodButton -> searchFood()
        }
    }

    private fun searchFood() {
        val ingredient = searchFoodEditText.text.toString()
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.edamam.com/api/food-database/parser?app_id=${APP_ID}&app_key=${APP_KEY}&category=generic-foods&ingr=$ingredient"

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> tmpFunc(response) },
            { error -> Log.d(TAG, "got error: ${error.message}") })
        queue.add(stringRequest)
    }

    private fun tmpFunc(response: String) {
        Log.d(TAG, "tmpFunc: $response")
        foodList = Food.getRecipesFromRequest(response)
        val adapter = FoodAdapter(context, foodList)
        foodItemsListView.adapter = adapter
    }

    companion object {
        private const val TAG = "SearchFoodDialog"
        private const val APP_KEY = "865f808ac524e6095b7bcd64bf434bd4"
        private const val APP_ID = "e6379b64"
    }
}