package com.example.finalprojectnotes

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.os.Build
import android.os.Handler
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class NotesActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var notes: ArrayList<ArrayList<String>> = ArrayList()
    var linearLayout: LinearLayout? = null
    var title: EditText? = null
    var note: EditText? = null
    var createNoteLayout: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        title = findViewById(R.id.titleEditText)
        note = findViewById(R.id.noteEditText)
        createNoteLayout = findViewById(R.id.createNoteLayout)

        linearLayout = findViewById(R.id.linearLayout)

        mAuth.currentUser?.uid?.let { test ->
            FirebaseDatabase.getInstance().reference.child("users")
                .child(test).child("notes").addValueEventListener(object : ValueEventListener {
                    @SuppressLint("ResourceType")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        notes.clear()
                        linearLayout?.removeAllViews()
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
                            linearLayout?.addView(textView)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ObjectAnimatorBinding")
    fun addNote(view: View) {
        ObjectAnimator.ofFloat(this.linearLayout, "translationX", 700f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.createNoteLayout, "translationY", -1000f).apply {
            duration = 500
            startDelay = 500
            start()
        }
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
