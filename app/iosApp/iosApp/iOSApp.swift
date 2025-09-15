import SwiftUI
import UIKit
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate

    init() {
        KoinInit_iosKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        // Return the desired orientation mask, e.g., .portrait, .landscape, or .all
        return .portrait
    }
}