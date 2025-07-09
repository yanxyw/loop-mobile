import SwiftUI
import GoogleSignIn
import shared

struct SocialSignInButton: View {
    @ObservedObject var loginViewModelWrapper: LoginViewModelWrapper
    let colors: IOSColorScheme
    let onLoginSuccess: () -> Void
    let provider: String
    var isDisabled: Bool = false

    var body: some View {
        let state = loginViewModelWrapper.state
        
        Button(action: {
            handleGoogleSignIn()
        }) {
            HStack(spacing: 8) {
                if state.isLoading && state.loadingProvider == provider {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(0.8)
                        .foregroundColor(Color(colors.onSurface))
                    
                    Text("Loading...")
                        .font(AppFont.inter(16))
                        .foregroundColor(Color(colors.onSurface))
                } else {
                    Image(provider)
                        .resizable()
                        .frame(width: 20, height: 20)
                        .foregroundColor(Color(colors.primary))
                    
                    Text(provider.capitalized)
                        .padding(.leading, 12)
                        .font(AppFont.inter(16))
                        .foregroundColor(Color(colors.onSurface))
                }
            }
            .frame(maxWidth: .infinity, minHeight: 48)
            .padding(.horizontal, 12)
            .overlay(
                RoundedRectangle(cornerRadius: 4)
                    .stroke(Color(colors.outlineVariant), lineWidth: 1.5)
            )
            .background(Color.clear)
            .cornerRadius(4)
        }
        .disabled(state.isLoading || isDisabled)
        .onReceive(loginViewModelWrapper.$state) { state in
            if state.isSuccess {
                loginViewModelWrapper.clearState()
                onLoginSuccess()
            }
            
            if let error = state.error {
                print("Login failed: \(error)")
            }
        }
    }
    
    private func handleGoogleSignIn() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let presentingViewController = windowScene.windows.first?.rootViewController else {
            print("No presenting view controller found")
            return
        }
        
        GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { result, error in
            if let error = error {
                handleSignInError(error)
                return
            }
            
            guard let result = result else {
                print("No result from Google Sign-In")
                return
            }
            
            handleSignInResult(result)
        }
    }
    
    private func handleSignInResult(_ result: GIDSignInResult) {
        let user = result.user
        
        guard let idToken = user.idToken?.tokenString else {
            print("Failed to get ID token")
            return
        }
        
        print("Google ID Token received")
        
        loginViewModelWrapper.onIntent(
            LoginActionOnOAuthLogin(
                provider: provider,
                code: idToken,
                redirectUri: ""
            )
        )
    }
    
    private func handleSignInError(_ error: Error) {
        if let gidError = error as? GIDSignInError {
            switch gidError.code {
            case .canceled:
                print("User cancelled sign-in")
            case .hasNoAuthInKeychain:
                print("No previous authentication found")
            case .unknown:
                print("Unknown error occurred")
            default:
                print("Google sign-in failed: \(error.localizedDescription)")
            }
        } else {
            print("Google sign-in failed: \(error.localizedDescription)")
        }
    }
}
