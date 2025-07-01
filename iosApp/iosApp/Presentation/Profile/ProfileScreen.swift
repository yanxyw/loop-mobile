import SwiftUI
import shared

struct ProfileScreen: View {
    @ObservedObject var profileViewModelWrapper: ProfileViewModelWrapper
    @ObservedObject var logoutViewModelWrapper: LogoutViewModelWrapper
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    let colors: IOSColorScheme
    let onLogoutSuccess: () -> Void

    var body: some View {
        ZStack {
            VStack(spacing: 16) {
                if profileViewModelWrapper.state.isLoading {
                    ProgressView()
                } else if let user = profileViewModelWrapper.state.user {
                    if let url = URL(string: user.profileUrl ?? "") {
                        AsyncImage(url: url) { phase in
                            switch phase {
                            case .success(let image):
                                image
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 100, height: 100)
                                    .clipShape(Circle())
                            default:
                                ProgressView()
                            }
                        }
                    }

                    Text(user.username)
                        .foregroundColor(Color(colors.primary))
                        .font(.title2)
                        .bold()

                    Text(user.email)
                        .foregroundColor(Color(colors.onSurface))
                        .font(.subheadline)

                    Button("Logout") {
                        logoutViewModelWrapper.logout()
                    }

                    if let error = logoutViewModelWrapper.state.error {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                    }

                    if logoutViewModelWrapper.state.isLoading {
                        ProgressView()
                    }
                }

                Spacer()

                VStack(alignment: .leading) {
                    Text("Appearance")
                        .font(.headline)
                    ThemeSwitcherView(state: themeStateHolder)
                }
            }
            .padding()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(colors.background))
        .foregroundColor(Color(colors.primary))
        .onAppear {
            profileViewModelWrapper.start()
            logoutViewModelWrapper.start()
            if profileViewModelWrapper.state.user == nil && !profileViewModelWrapper.state.isLoading {
                profileViewModelWrapper.loadProfile()
            }
        }
        .onChange(of: logoutViewModelWrapper.state.isSuccess) { success in
            if success {
                profileViewModelWrapper.clearProfile()
                onLogoutSuccess()
            }
        }
    }
}
