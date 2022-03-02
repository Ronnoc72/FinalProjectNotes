package com.example.finalprojectnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotesActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var notes: ArrayList<ArrayList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        var gridLayout: androidx.gridlayout.widget.GridLayout = findViewById(R.id.gridLayout)
        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(it).child("notes").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (s in snapshot.children) {
                                return
                            }
                        }
                        Log.i("NOTES INFO: ", notes.toString())
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
