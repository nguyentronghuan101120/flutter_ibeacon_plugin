import 'package:flutter/material.dart';
import 'package:ibeacon_plugin/ibeacon_plugin.dart';

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
  final _ibeaconPlugin = IbeaconPlugin();

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
          iBeaconStatus = await _ibeaconPlugin.enableBeacon() ?? false;
          setState(() {});
        }),
      ),
    );
  }
}
