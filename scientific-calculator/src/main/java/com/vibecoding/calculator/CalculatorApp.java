package com.vibecoding.calculator;

import javax.swing.*;

public class CalculatorApp {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      CalculatorUI ui = new CalculatorUI();
      ui.setVisible(true);
    });
  }
}



