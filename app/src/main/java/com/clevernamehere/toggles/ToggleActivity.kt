package com.clevernamehere.toggles

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch

import kotlinx.android.synthetic.main.activity_toggle.*

const val PERMISSIONS_REQUEST_BLUETOOTH = 1

class ToggleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toggle)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toggle, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_BLUETOOTH -> {
                // If request is cancelled, the result arrays are empty.
                var bluetoothResult = grantResults[0] == PackageManager.PERMISSION_GRANTED
                var bluetoothAdminResult = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if ((grantResults.isNotEmpty() && bluetoothResult && bluetoothAdminResult)) {

                    // permission was granted, yay! Do the
                    // bluetooth task you need to do.
                    var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                    // make sure we have an adapter
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.enable()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun toggleBluetooth(view: View) {
        var bluetoothToggle = findViewById<Switch>(R.id.bluetoothToggle)
        var toggled = bluetoothToggle.isChecked()

        if (toggled) {
            val permissionCheckBluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            val permissionCheckAdmin = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            // if we have permission, enable it, otherwise ask
            if (permissionCheckBluetooth == PackageManager.PERMISSION_GRANTED && permissionCheckAdmin == PackageManager.PERMISSION_GRANTED) {
                var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                // make sure we have an adapter
                if (bluetoothAdapter != null) {
                    bluetoothAdapter.enable()
                }
            } else {
                Log.d("ELSE", "Hitting else, requesting perms")
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
                        PERMISSIONS_REQUEST_BLUETOOTH)
            }
        }
        return
    }
}
