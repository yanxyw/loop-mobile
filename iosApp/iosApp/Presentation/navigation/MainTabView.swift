import SwiftUI
import shared

struct MainTabView: View {
    var body: some View {
        TabView {
            NavigationStack {
                HomeScreen()
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
