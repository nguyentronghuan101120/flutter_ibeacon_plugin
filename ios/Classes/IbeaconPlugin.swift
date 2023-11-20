import Flutter
import UIKit

public class IbeaconPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "ibeacon_plugin", binaryMessenger: registrar.messenger())
        let instance = IbeaconPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    let beaconManager = BeaconManager()
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "enableBeacon":
            self.handleTiggerBeacon(call, result: result)
        case "disableBeacon":
            self.beaconManager.stopAdvertising()
            result(false)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    @objc func handleTiggerBeacon(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        
        var uuidFromFlutter = ""
        
        if let args = call.arguments as? Dictionary<String, Any>,
           let uuid = args["uuid"] as? String {
            
            uuidFromFlutter = uuid
            
            
            
        } else {
            result(FlutterError.init(code: "errorSetDebug", message: "Not have UUID", details: nil))
        }
        do {
            beaconManager.createBeaconRegion(uuid: uuidFromFlutter)
            guard beaconManager.isBluetoothAvailable == true else {
                return
            }
            
            beaconManager.startAdvertising()
            
            
            result(true)
        }  catch {
            
            result(false)
        }
    }
    
}
