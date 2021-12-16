package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DailyLogFragment : Fragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private var userLog: DataSnapshot? = null
    private lateinit var cal: Calendar
    private val mealsRef = Firebase.database.getReference("meal_diary")
    private lateinit var dateRangeTextView: TextView
    private lateinit var dayOfWeekPicker: RadioGroup
    private lateinit var prevWeekImageView: ImageView
    private lateinit var nextWeekImageView: ImageView
    private lateinit var dailyConsumptionListView: ListView
    private lateinit var waterProgressBar: WaterProgressBar
    private lateinit var calorieConsumptionTextView: TextView
    private lateinit var minusTextView: TextView
    private lateinit var plusTextView: TextView
    private var dailyDrink = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.fragment_daily_log, container, false)
        Log.d(TAG, "[onCreateView] initialize activity")

        cal = Calendar.getInstance(Locale.US)
        dateRangeTextView = inputFragmentView.findViewById(R.id.date_range_text_view)
        dayOfWeekPicker = inputFragmentView.findViewById(R.id.day_of_week_picker)
        prevWeekImageView = inputFragmentView.findViewById(R.id.prev_week_button)
        nextWeekImageView = inputFragmentView.findViewById(R.id.next_week_button)
        dailyConsumptionListView = inputFragmentView.findViewById(R.id.daily_consumption_list_view)
        waterProgressBar = inputFragmentView.findViewById(R.id.water_progress_bar)
        calorieConsumptionTextView = inputFragmentView.findViewById(R.id.calorie_consumption_text_view)
        minusTextView = inputFragmentView.findViewById(R.id.minus_text_view)
        plusTextView = inputFragmentView.findViewById(R.id.plus_text_view)

        prevWeekImageView.setOnClickListener(this)
        nextWeekImageView.setOnClickListener(this)
        dayOfWeekPicker.setOnCheckedChangeListener(this)
        minusTextView.setOnClickListener(this)
        plusTextView.setOnClickListener(this)

        setDateView()
        getUserLog()

        return inputFragmentView
    }

    private fun setDateView() {
        dateRangeTextView.text = getDateRange()
        when(cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> dayOfWeekPicker.check((R.id.sunday_button))
            Calendar.MONDAY -> dayOfWeekPicker.check((R.id.monday_button))
            Calendar.TUESDAY -> dayOfWeekPicker.check((R.id.tuesday_button))
            Calendar.WEDNESDAY -> dayOfWeekPicker.check((R.id.wednesday_button))
            Calendar.THURSDAY -> dayOfWeekPicker.check((R.id.thursday_button))
            Calendar.FRIDAY -> dayOfWeekPicker.check((R.id.friday_button))
            Calendar.SATURDAY -> dayOfWeekPicker.check((R.id.saturday_button))
        }
    }

    private fun getDateRange() : String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal2 = Calendar.getInstance()
        cal2.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        cal2.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR))

        cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val firstDay = cal2.time

        cal2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        val lastDay = cal2.time

        return "${sdf.format(firstDay)} - ${sdf.format(lastDay)}"
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.prev_week_button -> {
                cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) - 1)
                clearDailyLog()
                setDateView()
            }
            R.id.next_week_button -> {
                cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 1)
                clearDailyLog()
                setDateView()
            }
            R.id.plus_text_view -> addCups(1)
            R.id.minus_text_view -> addCups(-1)
        }
    }

    private fun addCups(cups: Int) {
        val uid = MainActivity.getUser().userId
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.format(cal.time)
        dailyDrink += cups
        if (dailyDrink < 0)
            dailyDrink = 0
        if (dailyDrink > 8)
            dailyDrink = 8
        mealsRef.child(uid).child(date).child("drink").setValue(dailyDrink)
        showDailyLog()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == -1) return
        if (group == dayOfWeekPicker) {
            val checkedRadioButton = group.findViewById(checkedId) as RadioButton
            if (!checkedRadioButton.isChecked) return
            when (checkedId) {
                R.id.sunday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                R.id.monday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                R.id.tuesday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                R.id.wednesday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                R.id.thursday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                R.id.friday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                R.id.saturday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
            showDailyLog()
        }
    }

    private fun clearDailyLog() {
        dayOfWeekPicker.clearCheck()
    }

    private fun getUserLog() {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uid = MainActivity.getUser().userId
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(cal.time)
                userLog = dataSnapshot.child(uid)
                if (!userLog!!.child(date).hasChild("drink")) {
                    dailyDrink = 0
                    mealsRef.child(uid).child(date).child("drink").setValue(dailyDrink)
                    mealsRef.push()
                } else {
                    val ti2 = object : GenericTypeIndicator<Int>() {}
                    dailyDrink = userLog?.child(date)?.child("drink")?.getValue(ti2)!!
                }
                showDailyLog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error connecting to database: ${databaseError.message}")
            }
        }
        mealsRef.addListenerForSingleValueEvent(listener)
    }

    private fun showDailyLog() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.format(cal.time)
        try {
            val ti1 = object : GenericTypeIndicator<HashMap<String, ArrayList<Food>>>() {}

            var dailyMeal = userLog?.child(date)?.child("food")?.getValue(ti1)!!
            dailyMeal = dailyMeal.toSortedMap().toMap() as HashMap<String, ArrayList<Food>>
            val dailyCalorieConsumption =
                (dailyMeal.values.map { (it.map { f -> f.calories }).sum() }).sum()
            val adapter = DailyLogAdapter(requireContext(), dailyMeal)
            dailyConsumptionListView.adapter = adapter
            calorieConsumptionTextView.text =
                "Total calorie consumption: ${dailyCalorieConsumption}/${MainActivity.getUser().calories}"
            waterProgressBar.counter = dailyDrink
        } catch (e: Exception) {
            dailyConsumptionListView.adapter = DailyLogAdapter(requireContext(), HashMap())
            calorieConsumptionTextView.text = "Total calorie consumption: 0/0"
            waterProgressBar.counter = 0
        }
    }

    companion object {
        const val TAG = "DailyLogFragment"
    }
}