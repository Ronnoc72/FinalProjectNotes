package com.example.finalprojectnotes

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var signInBtn: Button? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInBtn = findViewById(R.id.goButton)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }

    fun goClick(view: View) {
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { test ->
                            if (test.isSuccessful) {
                                FirebaseDatabase.getInstance().reference.child("users").child(test.result?.user!!.uid)
                                    .child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            } else {
                                Toast.makeText(this, "Login Failed Try Again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    fun closeKeyboard() {
        var view = this@MainActivity.currentFocus
        if (view != null) {
            var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun logIn() {
        closeKeyboard()
        val intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }
}