package com.vibecoding.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public final class Theme {
    // Base palette (dark)
    public static final Color BG_DARK = new Color(0x1E, 0x1E, 0x2E);
    public static final Color BG_SURFACE = new Color(0x31, 0x32, 0x44);
    public static final Color BG_OVERLAY = new Color(0x45, 0x47, 0x5A);
    public static final Color TEXT_PRIMARY = new Color(0xCD, 0xD6, 0xF4);
    public static final Color TEXT_DIM = new Color(0xA6, 0xAD, 0xC8);
    public static final Color TEXT_SUBTLE = new Color(0x6C, 0x70, 0x86);

    // Accent colors
    public static final Color ACCENT_BLUE = new Color(0x89, 0xB4, 0xFA);
    public static final Color ACCENT_GREEN = new Color(0xA6, 0xE3, 0xA1);
    public static final Color ACCENT_PEACH = new Color(0xFA, 0xB3, 0x87);
    public static final Color ACCENT_MAUVE = new Color(0xCB, 0xA6, 0xF7);
    public static final Color ACCENT_RED = new Color(0xF3, 0x8B, 0xA8);
    public static final Color ACCENT_YELLOW = new Color(0xF9, 0xE2, 0xAF);
    public static final Color ACCENT_TEAL = new Color(0x94, 0xE2, 0xD5);

    // Button categories
    public static final Color BTN_NUMBER = new Color(0x45, 0x47, 0x5A);
    public static final Color BTN_NUMBER_HOVER = new Color(0x58, 0x5B, 0x70);
    public static final Color BTN_OPERATOR = new Color(0x58, 0x5B, 0x70);
    public static final Color BTN_OPERATOR_HOVER = new Color(0x6C, 0x70, 0x86);
    public static final Color BTN_FUNCTION = new Color(0x31, 0x32, 0x44);
    public static final Color BTN_FUNCTION_HOVER = new Color(0x45, 0x47, 0x5A);
    public static final Color BTN_ACCENT = new Color(0x89, 0xB4, 0xFA);
    public static final Color BTN_ACCENT_HOVER = new Color(0xB4, 0xBE, 0xFE);
    public static final Color BTN_CLEAR = new Color(0xF3, 0x8B, 0xA8);
    public static final Color BTN_CLEAR_HOVER = new Color(0xF5, 0xA6, 0xBC);

    // Fonts
    public static final Font DISPLAY_FONT = new Font("Consolas", Font.BOLD, 28);
    public static final Font DISPLAY_SMALL = new Font("Consolas", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BUTTON_FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private Theme() {}

    public static JButton createButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(hoverBg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(bg);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BUTTON_FONT);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(68, 48));
        return btn;
    }

    public static JButton numberButton(String text) {
        return createButton(text, BTN_NUMBER, BTN_NUMBER_HOVER, TEXT_PRIMARY);
    }

    public static JButton operatorButton(String text) {
        return createButton(text, BTN_OPERATOR, BTN_OPERATOR_HOVER, ACCENT_PEACH);
    }

    public static JButton functionButton(String text) {
        return createButton(text, BTN_FUNCTION, BTN_FUNCTION_HOVER, ACCENT_BLUE);
    }

    public static JButton accentButton(String text) {
        return createButton(text, BTN_ACCENT, BTN_ACCENT_HOVER, BG_DARK);
    }

    public static JButton clearButton(String text) {
        return createButton(text, BTN_CLEAR, BTN_CLEAR_HOVER, BG_DARK);
    }

    public static JTextField createDisplay() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setFont(DISPLAY_FONT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_SURFACE);
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setCaretColor(TEXT_PRIMARY);
        return field;
    }

    public static JPanel createDarkPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_DARK);
        return panel;
    }

    public static JPanel createSurfacePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SURFACE);
        return panel;
    }

    public static void styleFrame(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_DARK);
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}
