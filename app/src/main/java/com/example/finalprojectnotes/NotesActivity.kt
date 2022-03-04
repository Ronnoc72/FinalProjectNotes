package com.example.finalprojectnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.setPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.finalprojectnotes.NotesActivity as ComExampleFinalprojectnotesNotesActivity

class NotesActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var notes: ArrayList<ArrayList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        var gridLayout: androidx.gridlayout.widget.GridLayout = findViewById(R.id.gridLayout)
        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(it).child("notes").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in gridLayout.children) {
                            gridLayout.removeView(child)
                        }
                        if (snapshot.exists()) {
                            for (s in snapshot.children) {
                                var noteList: ArrayList<String> = ArrayList()
                                for (str in s.children) {
                                    noteList.add(str.value.toString())
                                }
                                noteList.add(s.key.toString())
                                notes.add(noteList)
                            }
                        }
                        for (note in notes) {
                            var textView = TextView(this@NotesActivity)
                            textView.text = note[1]
                            textView.setOnClickListener { task ->
                                var intent = Intent(this@NotesActivity, ViewNotesActivity::class.java)
                                intent.putExtra("title", note[1])
                                intent.putExtra("note", note[0])
                                intent.putExtra("key", note[2])
                                startActivity(intent)
                            }
                            textView.height = 360
                            textView.width = 360
                            gridLayout.addView(textView)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
    }

    fun noteMenu(view: View) {

    }

    fun addNote(view: View) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        startActivity(intent)
    }
}
