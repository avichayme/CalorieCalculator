package com.meiri.caloriecalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView


class DailyLogAdapter(private val context: Context, private val dataSource: HashMap<String, ArrayList<Food>>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val keys = dataSource.keys.toList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var rowView = convertView
        if (rowView == null) rowView = inflater.inflate(R.layout.daily_log_item, parent, false)

        // Get food item elements
        val dailyLogHourTextView = rowView?.findViewById(R.id.daily_log_hour_text_view) as TextView
        val dailyLogLayout = rowView.findViewById(R.id.daily_meal_log) as ListView

        // Setting item in list
        val foodListItem = getItem(position) as ArrayList<Food>

        dailyLogHourTextView.text = keys[position]
        val adapter = FoodAdapter(context, foodListItem)
        dailyLogLayout.adapter = adapter

        return rowView
    }

    override fun getItem(position: Int): Any { return dataSource[keys[position]]!! }

    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getCount(): Int { return dataSource.size }
}
