import SwiftUI
import shared

struct HomeScreen: View {
    @ObservedObject var authWrapper: AuthStateWrapper
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    let colors: IOSColorScheme
    var onLoginTap: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            if let user = authWrapper.user {
                Text("Welcome, \(user.username)!")
                    .font(AppFont.bitter(24))
            } else {
                Text("To create your own music space")
                    .font(AppFont.inter(14, weight: .light))

                Button("Login", action: onLoginTap)
                    .buttonStyle(.borderedProminent)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(colors.background))
        .ignoresSafeArea()
        .foregroundColor(Color(colors.primary))
        .animation(.default, value: colors.background)
    }
}
