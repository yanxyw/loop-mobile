import SwiftUI
import shared

struct SearchScreen: View {
    @ObservedObject var searchViewModelWrapper: SearchViewModelWrapper
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    let colors: IOSColorScheme

    var body: some View {
        ZStack {
            // Full screen background
            Color(colors.background)
                .ignoresSafeArea()

            VStack(spacing: 12) {
                // Search area with custom background
                VStack(spacing: 8) {
                    TextField("Search", text: $searchViewModelWrapper.query)
                        .textFieldStyle(.roundedBorder)
                        .padding(.horizontal)

                    Button("Search") {
                        searchViewModelWrapper.search()
                    }
                    .padding(.horizontal)
                }
                .background(Color(colors.surface))
                .cornerRadius(10)
                .padding()

                if searchViewModelWrapper.searchState.isLoading {
                    ProgressView()
                        .padding()
                } else {
                    List(searchViewModelWrapper.searchState.results, id: \.self) { result in
                        Text(result)
                            .foregroundColor(Color(colors.primary))
                    }
                    .listStyle(PlainListStyle()) // Important to remove default inset style and bg
                    .background(Color(colors.background)) // Match full bg color
                }
            }
            .foregroundColor(Color(colors.primary))
        }
        .animation(.default, value: colors.background)
        .onAppear {
            searchViewModelWrapper.start()
        }
    }
}