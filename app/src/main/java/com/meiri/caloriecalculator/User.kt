package com.meiri.caloriecalculator

import android.content.res.Resources
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class User(val userId: String) {
    var birthDate: LocalDate = INIT_BIRTH_DATE // figure out a way to save as Date
    var height: Long = INIT_HEIGHT
    var weight: Long = INIT_WEIGHT
    var gender: String = INIT_GENDER
    var activityFactor: Double = INIT_ACTIVITY_FACTOR
    var registered: Boolean = false

    private val age: Int
        get() {
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob.set(birthDate.year, birthDate.monthValue, birthDate.dayOfMonth)

            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR])
                age--
            return age
        }
    private val bmr: Double
        get() = if (gender == Resources.getSystem()
                .getString(R.string.male)
        ) 13.397 * weight + 4.799 * height - 5.677 * age + 88.362 else 9.247 * weight + 3.098 * height - 4.330 * age + 447.593
    val calories: Double
        get() = (bmr * activityFactor).roundToInt() / 100.0

    private val usersRef = Firebase.database.getReference("users")
    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")

    fun getBirthDateFormatted() : String {
        return birthDate.format(formatter).replace("-", "/")
    }

    private fun updateUserFromDatabase() {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ti = object : GenericTypeIndicator<HashMap<String, Any>?>() {}
                val userData: HashMap<String, Any>? = dataSnapshot.child(userId).getValue(ti)

                if (userData != null) {
                    birthDate = LocalDate.parse(userData["birth_date"].toString(), formatter)
                    height = userData["height"] as Long
                    weight = userData["weight"] as Long
                    gender = userData["gender"].toString()
                    activityFactor = userData["activity_factor"] as Double
                    registered = true
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error connecting to database: ${databaseError.message}")
            }
        }
        usersRef.addListenerForSingleValueEvent(listener)
    }

    fun saveToDatabase() {
        usersRef.child(userId).setValue(toDatabaseRecord())
        registered = true
    }

    private fun toDatabaseRecord(): MutableMap<String, Any> {
        val userData: MutableMap<String, Any> = HashMap()
        userData["birth_date"] = birthDate.toString()
        userData["height"] = height
        userData["weight"] = weight
        userData["gender"] = gender
        userData["activity_factor"] = activityFactor
        return userData
    }

    fun fromDatabaseRecord(userData: MutableMap<String, Any>) {
        birthDate = LocalDate.parse(userData["birth_date"].toString().replace("/", "-"), formatter)
        height = userData["height"] as Long
        weight = userData["weight"] as Long
        gender = userData["gender"] as String
        activityFactor = userData["activity_factor"] as Double

        saveToDatabase()
    }

    companion object {
        private const val TAG = "User"
        private val INIT_BIRTH_DATE = LocalDate.now()
        private const val INIT_HEIGHT = 170L
        private const val INIT_WEIGHT = 80L
        private const val INIT_GENDER = "Female" // Resources.getSystem().getString(R.string.female)
        private const val INIT_ACTIVITY_FACTOR =
            1.2 // Resources.getSystem().getString(R.string.activity_factor_1).toDouble()
    }

    init {
        updateUserFromDatabase()
    }
}