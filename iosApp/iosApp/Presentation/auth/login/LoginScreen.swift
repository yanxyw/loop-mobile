import SwiftUI
import shared

struct LoginScreen: View {
    @StateObject private var viewModelWrapper = LoginViewModelWrapper()

    var body: some View {
        VStack(spacing: 20) {
            TextField("Email", text: $viewModelWrapper.email)
                .textFieldStyle(.roundedBorder)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)

            SecureField("Password", text: $viewModelWrapper.password)
                .textFieldStyle(.roundedBorder)

            if viewModelWrapper.state.isLoading {
                ProgressView()
            } else {
                Button("Login") {
                    viewModelWrapper.login()
                }
            }

            if let error = viewModelWrapper.state.error {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
        .padding()
        .onAppear {
            viewModelWrapper.start()
        }
    }
}
