import shared

@MainActor
class LogoutViewModelWrapper: ObservableObject {
    private let viewModel: LogoutViewModel
    private var isObserving = false

    @Published var state: LogoutState = LogoutState(
        isLoading: false,
        error: nil,
        isSuccess: false,
        message: nil
    )

    init() {
        self.viewModel = LogoutViewModelInjector().logoutViewModel
        self.state = viewModel.state.value as? LogoutState ?? LogoutState(
                isLoading: false,
                error: nil,
                isSuccess: false,
                message: nil
        )
    }

    func start() {
        guard !isObserving else { return }
        isObserving = true

        viewModel.observeState { newState in
            self.state = newState
        }
    }

    func logout() {
        viewModel.logout()
    }

    deinit {
        viewModel.clear()
    }
}
