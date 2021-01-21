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


class Regis : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("User")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val id : EditText = findViewById(R.id.id)
        val email : EditText = findViewById(R.id.email)
        val pw : EditText = findViewById(R.id.pass)
        val pwc : EditText = findViewById(R.id.passcek)
        val buat : ImageView = findViewById(R.id.baru)
        mAuth = FirebaseAuth.getInstance()

        buat.setOnClickListener{
            if(id.text.toString().isEmpty()) {
                id.error = "Tolong masukan Nama"
                id.requestFocus()}
            if(email.text.toString().isEmpty()) {
                email.setError("Tolong masukan Email")
                email.requestFocus()}
            if(pw.text.toString().isEmpty()){
                pw.setError("Tolong masukan password")
                pw.requestFocus()}
            if(pwc.text.toString().isEmpty()){
                pwc.setError("Tolong masukan password")
                pwc.requestFocus()}
            if(pw.text.toString().length < 6){
                pw.setError("Password minimal 6 digit")
                pw.requestFocus()
            }else{
                if(!id.text.toString().isEmpty() && !email.text.toString().isEmpty() && !pw.text.toString().isEmpty() && !pwc.text.toString().isEmpty()) {
                    if(pw.text.toString().equals(pwc.text.toString())){
                        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val cekusr: String? = dataSnapshot.child(email.text.toString().replace(".", "_")).child("Nama").getValue(String::class.java)
                                if (cekusr == null) {
                                    mAuth.createUserWithEmailAndPassword(email.text.toString(), pw.text.toString()).addOnCompleteListener(OnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            myRef.child(email.text.toString().replace(".", "_")).child("Nama").setValue(id.text.toString())
                                            myRef.child(email.text.toString().replace(".", "_")).child("Pass").setValue(pw.text.toString())
                                            myRef.child(email.text.toString().replace(".", "_")).child("Via").setValue("Apk")
                                            Toast.makeText(this@Regis, "Akun Dibuat", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@Regis, Main::class.java)
                                            startActivity(intent)
                                            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
                                            finish()
                                        } else {
                                            Toast.makeText(this@Regis, "Coba beberapa saat lagi", Toast.LENGTH_SHORT).show()
                                        }
                                    })

                                } else {
                                    Toast.makeText(this@Regis, "Email telah digunakan", Toast.LENGTH_SHORT).show()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }else{
                        pwc.setError("Password tidak cocok")
                        pwc.requestFocus()

                    }
                }
            }

        }
    }
    override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event)
            val intent = Intent(this@Regis, Main::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
            return true
        }
        return false
    }
}
