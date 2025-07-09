import UIKit
import shared
import GoogleSignIn

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        KoinInitializer_iosKt.doInitKoin()
        
        // Configure Google Sign-In with Web Client ID
        guard let webClientId = Bundle.main.object(forInfoDictionaryKey: "GOOGLE_CLIENT_ID") as? String else {
            print("GOOGLE_CLIENT_ID (Web Client ID) not found in Info.plist")
            return true
        }
        
        let config = GIDConfiguration(clientID: webClientId)
        GIDSignIn.sharedInstance.configuration = config
        
        return true
    }
    
    // Handle URL callbacks for Google Sign-In
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}
