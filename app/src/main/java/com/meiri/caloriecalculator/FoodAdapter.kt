package com.meiri.caloriecalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class FoodAdapter(context: Context, private val dataSource: ArrayList<Food>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var rowView = convertView
        if (rowView == null) rowView = inflater.inflate(R.layout.list_item_food, parent, false)

        // Get food item elements
        val nameTextView = rowView?.findViewById(R.id.food_item_name) as TextView
        val calorieTextView = rowView.findViewById(R.id.food_item_calorie) as TextView
        val thumbnailImageView = rowView.findViewById(R.id.food_item_thumbnail) as ImageView

        // Setting item in list
        val foodItem = getItem(position) as Food
        nameTextView.text = foodItem.foodName
        calorieTextView.text = foodItem.calories.toString()
        Picasso.get().load(foodItem.imgURL).placeholder(R.drawable.app_icon_bw).into(thumbnailImageView)

        return rowView
    }

    override fun getItem(position: Int): Any { return dataSource[position] }

    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getCount(): Int { return dataSource.size }
}
