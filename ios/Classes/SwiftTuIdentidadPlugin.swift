import Flutter
import UIKit
import TuIdentidadSDK

public class SwiftTuIdentidadPlugin: NSObject, FlutterPlugin, IDValidationDelegate{
    var _controller : UIViewController
    var _result: FlutterResult?

    
    init(uiViewController: UIViewController) {
        _controller = uiViewController
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "tu_identidad", binaryMessenger: registrar.messenger())
        
        let viewController: UIViewController =
            (UIApplication.shared.delegate?.window??.rootViewController)!;
        
        let instance = SwiftTuIdentidadPlugin(uiViewController: viewController)
        
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
                if (_result != nil) {
                    _result?(FlutterError(code:"multiple_request",
                    message:"Cancelled by a second request",
                    details:nil))
                    _result = nil;
                }
        switch call.method {
        case "init":
            handleInit(call: call, result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func handleInit(call: FlutterMethodCall, result: @escaping FlutterResult) {
        _result = result
        let arguments: Dictionary<String, Any> = call.arguments as! Dictionary
        

        let showTutorial: Bool = arguments["showTutorial"] as! Bool
        let showResults: Bool = arguments["showResults"] as! Bool
        let apiKey: String = arguments["apiKey"] as! String


        var INEMethod: Methods {
            switch arguments["method"] as! String {
        case "INE": return Methods.INE
        case "IDVAL": return Methods.IDVAL
        case "ONLYOCR": return Methods.ONLYOCR
        default: return Methods.INE
            }
        }
        let INEValidationInfo: Bool = arguments["INEValidationInfo"] as! Bool
        let INEValidationQuality: Bool = arguments["INEValidationQuality"] as! Bool
        let INEValidationPatterns: Bool = arguments["INEValidationPatterns"] as! Bool
        let INEValidationCurp: Bool = arguments["INEValidationCurp"] as! Bool
        
        TUID.instantiateIDAuth(delegate: self, context: _controller, apikey: apiKey, method: INEMethod,
        showResults: showResults, validateOptions: IDValidateOptions(checkInfo: INEValidationInfo, checkQuality: INEValidationQuality,
        checkPatterns: INEValidationPatterns, checkCurp: INEValidationCurp))
    }
    
    public func getData(data: IDValidation) {
        _result!(data)
    }
    
    public func getINEData(data: IDValidationINE) {
        _result!(data)
    }
    
    public func error(response: String) {
        _result!(response)
    }
    
}

