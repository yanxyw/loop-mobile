import SwiftUI
import GoogleSignIn
import shared

struct SocialSignInButton: View {
    @ObservedObject var loginViewModelWrapper: LoginViewModelWrapper
    @State private var isLoading = false
    
    var body: some View {
        Button(action: {
            handleGoogleSignIn()
        }) {
            HStack {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(0.8)
                } else {
                    Image("home_filled") // Add Google logo
                        .resizable()
                        .frame(width: 20, height: 20)
                    
                    Text("Continue with Google")
                        .font(.system(size: 16, weight: .medium))
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .background(Color.blue) // Customize your button color
            .foregroundColor(.white)
            .cornerRadius(4)
        }
        .disabled(isLoading)
        .onReceive(loginViewModelWrapper.$state) { state in
            isLoading = state.isLoading
            
            if state.isSuccess {
                // Handle successful login - navigate to home
                print("Login successful")
                loginViewModelWrapper.clearState()
            }
            
            if let error = state.error {
                print("Login failed: \(error)")
                // Handle error - show alert or toast
            }
        }
    }
    
    private func handleGoogleSignIn() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let presentingViewController = windowScene.windows.first?.rootViewController else {
            print("No presenting view controller found")
            return
        }
        
        // Google Sign-In is already configured in AppDelegate
        // Just call signIn directly
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
        
        // Send to your login view model
        loginViewModelWrapper.onIntent(
            LoginActionOnOAuthLogin(
                provider: "google",
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
