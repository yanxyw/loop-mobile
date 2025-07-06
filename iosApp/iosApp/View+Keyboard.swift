import SwiftUI

extension View {
    func hideKeyboardOnTap<T: Hashable>(focusedField: Binding<T?>) -> some View {
        self
            .contentShape(Rectangle()) // Make entire area tappable
            .onTapGesture {
                focusedField.wrappedValue = nil // dismiss keyboard by clearing focus
            }
    }
}
