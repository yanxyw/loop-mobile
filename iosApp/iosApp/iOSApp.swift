import SwiftUI
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            MainTabView()
                .onAppear {
                    restoreAuth()
                }
        }
    }

    func restoreAuth() {
        let tokenStorage = TokenStorageInjector().tokenStorage
        let userRepository = UserRepositoryInjector().userRepository
        let authStateManager = AuthStateManagerInjector().authStateManager

        Task {
            do {
                try await withCheckedThrowingContinuation { continuation in
                    sharedApiRestoreAuthState(
                        tokenStorage: tokenStorage,
                        userRepository: userRepository,
                        authStateManager: authStateManager,
                        continuation: continuation
                    )
                }
            } catch {
                print("Failed to restore auth state: \(error)")
            }
        }
    }

    func sharedApiRestoreAuthState(
        tokenStorage: TokenStorage,
        userRepository: UserRepository,
        authStateManager: AuthStateManager,
        continuation: CheckedContinuation<Void, Error>
    ) {
        AuthInitializerKt.restoreAuthState(
            tokenStorage: tokenStorage,
            userRepository: userRepository,
            authStateManager: authStateManager
        ) { error in
            if let error = error {
                print("Failed to restore auth state: \(error)")
            } else {
                print("Auth state restored successfully.")
            }
        }
    }
}
