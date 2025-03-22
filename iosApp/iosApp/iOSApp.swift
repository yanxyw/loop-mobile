import SwiftUI

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        DiKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            SearchScreen()
        }
    }
}