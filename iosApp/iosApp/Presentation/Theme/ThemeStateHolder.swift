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
    @Published var currentColors: IOSColorScheme

    init(themeManager: ThemeManager) {
        self.themeManager = themeManager
        self.currentColors = themeManager.getCurrentColorScheme().toIOSColorScheme()

        themeManager.overrideDarkMode
            .asPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] newValue in
                self?.overrideDarkMode = newValue
                self?.currentColors = themeManager.getCurrentColorScheme().toIOSColorScheme()
            }
            .store(in: &cancellables)
    }

    func setOverride(_ override: Bool?) {
        themeManager.setOverrideDarkMode(enabled: override?.toKotlinBoolean())
    }

    func getColors() -> IOSColorScheme {
        currentColors
    }
}
