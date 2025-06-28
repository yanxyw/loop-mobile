import shared
import Combine

extension Kotlinx_coroutines_coreFlow {
    func asPublisher<T>() -> AnyPublisher<T, Never> {
        let subject = PassthroughSubject<T, Never>()

        let collector = FlowCollectorWrapper<T> { value in
            subject.send(value)
        }

        Task {
            do {
                try await self.collect(collector: collector)
            } catch {
                // Handle error if needed (but we’re ignoring it for now)
            }
        }

        return subject.eraseToAnyPublisher()
    }
}

private class FlowCollectorWrapper<T>: Kotlinx_coroutines_coreFlowCollector {
    let onValue: (T) -> Void

    init(onValue: @escaping (T) -> Void) {
        self.onValue = onValue
    }

    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        if let typedValue = value as? T {
            onValue(typedValue)
        }
        completionHandler(nil)
    }
}
