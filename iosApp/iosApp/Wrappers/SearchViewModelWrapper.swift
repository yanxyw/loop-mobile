import Foundation
import shared

@MainActor
class SearchViewModelWrapper: ObservableObject {
    @Published var state: SearchState = SearchState(query: "", isLoading: false, results: [])

    let viewModel: SearchViewModel
    private var tasks: [Task<Void, Never>] = []

    init() {
        self.viewModel = DiKt.getViewModel() as! SearchViewModel
        self.state = viewModel.state.value
    }

    func start() {
        startObserving()
    }

    func onIntent(_ intent: SearchAction) {
        viewModel.onIntent(intent: intent)
    }

    private func startObserving() {
        viewModel.watchState { [weak self] newState in
            self?.state = newState
        }
    }

    deinit {
        tasks.forEach { $0.cancel() }
    }
}