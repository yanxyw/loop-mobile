import SwiftUI
import shared

struct LoginScreen: View {
    @ObservedObject var loginViewModelWrapper: LoginViewModelWrapper
    let colors: IOSColorScheme
    let onLoginSuccess: () -> Void

    var body: some View {
        VStack(spacing: 20) {
            TextField("Email", text: $loginViewModelWrapper.email)
                .textFieldStyle(.roundedBorder)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)

            SecureField("Password", text: $loginViewModelWrapper.password)
                .textFieldStyle(.roundedBorder)

            if loginViewModelWrapper.state.isLoading {
                ProgressView()
            } else {
                Button("Login") {
                    loginViewModelWrapper.login()
                }
            }

            if let error = loginViewModelWrapper.state.error {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
        .padding()
        .onAppear {
            loginViewModelWrapper.start()
        }
        .onChange(of: loginViewModelWrapper.state.isSuccess) { success in
            if success {
                onLoginSuccess()
            }
        }
    }
}
