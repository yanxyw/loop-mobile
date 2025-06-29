import SwiftUI

enum AppFont {
    static func bitter(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        switch weight {
        case .bold:
            return .custom("Bitter-Bold", size: size)
        case .semibold:
            return .custom("Bitter-SemiBold", size: size)
        case .medium:
            return .custom("Bitter-Medium", size: size)
        case .light:
            return .custom("Bitter-Light", size: size)
        default:
            return .custom("Bitter-Regular", size: size)
        }
    }

    static func inter(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        switch weight {
        case .bold:
            return .custom("Inter-Bold", size: size)
        case .semibold:
            return .custom("Inter-SemiBold", size: size)
        case .medium:
            return .custom("Inter-Medium", size: size)
        case .light:
            return .custom("Inter-Light", size: size)
        default:
            return .custom("Inter-Regular", size: size)
        }
    }
}