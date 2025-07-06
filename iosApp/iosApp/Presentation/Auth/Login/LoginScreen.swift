import SwiftUI
import shared

struct LoginScreen: View {
    @ObservedObject var loginViewModelWrapper: LoginViewModelWrapper
    let colors: IOSColorScheme
    let onLoginSuccess: () -> Void
    let onBack: () -> Void
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        VStack(spacing: 0) {
            // Custom navigation bar
            HStack(spacing: 6) {
                Button(action: {
                    onBack()
                }) {
                    Image(systemName: "chevron.left")
                        .font(AppFont.bitter(18, weight: .semibold))
                        .foregroundColor(Color(colors.onSurface))
                        .frame(width: 44, height: 44) // tappable area
                }
                Text("Login")
                    .font(AppFont.bitter(24, weight: .semibold))
                    .foregroundColor(Color(colors.onSurface))

                Spacer()
            }
            .padding(.top,
                UIApplication.shared.connectedScenes
                    .compactMap { $0 as? UIWindowScene }
                    .flatMap { $0.windows }
                    .first(where: { $0.isKeyWindow })?
                    .safeAreaInsets.top ?? 20
            )
            .padding(.leading, 8)
            .padding(.trailing, 8)
            .background(Color(colors.background))
            
            Spacer().frame(height: 20)

            // Content below navigation bar
            VStack(spacing: 20) {
                AppTextField(
                    colors: colors,
                    label: "Email",
                    placeholder: "you@example.com",
                    text: $loginViewModelWrapper.email,
                    onBlur: {
                        loginViewModelWrapper.onIntent(LoginActionOnEmailBlur())
                    },
                    error: loginViewModelWrapper.state.emailError,
                    touched: loginViewModelWrapper.state.emailTouched
                )

                AppTextField(
                    colors: colors,
                    label: "Password",
                    placeholder: "••••••••",
                    text: $loginViewModelWrapper.password,
                    isSecure: true,
                    onBlur: {
                        loginViewModelWrapper.onIntent(LoginActionOnPasswordBlur())
                    },
                    error: loginViewModelWrapper.state.passwordError,
                    touched: loginViewModelWrapper.state.passwordTouched
                )
                
                // Login button
                Button(action: {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                    loginViewModelWrapper.login()
                }) {
                    HStack {
                        if loginViewModelWrapper.state.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: Color(colors.background)))
                        }
                        Text("Login")
                            .font(AppFont.inter(16, weight: .medium))
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(colors.primary))
                    .foregroundColor(Color(colors.background))
                    .cornerRadius(4)
                }
                .padding(.top, 8) 
                .disabled(loginViewModelWrapper.state.isLoading)

                if let error = loginViewModelWrapper.state.error {
                    Text(error)
                        .foregroundColor(Color(colors.error))
                        .font(.caption)
                }
            }
            .padding(.leading, 24)
            .padding(.trailing, 24)
            Spacer()
        }
        .hideKeyboardOnTap()
        .background(Color(colors.background))
        .ignoresSafeArea()
        .foregroundColor(Color(colors.primary))
        .onAppear {
            loginViewModelWrapper.start()
        }
        .onChange(of: loginViewModelWrapper.state.isSuccess) { success in
            if success {
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                loginViewModelWrapper.clearState()
                onLoginSuccess()
            }
        }
        // Hide the default navigation bar:
        .navigationBarHidden(true)
    }
}
