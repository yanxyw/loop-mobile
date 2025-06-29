import SwiftUI
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    @StateObject private var themeStateHolder = ThemeStateHolder(themeManager: ThemeManagerInjector().themeManager)

    var body: some Scene {
        WindowGroup {
            MainTabView()
                .environmentObject(themeStateHolder)
                .onAppear {
                    restoreAuth()
                    updateTabBarColors()
                }
                .onChange(of: themeStateHolder.currentColors) { _ in
                    updateTabBarColors()
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
    
    func updateTabBarColors() {
        DispatchQueue.main.async {
            UITabBar.appearance().tintColor = themeStateHolder.getColors().primary
            UITabBar.appearance().unselectedItemTintColor = themeStateHolder.getColors().onSurfaceVariant
            // Force UITabBar to update appearance, if needed
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                windowScene.windows.forEach { $0.rootViewController?.view.setNeedsLayout() }
            }
        }
    }
}
