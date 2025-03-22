import shared

@MainActor
class SearchViewModelWrapper: ObservableObject {
    private var tasks: [Task<Void, Never>] = []
    private let viewModel: SearchViewModel
    private var isObserving = false

    @Published var searchState: SearchState = SearchState(query: "", results: [], isLoading: false)

    var query: String {
        get { searchState.query }
        set { updateQuery(query: newValue) }
    }

    init() {
        self.viewModel = SearchViewModelInjector().searchViewModel
        searchState = viewModel.state.value as? SearchState ?? SearchState(query: "", results: [], isLoading: false)
    }

    func start() {
        startObserving()
    }

    func onIntent(action: SearchAction) {
        viewModel.onIntent(intent: action)
    }

    func updateQuery(query: String) {
        onIntent(action: SearchActionOnQueryChange(query: query))
    }

    func search() {
        onIntent(action: SearchActionOnSearch())
    }

    private func startObserving() {
        guard !isObserving else { return }
        isObserving = true

        viewModel.observeState { state in
            DispatchQueue.main.async {
                self.searchState = state
            }
        }
    }

    deinit {
        tasks.forEach { $0.cancel() }
        viewModel.clear()
    }
}
