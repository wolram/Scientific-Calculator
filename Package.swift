// swift-tools-version: 6.0

import PackageDescription

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
            path: "Sources/ScientificCalculator"
        ),
        .testTarget(
            name: "ScientificCalculatorTests",
            dependencies: ["ScientificCalculator"],
            path: "Tests/ScientificCalculatorTests"
        )
    ]
)
