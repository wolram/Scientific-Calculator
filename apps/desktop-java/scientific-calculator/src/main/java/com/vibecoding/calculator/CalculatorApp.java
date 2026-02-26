package com.vibecoding.calculator;

import com.vibecoding.calculator.ui.MainMenuUI;

import javax.swing.*;

public class CalculatorApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainMenuUI menu = new MainMenuUI();
            menu.setVisible(true);
        });
    }
}
