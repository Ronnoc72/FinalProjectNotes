package com.example.finalprojectnotes

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateNoteActivity : AppCompatActivity() {

    var title: EditText? = null
    var note: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        title = findViewById(R.id.titleEditText)
        note = findViewById(R.id.noteEditText)
    }

    fun createNote(view: View) {
        var titleText = title?.text.toString()
        var noteText = note?.text.toString()
        val noteMap: Map<String, String> = mapOf("title" to titleText,
            "note" to noteText)

        val currentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseDatabase.getInstance().reference.child("users")
            .child(currentUser).child("notes").push().setValue(noteMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, NotesActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Note Create Failed, Try Again", Toast.LENGTH_SHORT).show()
                }
            }
    }
}