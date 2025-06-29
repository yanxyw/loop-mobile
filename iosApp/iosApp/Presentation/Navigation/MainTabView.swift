import SwiftUI
import shared

struct MainTabView: View {
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    @StateObject private var authWrapper = AuthStateWrapper(manager: AuthStateManagerInjector().authStateManager)
    @State private var showLogin = false
    
    var colors: IOSColorScheme {
        themeStateHolder.getColors()
    }

    var body: some View {
        TabView {
            NavigationStack {
                HomeScreen(authWrapper: authWrapper, colors: colors) {
                    showLogin = true
                }
                .navigationDestination(isPresented: $showLogin) {
                    LoginScreen()
                }
            }
            .tabItem {
                Label("Home", systemImage: "house")
            }

            NavigationStack {
                SearchScreen()
            }
            .tabItem {
                Label("Search", systemImage: "magnifyingglass")
            }

            NavigationStack {
                LibraryScreen()
            }
            .tabItem {
                Label("Library", systemImage: "books.vertical")
            }

            NavigationStack {
                ProfileScreen(colors: colors)
            }
            .tabItem {
                Label("Me", systemImage: "person.crop.circle")
            }
        }
    }
}
