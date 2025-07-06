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
    @State private var loginOffset: CGFloat = 0

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
        let drag = DragGesture()
            .onChanged { value in
                loginOffset = max(value.translation.width, 0)
            }
            .onEnded { value in
                let dragThreshold: CGFloat = 100
                let velocityThreshold: CGFloat = 500
                
                if value.translation.width > dragThreshold || value.velocity.width > velocityThreshold {
                    withAnimation(.easeOut(duration: 0.3)) {
                        showLogin = false
                        loginOffset = 0
                    }
                } else {
                    withAnimation(.easeOut(duration: 0.2)) {
                        loginOffset = 0
                    }
                }
            }

        ZStack {
            VStack(spacing: 0) {
                ZStack {
                    switch selectedTab {
                    case .home:
                        NavigationStack {
                            HomeScreen(authWrapper: authWrapper, colors: colors) {
                                withAnimation {
                                    showLogin = true
                                    loginOffset = 0
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
                                    loginOffset = 0
                                }
                            }
                        }
                    }
                }
                
                // Tab bar - hide when login is shown
                if !showLogin {
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
                    .transition(.move(edge: .bottom))
                }
            }

            // Overlay LoginScreen when needed
            if showLogin {
                LoginScreen(
                    loginViewModelWrapper: loginViewModelWrapper,
                    colors: colors,
                    onLoginSuccess: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            showLogin = false
                            loginOffset = 0
                        }
                    },
                    onBack: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            showLogin = false
                            loginOffset = 0
                        }
                    }
                )
                .offset(x: loginOffset)
                .gesture(drag)
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .trailing)
                ))
                .zIndex(1)
            }
        }
    }
}
