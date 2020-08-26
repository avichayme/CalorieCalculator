package com.meiri.caloriecalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        findViewById<SignInButton>(R.id.signInButton).setOnClickListener(this)
        startActivity(Intent(this, MealActivity::class.java))
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val firebaseUser = mFirebaseAuth.currentUser
        updateUI(firebaseUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.i(TAG, "[updateUI] ${user.toString()}")
        if (user != null) {
            findViewById<TextView>(R.id.statusTextView).text = "Hello ${user.displayName}!"
//            findViewById<SignInButton>(R.id.sign_in_button).isEnabled = false
            findViewById<SignInButton>(R.id.signInButton).visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.statusTextView).text = "Hello World!"
//            findViewById<SignInButton>(R.id.sign_in_button).isEnabled = true
            findViewById<SignInButton>(R.id.signInButton).visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        Log.i(TAG, "[onClick] ${v.toString()}")
        when (v!!.id) {
            R.id.signInButton -> signIn()
        }
    }

    private fun signIn() {
        Log.i(TAG, "[signIn]")
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.i(TAG, "[handleSignInResult] $completedTask")
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "[handleSignInResult] signInResult:failed code=" + e.statusCode)
            updateUI(null)
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
                    updateUI(firebaseUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        TAG,
                        "[firebaseAuthWithGoogle] signInWithCredential:failure",
                        task.exception
                    )
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun createNewUser(uid: String?) {
        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra("user_id", uid)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }
}
