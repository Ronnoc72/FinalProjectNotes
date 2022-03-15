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
import android.app.AlertDialog
import android.content.Context
import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
    var signOutButton: Button? = null
    var addNoteButton: Button? = null
    var titleText: EditText? = null
    var noteText: EditText? = null
    var saveNoteLayout: ConstraintLayout? = null
    var currentNoteKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        title = findViewById(R.id.titleEditText)
        note = findViewById(R.id.noteEditText)
        createNoteLayout = findViewById(R.id.createNoteLayout)
        saveNoteLayout = findViewById(R.id.saveNoteLayout)

        linearLayout = findViewById(R.id.linearLayout)
        signOutButton = findViewById(R.id.signOutBtn)
        addNoteButton = findViewById(R.id.addNoteBtn)
        titleText = findViewById(R.id.viewTitleEditText)
        noteText = findViewById(R.id.viewNoteEditText)

        closeKeyboard()

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
                                this@NotesActivity.saveNoteLayout?.let { moveMainActivity(it) }
                                titleText?.isEnabled = true
                                titleText?.setText(note[1])
                                noteText?.isEnabled = true
                                noteText?.setText(note[0])
                                currentNoteKey = note[2]
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
        title?.isEnabled = true
        note?.isEnabled = true
        this.createNoteLayout?.let { moveMainActivity(it) }
    }

    fun moveMainActivity(view: ConstraintLayout) {
        ObjectAnimator.ofFloat(this.linearLayout, "translationX", 700f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.signOutButton, "translationX", -400f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.addNoteButton, "translationX", 400f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(view, "translationY", 0f).apply {
            duration = 500
            startDelay = 500
            start()
        }
        closeKeyboard()
    }

    fun closeLayout(view: ConstraintLayout, amount: Float) {
        ObjectAnimator.ofFloat(view, "translationY", amount).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.linearLayout, "translationX", 0f).apply {
            startDelay = 500
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.signOutButton, "translationX", 0f).apply {
            startDelay = 500
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(this.addNoteButton, "translationX", 0f).apply {
            startDelay = 500
            duration = 500
            start()
        }
    }

    fun closeWindow(view: View) {
        this.createNoteLayout?.let { closeLayout(it, 2000f) }
        title?.isEnabled = false
        note?.isEnabled = false
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
        this.createNoteLayout?.let { closeWindow(it) }
    }

    fun saveNote(view: View) {
        if (noteText?.text.isNullOrEmpty() || titleText?.text.isNullOrEmpty()) {
            Toast.makeText(this, "You must have Content in both Title and Note.", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.currentUser?.uid?.let {
                currentNoteKey?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(it).child("notes").child(it1)
                        .child("note").setValue(noteText?.text.toString())
                }
                currentNoteKey?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(it).child("notes").child(it1)
                        .child("title").setValue(titleText?.text.toString())
                }
            }
            this.saveNoteLayout?.let { closeLayout(it, -2000f) }
            titleText?.isEnabled = false
            noteText?.isEnabled = false
        }

    }

    fun closeKeyboard() {
        var view = this@NotesActivity.currentFocus
        if (view != null) {
            var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun back(view: View) {
        this.saveNoteLayout?.let { closeLayout(it, -2000f) }
        titleText?.isEnabled = false
        noteText?.isEnabled = false
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
