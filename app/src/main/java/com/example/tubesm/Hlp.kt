package com.example.tubesm

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Hlp : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("User")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hlp)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val email : EditText = findViewById(R.id.email)
        val find : ImageView = findViewById(R.id.find)
        mAuth = FirebaseAuth.getInstance()
        find.setOnClickListener{
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cekusr: String? = dataSnapshot.child(email.text.toString().replace(".", "_")).child("Via").getValue(String::class.java)
                    if (cekusr != null) {
                        if (cekusr.equals("Google")) {
                            Toast.makeText(this@Hlp, "Email tidak memerlukan pemulihan sandi", Toast.LENGTH_SHORT).show()
                        } else {
                            mAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@Hlp, "Email pemulihan dikirim", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@Hlp, "Coba beberapa saat lagi", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }

                    } else {
                        Toast.makeText(this@Hlp, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event)
            val intent = Intent(this@Hlp, Main::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
            return true
        }
        return false
    }
}

