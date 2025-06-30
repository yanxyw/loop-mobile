import SwiftUI
import shared

struct LibraryScreen: View {
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    let colors: IOSColorScheme

    var body: some View {
        VStack {
            Text("Library")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(colors.background))
        .ignoresSafeArea()
        .foregroundColor(Color(colors.primary))
        .animation(.default, value: colors.background)
    }
}
