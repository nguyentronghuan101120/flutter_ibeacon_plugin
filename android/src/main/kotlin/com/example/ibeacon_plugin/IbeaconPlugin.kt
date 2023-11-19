package com.example.ibeacon_plugin

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result




/** IbeaconPlugin */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class IbeaconPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ibeacon_plugin")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "triggerIBeacon") {
      startAdvertising()
      result.success(true)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private var isAdvertising = false

 private val bluetoothManager: BluetoothManager by lazy {
   getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
 }

 private val bluetoothAdapter: BluetoothAdapter by lazy {
   bluetoothManager.adapter
 }

 private val advertiser: BluetoothLeAdvertiser by lazy {
   bluetoothAdapter.bluetoothLeAdvertiser
 }


  private fun startAdvertising(): Boolean {

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
    advertiser.startAdvertising(
      settingsBuilder.build(),
      advertiseData.build(),
      advertiseCallback
    )
    return true

  }

  private fun stopAdvertising(): Boolean {
    advertiser.stopAdvertising(advertiseCallback)
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
