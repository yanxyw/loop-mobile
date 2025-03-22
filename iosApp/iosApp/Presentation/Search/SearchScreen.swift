import SwiftUI
import shared

struct SearchScreen: View {
    @StateObject private var viewModelWrapper = SearchViewModelWrapper()

    var body: some View {
        VStack {
            TextField("Search", text: $viewModelWrapper.query)
            .textFieldStyle(.roundedBorder)

            Button("Search") {
                viewModelWrapper.search()
            }

            if viewModelWrapper.searchState.isLoading {
                ProgressView()
            } else {
                List(viewModelWrapper.searchState.results, id: \.self) {
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
