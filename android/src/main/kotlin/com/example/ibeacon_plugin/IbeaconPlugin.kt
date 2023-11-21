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
import android.os.ParcelUuid
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
import java.nio.ByteBuffer
import java.util.UUID


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
        when (call.method) {
            "enableBeacon" -> {
                val hashMap = call.arguments as HashMap<*, *> //Get the arguments as a HashMap

                val uuid = hashMap["uuid"].toString()
                startAdvertising(uuid = uuid)
                result.success(true)

            }

            "disableBeacon" -> {
                stopAdvertising()
                result.success(false)
            }

            else -> {
                result.notImplemented()
            }
        }
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    private var bluetoothManager: BluetoothManager? = null


    private var bluetoothAdapter: BluetoothAdapter? = null

    private var advertiser: BluetoothLeAdvertiser? = null


    private fun startAdvertising(uuid: String) {
//
//        //Advertising packet
//        val payload = byteArrayOf(
//            0x02,
//            0x15,
//            0x01,
//            0x02,
//            0x03,
//            0x04,
//            0x01,
//            0x02,
//            0x03,
//            0x04,
//            0x01,
//            0x02,
//            0x03,
//            0x04,
//            0x01,
//            0x02,
//            0x03,
//            0x04,
//            0x0B,
//            0x0B,
//            0x0C,
//            0x0C,
//            0x01
//        )
//
//
//        val advertiseData = AdvertiseData.Builder()
//
//        advertiseData.addManufacturerData(0x004C, payload)
//
//
//        val settingsBuilder = AdvertiseSettings.Builder()
//        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
//        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
//        settingsBuilder.setConnectable(true);
//
//
//        // Have to check if permission to advertise bluetooth is permitted.
//        advertiser?.startAdvertising(
//            settingsBuilder.build(),
//            advertiseData.build(),
//            advertiseCallback
//        )
//

        try {
            val mAdData = AdvertiseData.Builder()
            val mManufacturerData = ByteBuffer.allocate(24)
            val uuid = getIdAsByte(UUID.fromString(uuid))

            mManufacturerData.put(0, 0xBE.toByte()) // Beacon Identifier
            mManufacturerData.put(1, 0xAC.toByte()) // Beacon Identifier

            for (i in 2..17) {
                mManufacturerData.put(i, uuid[i - 2]) // adding the UUID
            }

            mManufacturerData.put(18, 0x00.toByte()) // first byte of Major
            mManufacturerData.put(19, 0x09.toByte()) // second byte of Major
            mManufacturerData.put(20, 0x00.toByte()) // first minor
            mManufacturerData.put(21, 0x06.toByte()) // second minor
            mManufacturerData.put(22, 0xB5.toByte()) // txPower

            mAdData.addManufacturerData(0x004C, mManufacturerData.array()) // u

            val mBuilder = AdvertiseSettings.Builder()
            mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            mBuilder.setConnectable(true)
            mBuilder.setTimeout(0)
            mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)

            advertiser?.startAdvertising(mBuilder.build(), mAdData.build(), advertiseCallback);
        } catch (e: Exception) {
            print(e)
        }


    }

    fun setAdvertiseData() {
        val mBuilder = AdvertiseData.Builder()
        val mManufacturerData = ByteBuffer.allocate(24)
        val uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"))

        mManufacturerData.put(0, 0xBE.toByte()) // Beacon Identifier
        mManufacturerData.put(1, 0xAC.toByte()) // Beacon Identifier

        for (i in 2..17) {
            mManufacturerData.put(i, uuid[i - 2]) // adding the UUID
        }

        mManufacturerData.put(18, 0x00.toByte()) // first byte of Major
        mManufacturerData.put(19, 0x09.toByte()) // second byte of Major
        mManufacturerData.put(20, 0x00.toByte()) // first minor
        mManufacturerData.put(21, 0x06.toByte()) // second minor
        mManufacturerData.put(22, 0xB5.toByte()) // txPower

        mBuilder.addManufacturerData(224, mManufacturerData.array()) // using google's company ID

    }

    private fun getIdAsByte(uuid: UUID): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return buffer.array()
    }

    fun setAdvertiseSettings() {
        val mBuilder = AdvertiseSettings.Builder()
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        mBuilder.setConnectable(false)
        mBuilder.setTimeout(0)
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
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
