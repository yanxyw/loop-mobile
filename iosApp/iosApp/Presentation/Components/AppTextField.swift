import SwiftUI
import shared

struct AppTextField: View {
    let colors: IOSColorScheme
    let label: String
    let placeholder: String
    @Binding var text: String
    var isSecure: Bool = false
    @FocusState private var isFocused: Bool

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

                Group {
                    if isSecure {
                        SecureField("", text: $text)  // empty placeholder here
                            .foregroundColor(Color(colors.onSurface))
                            .font(AppFont.inter(16))
                        
                    } else {
                        TextField("", text: $text)    // empty placeholder here
                            .foregroundColor(Color(colors.onSurface))
                            .font(AppFont.inter(16))
                    }
                }
                .focused($isFocused)
                .padding(14)
            }
            .background(RoundedRectangle(cornerRadius: 4).stroke(Color(colors.outlineVariant)))
            .frame(height: 48)
        }
    }
}
