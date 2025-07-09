import SwiftUI
import shared

struct LoginScreen: View {
    @ObservedObject var loginViewModelWrapper: LoginViewModelWrapper
    let colors: IOSColorScheme
    let onLoginSuccess: () -> Void
    let onBack: () -> Void
    @Environment(\.presentationMode) var presentationMode
    @FocusState private var focusedField: Field?
    
    enum Field {
        case email
        case password
    }

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = geometry.size.width * 0.06
            let baseTopInset = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .flatMap { $0.windows }
                .first(where: { $0.isKeyWindow })?
                .safeAreaInsets.top ?? 20

            let topInset = baseTopInset + 8
            
            ScrollView {
                VStack(spacing: 0) {
                    // Custom navigation bar
                    HStack(spacing: 0) {
                        Button(action: {
                            loginViewModelWrapper.clearState()
                            onBack()
                        }) {
                            Image(systemName: "chevron.left")
                                .font(AppFont.bitter(18, weight: .semibold))
                                .foregroundColor(Color(colors.onSurface))
                                .contentShape(Rectangle())
                                .padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 20))
                        }
                        
                        Text("Login")
                            .font(AppFont.bitter(24, weight: .semibold))
                            .foregroundColor(Color(colors.onSurface))
                        
                        Spacer()
                    }
                    .padding(.top, topInset)
                    .padding(.horizontal, horizontalPadding)
                    .background(Color(colors.background))
                    
                    // Content
                    VStack(spacing: geometry.size.height * 0.025) {
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
                        .focused($focusedField, equals: .email)
                        
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
                        .focused($focusedField, equals: .password)
                        
                        Button(action: {
                            focusedField = nil
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
                        
                        HStack {
                            Rectangle()
                                .fill(Color(colors.outlineVariant))
                                .frame(height: 1)
                                .frame(maxWidth: .infinity)
                            
                            Text("or login with")
                                .font(AppFont.inter(16))
                                .foregroundColor(Color(colors.outline))
                                .padding(.horizontal, 8)
                            
                            Rectangle()
                                .fill(Color(colors.outlineVariant))
                                .frame(height: 1)
                                .frame(maxWidth: .infinity)
                        }
                        .padding(.vertical, geometry.size.height * 0.015)
                        
                        HStack(spacing: 12) {
                            SocialSignInButton(
                                loginViewModelWrapper: loginViewModelWrapper,
                                colors: colors,
                                onLoginSuccess: onLoginSuccess,
                                provider: "google"
                            )
                            
                            SocialSignInButton(
                                loginViewModelWrapper: loginViewModelWrapper,
                                colors: colors,
                                onLoginSuccess: onLoginSuccess,
                                provider: "apple",
                                isDisabled: true
                            )
                        }
                    }
                    .padding(.top, geometry.size.height * 0.02)
                    .padding(.horizontal, horizontalPadding)
                }
            }
            .hideKeyboardOnTap(focusedField: Binding(
                get: { focusedField },
                set: { focusedField = $0 }
            ))
            .background(Color(colors.background))
            .edgesIgnoringSafeArea(.top)
            .foregroundColor(Color(colors.primary))
            .onAppear {
                loginViewModelWrapper.start()
            }
            .onChange(of: loginViewModelWrapper.state.isSuccess) { success in
                if success {
                    focusedField = nil
                    loginViewModelWrapper.clearState()
                    onLoginSuccess()
                }
            }
            .navigationBarHidden(true)
        }
    }
}
