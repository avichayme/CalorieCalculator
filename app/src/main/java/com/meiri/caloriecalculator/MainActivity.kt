package com.meiri.caloriecalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

enum class State {LOGGED_OFF, LOGGED_IN, SIGN_UP, FULL_REGISTERED}

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var signInButton: SignInButton
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var adapter: TabAdapter
    private lateinit var welcomeLayout: View
    private var state: State = State.LOGGED_OFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "[onCreate]")

        welcomeLayout = findViewById(R.id.welcome_layout)
        signInButton = findViewById(R.id.sign_in_button)
        viewPager = findViewById(R.id.main_view_pager)
        tabLayout = findViewById(R.id.main_tab_layout)

        Log.d(TAG, "[onCreate] Starting Activity. State is ${state.name}")
        updateState()
    }

    override fun onClick(v: View?) {
        Log.i(TAG, "[onClick] ${v.toString()}")
        when (v!!.id) {
            R.id.sign_in_button -> {
                state = State.SIGN_UP
                Log.d(TAG, "[onClick] Changing state to ${state.name}")
            }
        }
        updateState()
    }

    private fun updateState() {
        Log.i(TAG, "[updateState]")
        when (state) {
            State.LOGGED_OFF -> initializeActivity()
            State.LOGGED_IN -> checkUserRegistration()
            State.SIGN_UP -> signIn()
            State.FULL_REGISTERED -> startMainActivity()
        }
    }

    private fun initializeActivity() {
        Log.d(TAG, "[initializeActivity] starting")
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()

        if (mFirebaseAuth.currentUser != null) {
            user = User(mFirebaseAuth.currentUser?.uid!!)
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setOnClickListener(this)

        if (mFirebaseAuth.currentUser != null) {
            state = State.LOGGED_IN
            updateState()
        }
        Log.d(TAG, "[initializeActivity] finish")
    }

    private fun checkUserRegistration() {
        state = if (user.registered) {
            State.FULL_REGISTERED
        } else {
            State.SIGN_UP
        }
        updateState()
    }

    private fun signIn() {
        Log.i(TAG, "[signIn]")
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "[onActivityResult]")
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        // Result returned from launching the Intent SignInActivity;
        if (requestCode == APP_SIGN_UP) {
            @Suppress("UNCHECKED_CAST")
            val userData = data?.getSerializableExtra("user_data") as HashMap<String, Any>
            signUserToDB(userData)
        }
    }

    private fun signUserToDB(userData: HashMap<String, Any>) {
        user.fromDatabaseRecord(userData)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.i(TAG, "[handleSignInResult] $completedTask")
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account?.idToken!!)
            state = State.FULL_REGISTERED
            Log.d(TAG, "[handleSignInResult] Changing state to ${state.name}")
            updateState()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "[handleSignInResult] signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.i(TAG, "[firebaseAuthWithGoogle] $idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "[firebaseAuthWithGoogle] signInWithCredential:success")
                    val firebaseUser = mFirebaseAuth.currentUser
                    if (task.result?.additionalUserInfo?.isNewUser!!)
                        createNewUser(firebaseUser?.uid)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        TAG,
                        "[firebaseAuthWithGoogle] signInWithCredential:failure",
                        task.exception
                    )
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createNewUser(uid: String?) {
        Log.i(TAG, "[createNewUser] creating new user with id $uid")
        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra("user_id", uid)
        startActivityForResult(intent, APP_SIGN_UP)
    }

    private fun startMainActivity() {
        Log.i(TAG, "[startMainActivity]")
        welcomeLayout.visibility = View.GONE
        adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(DailyLogFragment(), "Tab 1")
        adapter.addFragment(MealFragment(), "Tab 2")
        adapter.addFragment(Tab3Fragment(), "Tab 3")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        private lateinit var mFirebaseAuth: FirebaseAuth
        private lateinit var user: User
        private const val TAG = "MainActivity"
        private const val APP_SIGN_UP = 1
        private const val RC_SIGN_IN = 9001

        fun getUserID(): String? {
            return mFirebaseAuth.currentUser?.uid
        }
    }
}
