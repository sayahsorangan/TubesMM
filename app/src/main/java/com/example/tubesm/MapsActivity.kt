package com.example.tubesm

import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


@Suppress("UNREACHABLE_CODE")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationManager: LocationManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var listener: LocationListener
    var database = FirebaseDatabase.getInstance()
    var myRef1 = database.getReference("User")
    var x = true
    private lateinit var men: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        men = findViewById(R.id.menu)
        men.setOnClickListener {
            val intent = Intent(this@MapsActivity, Profil::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            finish()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle
                    )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }
        mMap = googleMap

        val latLngp = LatLng(0.0, 0.0)
        var m: Marker = mMap.addMarker(MarkerOptions().position(latLngp))
        m.remove()
        val posisi = arrayOf(
                mMap!!.addMarker(
                        MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                .position(latLngp)
                )
        )

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val intent = Intent(this, GetLoc::class.java)
        startService(intent)
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val lat = location.latitude
                val lon = location.longitude
                val latLngp = LatLng(lat, lon)
                posisi[0].remove()
                posisi[0] = mMap!!.addMarker(
                        MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                .position(latLngp)
                )
                if (x) {
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngp, 18f))
                    x=false
                }
                myRef1.orderByChild("Ket").equalTo("Ada").addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        val key = snapshot.key
                        mAuth = FirebaseAuth.getInstance()
                        val user = mAuth.currentUser
                        val mail = user!!.email
                        if (key != (mail!!.replace(".", "_"))) {
                            myRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (key != null) {
                                        m.remove()
                                        val lon1 = snapshot.child(key).child("Loc").child("Lon").value
                                        val lat1 = snapshot.child(key).child("Loc").child("Lat").value
                                        val latLng1 = LatLng(lat1 as Double, lon1 as Double)
                                        if ((lat1>=(lat-0.0005)) && (lat1<=(lat+0.0005)) && (lon1>=(lon-0.0005)) && (lon1<=(lon+0.0005))){
                                            m = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.sus)).position(latLng1))
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        m.remove()
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }
        }
        locationManager!!.requestLocationUpdates("gps", 2000, 0f, listener as LocationListener)
    }

}

