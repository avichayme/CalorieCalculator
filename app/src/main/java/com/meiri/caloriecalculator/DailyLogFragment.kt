package com.meiri.caloriecalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*


class DailyLogFragment : Fragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private lateinit var cal: Calendar
    private lateinit var dateRangeTextView: TextView
    private lateinit var dayOfWeekPicker: RadioGroup
    private lateinit var prevWeekImageView: ImageView
    private lateinit var nextWeekImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val inputFragmentView = inflater.inflate(R.layout.fragment_one, container, false)
        Log.d(TAG, "[onCreateView] initialize activity")

        cal = Calendar.getInstance(Locale.US)
        dateRangeTextView = inputFragmentView.findViewById(R.id.date_range_text_view)
        dayOfWeekPicker = inputFragmentView.findViewById(R.id.day_of_week_picker)
        prevWeekImageView = inputFragmentView.findViewById(R.id.prev_week_button)
        nextWeekImageView = inputFragmentView.findViewById(R.id.next_week_button)

        prevWeekImageView.setOnClickListener(this)
        nextWeekImageView.setOnClickListener(this)
        dayOfWeekPicker.setOnCheckedChangeListener(this)

        setDateView()
        getDailyLog()

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
        when(v!!.id) {
            R.id.prev_week_button -> cal.set(Calendar.WEEK_OF_YEAR,
                cal.get(Calendar.WEEK_OF_YEAR) - 1)
            R.id.next_week_button -> cal.set(Calendar.WEEK_OF_YEAR,
                cal.get(Calendar.WEEK_OF_YEAR) + 1)
        }
        setDateView()
        clearDailyLog()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == -1) return
        val checkedRadioButton = group!!.findViewById(checkedId) as RadioButton
        if (!checkedRadioButton.isChecked) return
        if (group == dayOfWeekPicker) {
            when (checkedId) {
                R.id.sunday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                R.id.monday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                R.id.tuesday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                R.id.wednesday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                R.id.thursday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                R.id.friday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                R.id.saturday_button -> cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
            getDailyLog()
        }
    }

    private fun clearDailyLog() {
        dayOfWeekPicker.clearCheck()
    }

    private fun getDailyLog() {

    }

    companion object {
        const val TAG = "DailyLogFragment"
    }
}