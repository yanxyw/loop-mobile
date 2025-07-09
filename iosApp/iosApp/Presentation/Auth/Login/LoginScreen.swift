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
        let state = loginViewModelWrapper.state
        
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
                            error: state.emailError,
                            touched: state.emailTouched
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
                            error: state.passwordError,
                            touched: state.passwordTouched
                        )
                        .focused($focusedField, equals: .password)
                        
                        Button(action: {
                            focusedField = nil
                            loginViewModelWrapper.login()
                        }) {
                            HStack(spacing: 8) {
                                if state.isLoading && state.loadingProvider == "password" {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle())
                                        .scaleEffect(0.8)
                                        .foregroundColor(Color(colors.onSurface))
                                    
                                    Text("Loading...")
                                        .font(AppFont.inter(16))
                                        .foregroundColor(Color(colors.onSurface))
                                } else {
                                    Text("Login")
                                        .font(AppFont.inter(16, weight: .medium))
                                }
                            }
                            .frame(maxWidth: .infinity, minHeight: 48)
//                            .padding()
                            .background(Color(colors.primary))
                            .foregroundColor(Color(colors.background))
                            .cornerRadius(4)
                        }
                        .padding(.top, 8)
                        .disabled(state.isLoading)
                        
                        if let error = state.error {
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
            .onChange(of: state.isSuccess) { success in
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
