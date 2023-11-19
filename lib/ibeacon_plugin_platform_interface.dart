import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ibeacon_plugin_method_channel.dart';

abstract class IbeaconPluginPlatform extends PlatformInterface {
  /// Constructs a IbeaconPluginPlatform.
  IbeaconPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static IbeaconPluginPlatform _instance = MethodChannelIbeaconPlugin();

  /// The default instance of [IbeaconPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelIbeaconPlugin].
  static IbeaconPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [IbeaconPluginPlatform] when
  /// they register themselves.
  static set instance(IbeaconPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> enableBeacon() {
    throw UnimplementedError('This has not been implemented.');
  }

    Future<bool?> disableBeacon() {
    throw UnimplementedError('This has not been implemented.');
  }
}
