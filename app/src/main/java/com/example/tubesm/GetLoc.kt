package com.example.tubesm

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.tubesm.Utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class GetLoc : Service() {
    private lateinit var listener: LocationListener
    var database = FirebaseDatabase.getInstance()
    var myRef1 = database.getReference("User")
    private lateinit var mAuth: FirebaseAuth
    lateinit var locationManager: LocationManager
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mAuth = FirebaseAuth.getInstance()
                val user = mAuth.currentUser
                val mail = user?.email.toString()
                val lat = location.latitude
                val lon = location.longitude
                if (User.isLogin(applicationContext)) {
                    myRef1.child(mail.replace(".", "_")).child("Loc").child("Lon").setValue(lon)
                    myRef1.child(mail.replace(".", "_")).child("Loc").child("Lat").setValue(lat)
                }
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }
        }

            locationManager.requestLocationUpdates("gps", 2000, 0f, listener as LocationListener)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}