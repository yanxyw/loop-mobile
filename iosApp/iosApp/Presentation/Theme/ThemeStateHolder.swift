import SwiftUI
import Combine
import shared

extension Bool {
    func toKotlinBoolean() -> KotlinBoolean {
        KotlinBoolean(bool: self)
    }
}

class ThemeStateHolder: ObservableObject {
    private let themeManager: ThemeManager
    private var cancellables = Set<AnyCancellable>()

    @Published var overrideDarkMode: Bool? = nil

    init(themeManager: ThemeManager) {
        self.themeManager = themeManager

        themeManager.overrideDarkMode
            .asPublisher()
            .receive(on: DispatchQueue.main)
            .assign(to: \.overrideDarkMode, on: self)
            .store(in: &cancellables)
    }

    func setOverride(_ override: Bool?) {
        themeManager.setOverrideDarkMode(enabled: override?.toKotlinBoolean())
    }

    func getColors() -> IOSColorScheme {
        themeManager.getCurrentColorScheme().toIOSColorScheme()
    }
}
