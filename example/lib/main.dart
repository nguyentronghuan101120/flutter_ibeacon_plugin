import 'package:bluetooth_enable_fork/bluetooth_enable_fork.dart';
import 'package:flutter/material.dart';
import 'package:ibeacon_plugin/ibeacon_plugin.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool iBeaconStatus = false;
  bool status = false;
  final _ibeaconPlugin = IbeaconPlugin();

  String dialogTitle = "Please give me permission to use Bluetooth!";
  bool displayDialogContent = true;
  String dialogContent = "This app requires Bluetooth to connect to device.";
  //or
  // bool displayDialogContent = false;
  // String dialogContent = "";
  String cancelBtnText = "Nope";
  String acceptBtnText = "Sure";
  double dialogRadius = 10.0;
  bool barrierDismissible = true;

  @override
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('IBeacon status: $iBeaconStatus\n'),
        ),
        floatingActionButton: FloatingActionButton(onPressed: () async {



          await BluetoothEnable.enableBluetooth.then((result) async {
            if (result == "true") {
              final bool isPermissionGranted = await Permission.bluetoothAdvertise.isGranted;

              if (isPermissionGranted) {
                status = !status;

                iBeaconStatus = await _ibeaconPlugin.toogleBeacon(status: status,uuid:"0A0B0C0D-8F07-4F07-A807-8D7FE5AD2984") ?? false;
              } else {
                await Permission.bluetoothAdvertise.request();
                await Permission.bluetooth.request();
              }
            } else if (result == "false") {
              await BluetoothEnable.showAlertDialog(
                context,
                dialogTitle,
                displayDialogContent,
                dialogContent,
                cancelBtnText,
                acceptBtnText,
                dialogRadius,
                barrierDismissible,
              );
            }
          });


          setState(() {});
        }),
      ),
    );
  }
}
