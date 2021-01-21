package com.example.tubesm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tubesm.Utils.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Main : AppCompatActivity() {
    lateinit var regis: TextView
    lateinit var hlp: TextView
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var google: ImageView
    lateinit var login: ImageView
    private var mAuth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("User")
    var RC_SIGN_IN = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        regis = findViewById(R.id.regis)
        hlp = findViewById(R.id.hlp)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        login = findViewById(R.id.login)
        mAuth = FirebaseAuth.getInstance()
        google = findViewById(R.id.google_signIn4)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("111307908454-pg7qvp3dpir0kkunlvc1am36c9klga7u.apps.googleusercontent.com")
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        regis.setOnClickListener {
            val intent = Intent(this@Main, Regis::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
        }
        hlp.setOnClickListener {
            val intent = Intent(this@Main, Hlp::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
        }
        login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                validasi()
            }

            private fun validasi() {
                val mail = email.getText().toString()
                val pw = pass.getText().toString()
                if (mail.isEmpty()) {
                    email.setError("Tolong masukan email")
                    email.requestFocus()
                } else if (pw.isEmpty()) {
                    pass.setError("Tolong masukan password")
                    pass.requestFocus()
                } else if (!mail.isEmpty() && !pw.isEmpty()) {
                    dologin(mail, pw)
                }
            }
        })
        google.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val intent = googleSignInClient!!.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acc = completedTask.getResult(ApiException::class.java)
            FirebaseGoogleAuth(acc)
        } catch (e: ApiException) {
            Toast.makeText(this@Main, "Sign Gagal", Toast.LENGTH_LONG).show()
            FirebaseGoogleAuth(null)
        }
    }

    private fun FirebaseGoogleAuth(acc: GoogleSignInAccount?) {
        val authCredential = GoogleAuthProvider.getCredential(acc!!.idToken, null)
        mAuth!!.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                User.setLogin(applicationContext)
                myRef.keepSynced(true)
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val myRef = database.getReference("User")
                            val user = mAuth!!.currentUser
                            val usr = user!!.displayName
                            val pw = "Google Account"
                            val mail = user.email
                            val cekusr = snapshot.child(mail!!.replace(".", "_")).child("Nama").getValue(String::class.java)
                            if (cekusr == null) {
                                Toast.makeText(this@Main, "Akun dibuat", Toast.LENGTH_LONG).show()
                                myRef.child(mail.replace(".", "_")).child("Nama").setValue(usr)
                                myRef.child(mail.replace(".", "_")).child("Pass").setValue(pw)
                                myRef.child(mail.replace(".", "_")).child("Via").setValue("Google")
                                Toast.makeText(this@Main, "Berhasil Login", Toast.LENGTH_LONG).show()
                            } else {
                                val intent = Intent(this@Main, MapsActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@Main, "Berhasil Login", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Main, "Gagal Login", Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                Toast.makeText(this@Main, "Gagal Login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun dologin(mail: String, pw: String) {
        mAuth!!.signInWithEmailAndPassword(mail, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                User.setLogin(applicationContext)
                Toast.makeText(this@Main, "Berhasil Login", Toast.LENGTH_LONG).show()
                val intent = Intent(this@Main, MapsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
                finish()
            } else {
                Toast.makeText(this@Main, "Email atau Passowrd yang anda masukan salah", Toast.LENGTH_LONG).show()
            }
        }
    }
}