package br.edu.utfpr.usandogps_pos2023

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import br.edu.utfpr.usandogps_pos2023.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager : LocationManager

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root )

        locationManager = getSystemService( Context.LOCATION_SERVICE ) as LocationManager

        if ( ! locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            val intent = Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS )
            startActivity( intent )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        ActivityCompat.requestPermissions( this, arrayOf( Manifest.permission.ACCESS_FINE_LOCATION ), 1 )



        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0f, this )

    }

    override fun onLocationChanged(location: Location) {

        binding.tvLatitude.text = location.latitude.toString()
        binding.tvLongitude.text = location.longitude.toString()

    }

}