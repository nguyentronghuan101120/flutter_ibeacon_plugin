package com.example.ibeacon_plugin

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** IbeaconPlugin */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class IbeaconPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context


    /*bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    bluetoothAdapter = bluetoothManager?.adapter
    advertiser = bluetoothAdapter?.bluetoothLeAdvertiser*/

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
        advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ibeacon_plugin")
        channel.setMethodCallHandler(this)
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "enableBeacon") {
            startAdvertising()
            result.success(true)

        } else if (call.method == "disableBeacon") {
            stopAdvertising()
            result.success(false)
        } else {
            result.notImplemented()
        }
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    private var bluetoothManager: BluetoothManager? = null


    private var bluetoothAdapter: BluetoothAdapter? = null

    private var advertiser: BluetoothLeAdvertiser? = null


    private fun startAdvertising() {

        //Advertising packet
        val payload = byteArrayOf(
            0x02,
            0x15,
            0x01,
            0x02,
            0x03,
            0x04,
            0x01,
            0x02,
            0x03,
            0x04,
            0x01,
            0x02,
            0x03,
            0x04,
            0x01,
            0x02,
            0x03,
            0x04,
            0x0B,
            0x0B,
            0x0C,
            0x0C,
            0x01
        )


        val advertiseData = AdvertiseData.Builder()
        advertiseData.addManufacturerData(0x004C, payload)
        // advertiseData.addServiceData(ParcelUuid.fromString("0A0B0C0D-8F07-4F07-A807-8D7FE5AD2984"),payload)

        val settingsBuilder = AdvertiseSettings.Builder()
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        settingsBuilder.setConnectable(true);


        // Have to check if permission to advertise bluetooth is permitted.
        advertiser?.startAdvertising(
            settingsBuilder.build(),
            advertiseData.build(),
            advertiseCallback
        )


    }

    private fun stopAdvertising() {
        advertiser?.stopAdvertising(advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d("", "Advertiser called succesfully")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.d("", "Advertiser call failed")
        }
    }
}
