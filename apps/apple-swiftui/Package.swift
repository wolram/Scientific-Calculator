// swift-tools-version: 6.0

import PackageDescription
import Foundation

let packageRoot = URL(fileURLWithPath: FileManager.default.currentDirectoryPath)
let iosInfoPlist = packageRoot
    .appendingPathComponent("Sources/ScientificCalculator/Support/Info-iOS.plist")
    .path
let macInfoPlist = packageRoot
    .appendingPathComponent("Sources/ScientificCalculator/Support/Info-macOS.plist")
    .path

let package = Package(
    name: "ScientificCalculator",
    platforms: [
        .macOS(.v14),
        .iOS(.v17)
    ],
    products: [
        .executable(
            name: "ScientificCalculator",
            targets: ["ScientificCalculator"]
        )
    ],
    targets: [
        .executableTarget(
            name: "ScientificCalculator",
            path: "Sources/ScientificCalculator",
            exclude: [
                "Support/Info-iOS.plist",
                "Support/Info-macOS.plist"
            ],
            linkerSettings: [
                // Embed an Info.plist into the Mach-O so UIKit/AppKit can resolve Bundle.main metadata.
                .unsafeFlags(
                    ["-Xlinker", "-sectcreate", "-Xlinker", "__TEXT", "-Xlinker", "__info_plist", "-Xlinker", iosInfoPlist],
                    .when(platforms: [.iOS])
                ),
                .unsafeFlags(
                    ["-Xlinker", "-sectcreate", "-Xlinker", "__TEXT", "-Xlinker", "__info_plist", "-Xlinker", macInfoPlist],
                    .when(platforms: [.macOS])
                )
            ]
        ),
        .testTarget(
            name: "ScientificCalculatorTests",
            dependencies: ["ScientificCalculator"],
            path: "Tests/ScientificCalculatorTests"
        )
    ]
)
