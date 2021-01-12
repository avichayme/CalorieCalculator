package com.meiri.caloriecalculator

import android.text.Editable
import android.text.TextWatcher
import java.util.*

class DateTextWatcher : TextWatcher {
    private var updatedText: String? = null
    private var editing: Boolean = false

    private fun validateDay(day: String): String {
        if (day.length == 1 && day.toInt() > 3) return "0$day"
        if (day.length == 2 && day.toInt() > 31) return "31"
        return day
    }

    private fun validateDayAndMonth(day: String, month: String): String {
        if (month.length == 1 && month.toInt() > 1) return validateDayAndMonth(day, "0$month")
        if (month.length == 2) {
            if (month.toInt() > 12) return validateDayAndMonth(day, "12")
            return "$day$month"
        }
        return "$day$month"
    }

    private fun validateMonth(date: String): String {
        val day = date.substring(0, 2)
        val month = date.substring(2)

        return validateDayAndMonth(day, month)
    }

    private fun validateDate(day: String, month: String, year: String): String {
        if (year.length == 4) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year.toInt())
            cal.set(Calendar.MONTH, month.toInt())
            if (day.toInt() > cal.getActualMaximum(Calendar.DATE))
                return "${cal.getActualMaximum(Calendar.DATE)}$month$year"
            return "$day$month$year"
        }
        return "$day$month$year"
    }

    private fun validateYear(date: String): String {
        val day = date.substring(0, 2)
        val month = date.substring(2, 4)
        val year = date.substring(4)

        return validateDate(day, month, year)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        if (text.toString() == updatedText || editing) return

        var digits = text.toString().replace("\\D".toRegex(), "") // remove non-digit characters

        digits = when (digits.length) {
            0 -> ""
            in 1..2 -> validateDay(digits)
            in 3..4 -> validateMonth(digits)
            in 5..8 -> validateYear(digits)
            else -> validateYear(digits.substring(0, 8))
        }

        updatedText = when (digits.length) {
            in 0..2 -> digits
            in 3..4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
            else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
        }
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editing) return

        editing = true

        editable?.clear()
        editable?.insert(0, updatedText)

        editing = false
    }
}