package com.meiri.caloriecalculator

import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.math.roundToInt

data class Food(
    var foodId: String = "",
    var foodName: String = "",
    var calories: Double = 0.0,
    var imgURL: String = ""
) {

    init {
        calories = (calories * 100).roundToInt() / 100.0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Food)
            return false
        return other.foodId == foodId
    }

    override fun hashCode(): Int {
        return foodId.hashCode()
    }

    companion object {
        fun getRecipesFromRequest(jsonString: String): ArrayList<Food> {
            val foodList = ArrayList<Food>()

            try {
                // Load data
                val json = JSONObject(jsonString)
                val hints = json.getJSONArray("hints")

                // Get Food objects from data
                (0 until hints.length()).mapTo(foodList) {
                    val foodItem = hints.getJSONObject(it).getJSONObject("food")
                    val foodId = foodItem.getString("foodId")
                    val name = foodItem.getString("label")
                    val calories = foodItem.getJSONObject("nutrients").getDouble("ENERC_KCAL")
                    val image = foodItem.optString("image", "drawable://" + R.drawable.app_icon_bw)
                    Food(foodId, name, calories, image)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return foodList
        }
    }
}