import SwiftUI

extension View {
    func hideKeyboardOnTap() -> some View {
        self
            .contentShape(Rectangle()) // Make entire area tappable
            .onTapGesture {
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder),
                                                to: nil, from: nil, for: nil)
            }
    }
}
