import SwiftUI
import shared

struct SplashView: View {
    @EnvironmentObject var themeStateHolder: ThemeStateHolder

    var colors: IOSColorScheme {
        themeStateHolder.getColors()
    }

    var body: some View {
        ZStack {
            Color(colors.background)
                .edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 20) {
                Image("app_icon") // Use your app logo or any icon
                    .resizable()
                    .scaledToFit()
                    .frame(width: 80, height: 80)
                    .foregroundColor(Color(colors.secondary))
            }
        }
    }
}
