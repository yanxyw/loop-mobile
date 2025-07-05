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
        ZStack {
            VStack(spacing: 0) {
                ZStack {
                    switch selectedTab {
                    case .home:
                        NavigationStack {
                            HomeScreen(authWrapper: authWrapper, colors: colors) {
                                withAnimation {
                                    showLogin = true
                                }
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
                                withAnimation {
                                    showLogin = true
                                }
                            }
                        }
                    }
                }
                // Tab bar
                HStack {
                    ForEach(Tab.allCases, id: \.self) { tab in
                        Spacer()
                        Button(action: {
                            selectedTab = tab
                        }) {
                            VStack {
                                Image(selectedTab == tab ? tab.iconName(isSelected: true) : tab.iconName(isSelected: false))
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 20, height: 20)
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

            // Overlay LoginScreen when needed
            if showLogin {
                LoginScreen(
                    loginViewModelWrapper: loginViewModelWrapper,
                    colors: colors,
                    onLoginSuccess: {
                        withAnimation {
                            showLogin = false
                            selectedTab = .home
                        }
                    },
                    onBack: {
                        withAnimation {
                            showLogin = false
                        }
                    }
                )
                .transition(.move(edge: .trailing)) // slide in from right
                .zIndex(1)
            }
        }
        .ignoresSafeArea(.keyboard)
        .animation(.easeInOut, value: showLogin)
    }
}
