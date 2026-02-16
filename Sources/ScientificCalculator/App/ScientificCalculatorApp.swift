import SwiftUI

@main
struct ScientificCalculatorApp: App {
    var body: some Scene {
        WindowGroup {
            MainMenuView()
                .preferredColorScheme(.dark)
                #if os(macOS)
                .frame(minWidth: 520, minHeight: 700)
                #endif
        }
        #if os(macOS)
        .windowStyle(.titleBar)
        .defaultSize(width: 520, height: 700)
        #endif
    }
}
