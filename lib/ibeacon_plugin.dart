import 'ibeacon_plugin_platform_interface.dart';

class IbeaconPlugin {
  Future<bool?> toogleBeacon({bool status = false,required String uuid}) {
    if (status) {
      return IbeaconPluginPlatform.instance.enableBeacon(uuid:uuid);
    } else {
      return IbeaconPluginPlatform.instance.disableBeacon();
    }
  }
}
