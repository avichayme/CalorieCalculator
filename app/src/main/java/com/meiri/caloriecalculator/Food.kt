package com.meiri.caloriecalculator

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

class Food(var foodName: String, var calories: Double, var imgURL: String) {
    init {
        calories = (calories * 100).roundToInt() / 100.0
    }

    companion object {
        fun getRecipesFromRequest(jsonString: String, context: Context): ArrayList<Food> {
            val foodList = ArrayList<Food>()

            try {
                // Load data
                val json = JSONObject(jsonString)
                val hints = json.getJSONArray("hints")

                // Get Food objects from data
                (0 until hints.length()).mapTo(foodList) {
                    val foodItem = hints.getJSONObject(it).getJSONObject("food")
                    val name = foodItem.getString("label")
                    val calories = foodItem.getJSONObject("nutrients").getDouble("ENERC_KCAL")
                    val image = foodItem.optString("image", "drawable://"+R.drawable.app_icon_bw)
                    Food(name, calories, image)
                }
            } catch (e: JSONException) { e.printStackTrace() }

            return foodList
        }
    }
}