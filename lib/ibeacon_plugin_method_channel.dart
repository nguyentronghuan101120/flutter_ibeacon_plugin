import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ibeacon_plugin_platform_interface.dart';

/// An implementation of [IbeaconPluginPlatform] that uses method channels.
class MethodChannelIbeaconPlugin extends IbeaconPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('ibeacon_plugin');

  @override
  Future<bool?> enableBeacon({required String uuid}) async {
    final bool iBeaconStatus = await methodChannel.invokeMethod<bool>('enableBeacon',{'uuid': uuid})  ?? false;
    return iBeaconStatus;
  }


  @override
  Future<bool?> disableBeacon() async {
    final bool iBeaconStatus = await methodChannel.invokeMethod<bool>('disableBeacon')  ?? false;
    return iBeaconStatus;
  }
}
