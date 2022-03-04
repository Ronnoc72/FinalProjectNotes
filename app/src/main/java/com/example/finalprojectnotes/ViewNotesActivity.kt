package com.example.finalprojectnotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.system.exitProcess

class ViewNotesActivity : AppCompatActivity() {

    var titleText: TextView? = null
    var noteText: TextView? = null
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)

        titleText = findViewById(R.id.viewTitleEditText)
        noteText = findViewById(R.id.viewNoteEditText)

        titleText?.text = intent.getStringExtra("title")
        noteText?.text = intent.getStringExtra("note")
    }

    fun saveNote(view: View) {
        if (noteText?.text.isNullOrEmpty() || titleText?.text.isNullOrEmpty()) {
            Toast.makeText(this, "You must have Content in both Title and Note.", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(it).child("notes").child(intent.getStringExtra("key").toString())
                    .child("note").setValue(noteText?.text.toString())
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(it).child("notes").child(intent.getStringExtra("key").toString())
                    .child("title").setValue(titleText?.text.toString())
            }
            finish()
        }
    }

    fun back(view: View) {
        finish()
    }
}