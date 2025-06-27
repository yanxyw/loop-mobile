import SwiftUI
import shared

struct MainTabView: View {
    @StateObject private var authWrapper = AuthStateWrapper(manager: AuthStateManagerInjector().authStateManager)
    @State private var showLogin = false

    var body: some View {
        TabView {
            NavigationStack {
                HomeScreen(authWrapper: authWrapper) {
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
                ProfileScreen()
            }
            .tabItem {
                Label("Me", systemImage: "person.crop.circle")
            }
        }
    }
}
