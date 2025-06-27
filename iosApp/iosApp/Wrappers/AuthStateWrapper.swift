import shared

@MainActor
class AuthStateWrapper: ObservableObject {
    @Published var user: DecodedUser? = nil
    @Published var isLoggedIn: Bool = false

    private var userJob: Kotlinx_coroutines_coreJob?
    private let manager: AuthStateManager

    init(manager: AuthStateManager) {
        self.manager = manager

        userJob = manager.observeUser { [weak self] user in
            Task { @MainActor in
                self?.user = user
                self?.isLoggedIn = user != nil
            }
        }
    }

    deinit {
        userJob?.cancel(cause: nil)
    }
}
