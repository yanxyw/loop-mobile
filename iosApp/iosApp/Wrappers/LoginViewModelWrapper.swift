import shared

@MainActor
class LoginViewModelWrapper: ObservableObject {
    private let viewModel: LoginViewModel
    private var isObserving = false

    @Published var state: LoginState = LoginState(email: "", password: "", emailTouched: false, passwordTouched: false, emailError: nil, passwordError: nil, isLoading: false, error: nil, isSuccess: false)

    var email: String {
        get { state.email }
        set { onIntent(LoginActionOnEmailChange(email: newValue)) }
    }

    var password: String {
        get { state.password }
        set { onIntent(LoginActionOnPasswordChange(password: newValue)) }
    }

    init() {
        self.viewModel = LoginViewModelInjector().loginViewModel
        self.state = viewModel.state.value as? LoginState ?? LoginState(email: "", password: "", emailTouched: false, passwordTouched: false, emailError: nil, passwordError: nil, isLoading: false, error: nil, isSuccess: false)
    }

    func start() {
        guard !isObserving else { return }
        isObserving = true

        viewModel.observeState { newState in
            self.state = newState
        }
    }

    func onIntent(_ action: LoginAction) {
        viewModel.onIntent(action: action)
    }

    func login() {
        onIntent(LoginActionOnLogin())
    }
    
    func clearState() {
        viewModel.clearState()
    }

    deinit {
        viewModel.clear()
    }
}
