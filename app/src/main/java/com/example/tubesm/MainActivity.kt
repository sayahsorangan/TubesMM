package com.example.tubesm

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tubesm.Utils.User

class MainActivity : AppCompatActivity() {
    var context = this
    var connectivity: ConnectivityManager? = null
    var netinfo: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)
        con()

    }

    fun con(): Int {
        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        netinfo = connectivity!!.activeNetworkInfo
        if (netinfo == null) {
            Toast.makeText(context, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show()
            return 0
        } else {
            return 1
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val net = con()
                if (net == 1) {
                    if (User.isLogin(applicationContext)){
                        Handler().postDelayed({
                            val intent = Intent(this, MapsActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
                            finish()
                        }, 1500)
                    } else {
                        Handler().postDelayed({
                            val intent = Intent(this, Main::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
                            finish()
                        }, 1500)
                    }

                }

            }
        }
    }
}