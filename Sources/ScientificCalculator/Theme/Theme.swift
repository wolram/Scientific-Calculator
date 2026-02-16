import SwiftUI

// MARK: - Catppuccin Mocha Color Palette

enum CatppuccinMocha {
    static let rosewater = Color(red: 0.96, green: 0.88, blue: 0.86)
    static let flamingo  = Color(red: 0.95, green: 0.80, blue: 0.80)
    static let pink      = Color(red: 0.96, green: 0.76, blue: 0.91)
    static let mauve     = Color(red: 0.80, green: 0.62, blue: 0.95)
    static let red       = Color(red: 0.95, green: 0.55, blue: 0.56)
    static let maroon    = Color(red: 0.92, green: 0.60, blue: 0.58)
    static let peach     = Color(red: 0.98, green: 0.70, blue: 0.53)
    static let yellow    = Color(red: 0.98, green: 0.85, blue: 0.59)
    static let green     = Color(red: 0.65, green: 0.89, blue: 0.63)
    static let teal      = Color(red: 0.60, green: 0.87, blue: 0.82)
    static let sky       = Color(red: 0.55, green: 0.85, blue: 0.90)
    static let sapphire  = Color(red: 0.45, green: 0.77, blue: 0.90)
    static let blue      = Color(red: 0.54, green: 0.71, blue: 0.98)
    static let lavender  = Color(red: 0.71, green: 0.74, blue: 0.98)

    static let text      = Color(red: 0.80, green: 0.84, blue: 0.96)
    static let subtext1  = Color(red: 0.73, green: 0.77, blue: 0.89)
    static let subtext0  = Color(red: 0.65, green: 0.69, blue: 0.82)
    static let overlay2  = Color(red: 0.58, green: 0.62, blue: 0.75)
    static let overlay1  = Color(red: 0.51, green: 0.55, blue: 0.68)
    static let overlay0  = Color(red: 0.43, green: 0.47, blue: 0.60)
    static let surface2  = Color(red: 0.36, green: 0.39, blue: 0.53)
    static let surface1  = Color(red: 0.28, green: 0.31, blue: 0.45)
    static let surface0  = Color(red: 0.19, green: 0.22, blue: 0.36)
    static let base      = Color(red: 0.12, green: 0.14, blue: 0.27)
    static let mantle    = Color(red: 0.09, green: 0.10, blue: 0.23)
    static let crust     = Color(red: 0.07, green: 0.07, blue: 0.19)
}

// MARK: - Button Styles

struct CalcButtonStyle: ButtonStyle {
    let background: Color
    let foreground: Color
    let cornerRadius: CGFloat

    init(
        background: Color = CatppuccinMocha.surface0,
        foreground: Color = CatppuccinMocha.text,
        cornerRadius: CGFloat = 12
    ) {
        self.background = background
        self.foreground = foreground
        self.cornerRadius = cornerRadius
    }

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 18, weight: .semibold, design: .rounded))
            .foregroundStyle(foreground)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .fill(configuration.isPressed ? background.opacity(0.6) : background)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

struct NumberButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 20, weight: .medium, design: .rounded))
            .foregroundStyle(CatppuccinMocha.text)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(configuration.isPressed
                          ? CatppuccinMocha.surface1.opacity(0.6)
                          : CatppuccinMocha.surface1)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

struct OperatorButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 20, weight: .bold, design: .rounded))
            .foregroundStyle(CatppuccinMocha.base)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(configuration.isPressed
                          ? CatppuccinMocha.peach.opacity(0.7)
                          : CatppuccinMocha.peach)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

struct FunctionButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 15, weight: .semibold, design: .rounded))
            .foregroundStyle(CatppuccinMocha.mauve)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(configuration.isPressed
                          ? CatppuccinMocha.surface0.opacity(0.6)
                          : CatppuccinMocha.surface0)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

struct AccentButtonStyle: ButtonStyle {
    let color: Color

    init(color: Color = CatppuccinMocha.blue) {
        self.color = color
    }

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 18, weight: .bold, design: .rounded))
            .foregroundStyle(CatppuccinMocha.base)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(configuration.isPressed
                          ? color.opacity(0.7)
                          : color)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

// MARK: - Display Style

struct DisplayModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .font(.system(size: 36, weight: .light, design: .monospaced))
            .foregroundStyle(CatppuccinMocha.text)
            .lineLimit(1)
            .minimumScaleFactor(0.4)
            .frame(maxWidth: .infinity, alignment: .trailing)
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(CatppuccinMocha.mantle)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(CatppuccinMocha.surface0, lineWidth: 1)
                    )
            )
    }
}

extension View {
    func displayStyle() -> some View {
        modifier(DisplayModifier())
    }
}

// MARK: - Card Style

struct CardModifier: ViewModifier {
    let isHovered: Bool

    func body(content: Content) -> some View {
        content
            .padding(20)
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(isHovered ? CatppuccinMocha.surface1 : CatppuccinMocha.surface0)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(
                        isHovered ? CatppuccinMocha.mauve.opacity(0.5) : Color.clear,
                        lineWidth: 2
                    )
            )
            .shadow(
                color: isHovered ? CatppuccinMocha.mauve.opacity(0.2) : .clear,
                radius: 10
            )
            .animation(.easeInOut(duration: 0.2), value: isHovered)
    }
}

extension View {
    func cardStyle(isHovered: Bool = false) -> some View {
        modifier(CardModifier(isHovered: isHovered))
    }
}
