import SwiftUI
import shared

struct SearchScreen: View {
    @StateObject private var viewModelWrapper = SearchViewModelWrapper()

    var body: some View {
        VStack {
            TextField("Search", text: Binding(
                get: { viewModelWrapper.state.query },
                set: { viewModelWrapper.onIntent(SearchAction.OnQueryChange(query: $0)) }
            ))
            .textFieldStyle(.roundedBorder)

            Button("Search") {
                viewModelWrapper.onIntent(SearchAction.OnSearch())
            }

            if viewModelWrapper.state.isLoading {
                ProgressView()
            } else {
                List(viewModelWrapper.state.results, id: \.self) {
                    Text($0)
                }
            }
        }
        .padding()
        .onAppear {
            viewModelWrapper.start()
        }
    }
}
