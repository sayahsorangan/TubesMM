package com.example.tubesm

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tubesm.Utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profil : AppCompatActivity() {
    private lateinit var logout: TextView
    private lateinit var nama: TextView
    private lateinit var email: TextView
    private lateinit var reset: TextView
    private lateinit var y: Button
    private lateinit var n: Button
    private lateinit var mAuth: FirebaseAuth
    var auth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    var myRef1 = database.getReference("User")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        logout = findViewById(R.id.logut)
        nama = findViewById(R.id.nama)
        email = findViewById(R.id.email)
        reset = findViewById(R.id.reset)
        y = findViewById(R.id.ya)
        n = findViewById(R.id.tida)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val mail = user!!.email
        myRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usrname = snapshot.child(mail!!.replace(".", "_")).child("Nama").getValue(String::class.java)
                nama.setText(usrname)
                email.setText(mail)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        y.setOnClickListener{
            myRef1.child(mail!!.replace(".", "_")).child("Ket").setValue("Ada")
        }
        n.setOnClickListener{
            myRef1.child(mail!!.replace(".", "_")).child("Ket").setValue("Tidak")
        }
        logout.setOnClickListener {
            val intent1 = Intent(this, GetLoc::class.java)
            stopService(intent1)
            FirebaseAuth.getInstance().signOut()
            User.userLogOut(applicationContext)
            val intent = Intent(this@Profil, Main::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
        }
        reset.setOnClickListener {
            auth.sendPasswordResetEmail(mail!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Profil, "Email pemulihan telah dikirim", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@Profil, "Mohon coba lagi", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onKeyDown(key_code: Int, key_event: KeyEvent): Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event)
            val intent = Intent(this@Profil, MapsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
            return true
        }
        return false
    }
}