import SwiftUI
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    @StateObject private var themeStateHolder = ThemeStateHolder(themeManager: ThemeManagerInjector().themeManager)
    
    @State private var isAuthRestored = false

    var body: some Scene {
        WindowGroup {
            if isAuthRestored {
                MainTabView()
                    .environmentObject(themeStateHolder)
            } else {
                SplashView()
                    .environmentObject(themeStateHolder)
                    .onAppear {
                        restoreAuth {
                            DispatchQueue.main.async {
                                withAnimation(.easeInOut) {
                                    isAuthRestored = true
                                }
                            }
                        }
                    }
            }
        }
    }

    func restoreAuth(completion: @escaping () -> Void) {
        let sessionStorage = SessionStorageInjector().sessionStorage
        let userRepository = UserRepositoryInjector().userRepository
        let authStateManager = AuthStateManagerInjector().authStateManager

        Task {
            do {
                try await withCheckedThrowingContinuation { continuation in
                    sharedApiRestoreAuthState(
                        sessionStorage: sessionStorage,
                        userRepository: userRepository,
                        authStateManager: authStateManager,
                        continuation: continuation
                    )
                }
                completion()
            } catch {
                print("Failed to restore auth state: \(error)")
                completion()
            }
        }
    }

    func sharedApiRestoreAuthState(
        sessionStorage: SessionStorage,
        userRepository: UserRepository,
        authStateManager: AuthStateManager,
        continuation: CheckedContinuation<Void, Error>
    ) {
        AuthInitializerKt.restoreAuthState(
            sessionStorage: sessionStorage,
            userRepository: userRepository,
            authStateManager: authStateManager
        ) { error in
            if let error = error {
                print("Failed to restore auth state: \(error)")
                continuation.resume(throwing: error) // Resume with error
            } else {
                print("Auth state restored successfully.")
                continuation.resume(returning: ()) // Resume normally
            }
        }
    }
}
