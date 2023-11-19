
import 'ibeacon_plugin_platform_interface.dart';

class IbeaconPlugin {
  Future<bool?> enableBeacon() {
    return IbeaconPluginPlatform.instance.enableBeacon();
  }

    Future<bool?> disableBeacon() {
    return IbeaconPluginPlatform.instance.disableBeacon();
  }
}
