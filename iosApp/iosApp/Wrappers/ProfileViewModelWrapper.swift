import shared

@MainActor
class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel
    private var isObserving = false

    @Published var state: ProfileState = ProfileState(isLoading: true, user: nil, error: nil)

    init() {
        self.viewModel = ProfileViewModelInjector().profileViewModel
        self.state = viewModel.state.value as? ProfileState ?? ProfileState(isLoading: true, user: nil, error: nil)
    }

    func start() {
        guard !isObserving else { return }
        isObserving = true

        viewModel.observeState { newState in
            self.state = newState
        }
    }

    func onIntent(_ action: ProfileAction) {
        viewModel.onIntent(action: action)
    }

    func loadProfile() {
        onIntent(ProfileActionLoadProfile())
    }
    
    func clearProfile() {
        viewModel.clearProfile()
    }

    deinit {
        viewModel.clear()
    }
}
