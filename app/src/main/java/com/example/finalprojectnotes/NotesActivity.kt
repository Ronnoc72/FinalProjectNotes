package com.example.finalprojectnotes

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class NotesActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var notes: ArrayList<ArrayList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        var linearLayout: LinearLayout = findViewById(R.id.linearLayout)

        mAuth.currentUser?.uid?.let { test ->
            FirebaseDatabase.getInstance().reference.child("users")
                .child(test).child("notes").addValueEventListener(object : ValueEventListener {
                    @SuppressLint("ResourceType")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        notes.clear()
                        linearLayout.removeAllViews()
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
                            textView.setOnLongClickListener { task ->
                                val builder = AlertDialog.Builder(this@NotesActivity)
                                builder.setMessage(R.string.dialog_message)
                                builder.setTitle("Delete Note?")
                                builder.setCancelable(false)
                                builder.setPositiveButton(R.string.okay, DialogInterface.OnClickListener { dialogInterface, i ->
                                    mAuth.currentUser?.uid?.let {
                                        FirebaseDatabase.getInstance().reference
                                            .child("users")
                                            .child(it).child("notes")
                                            .child(note[2]).removeValue()
                                    }
                                })
                                builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->
                                    return@OnClickListener
                                })
                                val alertDialog = builder.create()
                                alertDialog.show()
                                true
                            }
                            textView.text = formatText(note[1].replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            })
                            textView.textSize = 24F
                            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                            textView.setOnClickListener { task ->
                                var intent = Intent(this@NotesActivity, ViewNotesActivity::class.java)
                                intent.putExtra("title", note[1])
                                intent.putExtra("note", note[0])
                                intent.putExtra("key", note[2])
                                startActivity(intent)
                            }
                            textView.height = 360
                            textView.width = 360
                            linearLayout.addView(textView)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
    }

    fun addNote(view: View) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        startActivity(intent)
    }

    fun signOut(view: View) {
        mAuth.signOut()
        finish()
    }

    fun formatText(text: String): String {
        if (text.length > 15) {
            return text.substring(0, 15) + "..."
        }
        return text
    }
}
