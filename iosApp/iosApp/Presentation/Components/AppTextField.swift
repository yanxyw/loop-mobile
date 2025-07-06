import SwiftUI
import shared

struct AppTextField: View {
    let colors: IOSColorScheme
    let label: String
    let placeholder: String
    @Binding var text: String
    var isSecure: Bool = false
    var onBlur: (() -> Void)? = nil
    var error: String? = nil
    var touched: Bool = false

    @FocusState private var isFocused: Bool
    @State private var isTextVisible: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .foregroundColor(Color(colors.onSurface))
                .font(AppFont.inter(16))

            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .foregroundColor(Color(colors.outlineVariant))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 14)
                }

                HStack {
                    Group {
                        if isSecure && !isTextVisible {
                            SecureField("", text: $text)
                        } else {
                            TextField("", text: $text)
                        }
                    }
                    .focused($isFocused)
                    .onChange(of: isFocused) { newValue in
                        if !newValue {
                            onBlur?()
                        }
                    }
                    .foregroundColor(Color(colors.onSurface))
                    .frame(height: 20)
                    .font(AppFont.inter(16))
                    .padding(.vertical, 14)
                    .padding(.leading, 14)

                    if isSecure {
                        Button(action: {
                            isTextVisible.toggle()
                        }) {
                            Image(isTextVisible ? "eye_open" : "eye_closed")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20, height: 20)
                                .foregroundColor(Color(colors.outlineVariant))
                        }
                        .padding(.trailing, 14)
                    }
                }
            }
            .background(
                RoundedRectangle(cornerRadius: 4)
                    .stroke(error != nil ? Color(colors.error) : Color(colors.outlineVariant))
            )
            .frame(height: 48)

            Group {
                if touched, let error = error {
                    Text(error)
                        .foregroundColor(Color(colors.error))
                        .font(AppFont.inter(14))
                        .transition(
                            .asymmetric(
                                insertion: .move(edge: .top).combined(with: .opacity),
                                removal: .move(edge: .top).combined(with: .opacity)
                            )
                        )
                }
            }
            .animation(.easeInOut(duration: 0.25), value: error)
        }
        .animation(.easeInOut(duration: 0.25), value: error)
    }
}
