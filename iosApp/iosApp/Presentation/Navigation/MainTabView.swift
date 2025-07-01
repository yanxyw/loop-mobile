import SwiftUI
import shared

struct MainTabView: View {
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    @StateObject private var authWrapper = AuthStateWrapper(manager: AuthStateManagerInjector().authStateManager)
    @StateObject private var loginViewModelWrapper = LoginViewModelWrapper()
    @StateObject private var profileViewModelWrapper = ProfileViewModelWrapper()
    @StateObject private var logoutViewModelWrapper = LogoutViewModelWrapper()
    @StateObject private var searchViewModelWrapper = SearchViewModelWrapper()
    @State private var showLogin = false
    @State private var selectedTab: Tab = .home

    var colors: IOSColorScheme {
        themeStateHolder.getColors()
    }

    enum Tab: String, CaseIterable {
        case home, search, library, profile

        var label: String {
            switch self {
            case .home: return "Home"
            case .search: return "Search"
            case .library: return "Library"
            case .profile: return "Me"
            }
        }

        func iconName(isSelected: Bool) -> String {
            switch self {
            case .home: return isSelected ? "home_filled" : "home_outlined"
            case .search: return isSelected ? "search_filled" : "search_outlined"
            case .library: return isSelected ? "library_filled" : "library_outlined"
            case .profile: return isSelected ? "profile_filled" : "profile_outlined"
            }
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            ZStack {
                switch selectedTab {
                case .home:
                    NavigationStack {
                        HomeScreen(authWrapper: authWrapper, colors: colors) {
                            showLogin = true
                        }
                        .navigationDestination(isPresented: $showLogin) {
                            LoginScreen(loginViewModelWrapper: loginViewModelWrapper)
                        }
                    }
                case .search:
                    NavigationStack {
                        SearchScreen(
                            searchViewModelWrapper: searchViewModelWrapper,
                            colors: colors
                        )
                    }
                case .library:
                    NavigationStack {
                        LibraryScreen(colors: colors)
                    }
                case .profile:
                    NavigationStack {
                        ProfileScreen(
                            profileViewModelWrapper: profileViewModelWrapper,
                            logoutViewModelWrapper: logoutViewModelWrapper,
                            colors: colors
                        ) {
                            showLogin = true
                        }
                        .navigationDestination(isPresented: $showLogin) {
                            LoginScreen(loginViewModelWrapper: loginViewModelWrapper)
                        }
                    }
                }
            }

            HStack {
                ForEach(Tab.allCases, id: \.self) { tab in
                    Spacer()
                    Button(action: {
                        selectedTab = tab
                    }) {
                        VStack {
                            if tab == .home {
                                Image(selectedTab == .home ? "home_filled" : "home_outlined")
                                    .renderingMode(.template)
                                    .resizable()
                                    .frame(width: 20, height: 20)
                            } else if tab == .search {
                                Image(selectedTab == .search ? "search_filled" : "search_outlined")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 20, height: 20)
                            } else if tab == .library {
                                Image(selectedTab == .library ? "library_filled" : "library_outlined")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 20, height: 20)
                            } else {
                                Image(selectedTab == .profile ? "profile_filled" : "profile_outlined")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 20, height: 20)
                            }
                            Text(tab.label)
                                .font(AppFont.inter(12, weight: .light))
                        }
                        .foregroundColor(selectedTab == tab ? Color(colors.primary) : Color(colors.onSurfaceVariant))
                    }
                    Spacer()
                }
            }
            .padding(.vertical, 8)
            .background(Color(colors.background))
        }
        .ignoresSafeArea(.keyboard)
    }
}
