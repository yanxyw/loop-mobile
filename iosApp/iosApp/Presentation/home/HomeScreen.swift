import SwiftUI
import shared

struct HomeScreen: View {
    @ObservedObject var authWrapper: AuthStateWrapper
    var onLoginTap: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            if let user = authWrapper.user {
                Text("Welcome, \(user.username)!")
                    .font(.title)
            } else {
                Text("To create your own music space")
                    .font(.headline)

                Button("Login", action: onLoginTap)
                    .buttonStyle(.borderedProminent)
            }
        }
        .padding()
    }
}
