package br.edu.utfpr.usandogps_pos2023

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import br.edu.utfpr.usandogps_pos2023.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager : LocationManager

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root )

        binding.btMapa.setOnClickListener {
            btMapaOnClick()
        }

        binding.btEndereco.setOnClickListener {
            btEnderecoOnClick()
        }

        binding.btImagem.setOnClickListener {
            btImagemOnClick()
        }

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

    private fun btImagemOnClick() {
        //https://maps.googleapis.com/maps/api/staticmap?center=-26.081185,-53.091238&zoom=15&size=400x400&key=AIzaSyBcnJmftWCsfnTjVtkP-ed3us1Jzi83M2c

        Thread ( Runnable {

            val enderecoURL = "https://maps.googleapis.com/maps/api/staticmap?center=${binding.tvLatitude.text.toString()},${binding.tvLongitude.text.toString()}&zoom=15&size=400x400&key=AIzaSyBcnJmftWCsfnTjVtkP-ed3us1Jzi83M2"
            val url = URL(enderecoURL)
            val urlConnection = url.openConnection()

            val inputStream = urlConnection.getInputStream()

            val imagem = BitmapFactory.decodeStream( inputStream )

            binding.ivMapa.setImageBitmap( imagem )

        } ).start()


        //formatar a imagem no ImageView
    }

    private fun btEnderecoOnClick() {
        Thread( Runnable {

            val enderecoURL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=${binding.tvLatitude.text.toString()},${binding.tvLongitude.text.toString()}&key=AIzaSyAIezMrULJJrEvIyXs2GTR7kbypkTNSQUE"
            val url = URL(enderecoURL)
            val urlConnection = url.openConnection()

            val inputStream = urlConnection.getInputStream()
            val entrada = BufferedReader( InputStreamReader( inputStream ) )

            val saida = StringBuilder()
            var caractere = entrada.readLine()

            while ( caractere != null ) {
                saida.append( caractere )
                caractere = entrada.readLine()
            }

            runOnUiThread {
                val enderecoFormatado = saida.substring(
                                            saida.indexOf( "<formatted_address>" ) + 19,
                                            saida.indexOf( "</formatted_address>")
                                        )

                exibirAlerta(enderecoFormatado)
            }

        } ).start()

    }

    fun exibirAlerta( msg : String ) {
        val dialog = AlertDialog.Builder( this )
        dialog.setTitle( "Endereço" )
        dialog.setMessage( msg )
        dialog.setNeutralButton( "OK", null )
        dialog.setCancelable( false )
        dialog.show()
    }

    private fun btMapaOnClick() {
        val intent = Intent( this, MapsActivity::class.java )
        intent.putExtra( "latitude", binding.tvLatitude.text.toString().toDouble() )
        intent.putExtra( "longitude", binding.tvLongitude.text.toString().toDouble() )
        startActivity( intent )
    }

    override fun onLocationChanged(location: Location) {

        binding.tvLatitude.text = location.latitude.toString()
        binding.tvLongitude.text = location.longitude.toString()

    }

    //AIzaSyBcnJmftWCsfnTjVtkP-ed3us1Jzi83M2c
}