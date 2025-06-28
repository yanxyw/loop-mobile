import UIKit
import shared
import SwiftUI

struct ThemeSwitcherView: View {
    @ObservedObject var state: ThemeStateHolder

    var body: some View {
        Menu {
            Button("System") { state.setOverride(nil) }
            Button("Light") { state.setOverride(false) }
            Button("Dark") { state.setOverride(true) }
        } label: {
            Text(currentTitle)
                .padding()
                .background(Color(.systemGray5))
                .cornerRadius(8)
        }
    }

    var currentTitle: String {
        switch state.overrideDarkMode {
        case nil: return "System"
        case false: return "Light"
        case true: return "Dark"
        default: return "System"
        }
    }
}
