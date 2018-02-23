package com.clevernamehere.toggles

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch

import kotlinx.android.synthetic.main.activity_toggle.*

const val PERMISSIONS_REQUEST_BLUETOOTH = 1
const val PERMISSIONS_REQUEST_WIFI = 2
const val PERMISSIONS_REQUEST_SETTINGS_AUTO_BR = 3
const val PERMISSIONS_REQUEST_SETTINGS_BR_LVL = 4

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
                // If request is cancelled, the result arrays are empty.\
                if (grantResults.isEmpty()) {
                    return
                }

                var bluetoothResult = grantResults[0] == PackageManager.PERMISSION_GRANTED
                var bluetoothAdminResult = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (bluetoothResult && bluetoothAdminResult) {

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
            PERMISSIONS_REQUEST_WIFI -> {
                if (grantResults.isEmpty()) {
                    return
                }
                var wifiResult =  grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (wifiResult) {
                    var wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                    if (wifiMgr != null) {
                        wifiMgr.setWifiEnabled(true)
                    }
                }
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

        val permissionCheckBluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permissionCheckAdmin = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
        // if we have permission, enable it, otherwise ask
        if (permissionCheckBluetooth == PackageManager.PERMISSION_GRANTED && permissionCheckAdmin == PackageManager.PERMISSION_GRANTED) {
            var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            // make sure we have an adapter
            if (bluetoothAdapter != null) {
                if (toggled) bluetoothAdapter.enable() else bluetoothAdapter.disable()
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
                    PERMISSIONS_REQUEST_BLUETOOTH)
        }
    }

    fun toggleWifi(view: View) {
        var wifiToggle = findViewById<Switch>(R.id.wifiToggle)
        var toggled = wifiToggle.isChecked()

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            var wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (wifiMgr != null) {
                if (toggled) wifiMgr.setWifiEnabled(true) else wifiMgr.setWifiEnabled(false)
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CHANGE_WIFI_STATE),
                    PERMISSIONS_REQUEST_WIFI)
        }
    }

    fun toggleAutoBrightness(view: View) {
        var brightnessToggle = findViewById<Switch>(R.id.brightnessToggle)
        var toggled = brightnessToggle.isChecked()

        var permissionCheck = Settings.System.canWrite(applicationContext)

        if (permissionCheck) {
            // write the new value to the settings (apparently there's no manager or utility class for this)
            Settings.System.putInt(applicationContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    if (toggled) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)

        } else {
            intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            startActivityForResult(intent, PERMISSIONS_REQUEST_SETTINGS_AUTO_BR)

        }
    }

    fun setBrightnessLevel(view: View) {
        var brightnessLevel = findViewById<SeekBar>(R.id.brightnessSeekBar)
        var level = brightnessLevel.progress

        var permissionCheck = Settings.System.canWrite(applicationContext)

        if (permissionCheck) {
            // write the new value to the settings (apparently there's no manager or utility class for this)

            // turn off auto
            Settings.System.putInt(applicationContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)

            // set the new level
            Settings.System.putInt(applicationContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    level)

        } else {
            // send the user to settings to enable writes
            intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            startActivityForResult(intent, PERMISSIONS_REQUEST_SETTINGS_BR_LVL)
        }
    }

    fun setRingMode(view: View) {
        var ringMode = findViewById<Spinner>(R.id.ringModeSpinner)
        var mode = ringMode.selectedItem

        var audioMgr = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (mode) {
            "Ring" -> {
                audioMgr.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
            "Vibrate" -> {
                audioMgr.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            }
            // need to handle DND perms?
//            "Silent" -> {
//                audioMgr.ringerMode = AudioManager.RINGER_MODE_SILENT
//            }
        }
    }
}
