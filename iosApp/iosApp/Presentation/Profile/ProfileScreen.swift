import SwiftUI
import shared

struct ProfileScreen: View {
    @EnvironmentObject var themeStateHolder: ThemeStateHolder
    let colors: IOSColorScheme

    var body: some View {
        VStack {
            Text("Profile")
                .foregroundColor(Color(colors.primary))

            ThemeSwitcherView(state: themeStateHolder)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(colors.background))
        .ignoresSafeArea()
        .foregroundColor(Color(colors.primary))
        .animation(.default, value: colors.background)
    }
}