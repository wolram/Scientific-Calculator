package com.vibecoding.calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class CalculatorUI extends JFrame {
  private final JTextField display;
  private final CalculatorEngine engine;
  private String currentInput = "";
  private Double storedValue = null;
  private String pendingOp = null;

  public CalculatorUI() {
    super("Scientific Calculator");
    this.engine = new CalculatorEngine();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(420, 560);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(8, 8));

    display = new JTextField();
    display.setEditable(false);
    display.setHorizontalAlignment(SwingConstants.RIGHT);
    display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
    add(display, BorderLayout.NORTH);

    JPanel buttons = new JPanel(new GridLayout(6, 5, 6, 6));
    add(buttons, BorderLayout.CENTER);

    Map<String, Runnable> actions = new LinkedHashMap<>();

    actions.put("C", this::clearAll);
    actions.put("←", this::backspace);
    actions.put("(", () -> append("("));
    actions.put(")", () -> append(")"));
    actions.put("/", () -> op("/"));

    actions.put("7", () -> append("7"));
    actions.put("8", () -> append("8"));
    actions.put("9", () -> append("9"));
    actions.put("*", () -> op("*"));
    actions.put("sqrt", () -> applyUnary("sqrt"));

    actions.put("4", () -> append("4"));
    actions.put("5", () -> append("5"));
    actions.put("6", () -> append("6"));
    actions.put("-", () -> op("-"));
    actions.put("x^y", () -> op("^"));

    actions.put("1", () -> append("1"));
    actions.put("2", () -> append("2"));
    actions.put("3", () -> append("3"));
    actions.put("+", () -> op("+"));
    actions.put("exp", () -> applyUnary("exp"));

    actions.put("0", () -> append("0"));
    actions.put(".", () -> append("."));
    actions.put("±", this::toggleSign);
    actions.put("=", this::equalsOp);
    actions.put("n!", () -> applyUnary("fact"));

    actions.put("sin", () -> applyUnary("sinr"));
    actions.put("cos", () -> applyUnary("cosr"));
    actions.put("tan", () -> applyUnary("tanr"));
    actions.put("sin°", () -> applyUnary("sind"));
    actions.put("cos°", () -> applyUnary("cosd"));

    actions.put("tan°", () -> applyUnary("tand"));
    actions.put("ln", () -> applyUnary("ln"));
    actions.put("log", () -> applyUnary("log"));
    actions.put("pi", () -> append(String.valueOf(Math.PI)));
    actions.put("e", () -> append(String.valueOf(Math.E)));

    for (Map.Entry<String, Runnable> e : actions.entrySet()) {
      JButton b = new JButton(e.getKey());
      b.setFocusPainted(false);
      b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
      b.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) { e.getValue().run(); }
      });
      buttons.add(b);
    }

    addKeyBindings();
  }

  private void addKeyBindings() {
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c) || c == '.') append(String.valueOf(c));
        if (c == '+') op("+");
        if (c == '-') op("-");
        if (c == '*') op("*");
        if (c == '/') op("/");
        if (c == '^') op("^");
        if (c == '=') equalsOp();
        if (c == '\n') equalsOp();
      }
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) backspace();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) clearAll();
      }
    });
    setFocusable(true);
  }

  private void append(String s) {
    currentInput += s;
    display.setText(currentInput);
  }

  private void backspace() {
    if (!currentInput.isEmpty()) {
      currentInput = currentInput.substring(0, currentInput.length() - 1);
      display.setText(currentInput);
    }
  }

  private void clearAll() {
    currentInput = "";
    storedValue = null;
    pendingOp = null;
    display.setText("");
  }

  private Double parseInputOrNull() {
    try {
      if (currentInput == null || currentInput.isEmpty()) return null;
      return Double.parseDouble(currentInput);
    } catch (Exception ex) {
      showError("Invalid input");
      return null;
    }
  }

  private void op(String op) {
    Double val = parseInputOrNull();
    if (val != null) {
      if (storedValue == null) storedValue = val; else storedValue = applyBinary(storedValue, val, pendingOp);
      currentInput = "";
      display.setText(String.valueOf(storedValue));
    }
    pendingOp = op;
  }

  private void equalsOp() {
    Double val = parseInputOrNull();
    if (val == null && storedValue != null && pendingOp == null) {
      display.setText(String.valueOf(storedValue));
      return;
    }
    if (val != null && storedValue != null && pendingOp != null) {
      Double res = applyBinary(storedValue, val, pendingOp);
      storedValue = res;
      currentInput = "";
      pendingOp = null;
      display.setText(String.valueOf(res));
    }
  }

  private Double applyBinary(Double a, Double b, String op) {
    try {
      if (op == null) return b;
      return switch (op) {
        case "+" -> engine.add(a, b);
        case "-" -> engine.subtract(a, b);
        case "*" -> engine.multiply(a, b);
        case "/" -> engine.divide(a, b);
        case "^" -> engine.pow(a, b);
        default -> b;
      };
    } catch (Exception ex) {
      showError(ex.getMessage());
      return b;
    }
  }

  private void applyUnary(String op) {
    Double val = parseInputOrNull();
    if (val == null) return;
    try {
      double res = switch (op) {
        case "sqrt" -> engine.sqrt(val);
        case "exp" -> engine.exp(val);
        case "ln" -> engine.ln(val);
        case "log" -> engine.log10(val);
        case "sinr" -> engine.sinRad(val);
        case "cosr" -> engine.cosRad(val);
        case "tanr" -> engine.tanRad(val);
        case "sind" -> engine.sinDeg(val);
        case "cosd" -> engine.cosDeg(val);
        case "tand" -> engine.tanDeg(val);
        case "fact" -> engine.factorial((int)Math.round(val));
        default -> val;
      };
      currentInput = String.valueOf(res);
      display.setText(currentInput);
    } catch (Exception ex) {
      showError(ex.getMessage());
    }
  }

  private void toggleSign() {
    if (currentInput.startsWith("-")) currentInput = currentInput.substring(1);
    else if (!currentInput.isEmpty()) currentInput = "-" + currentInput;
    display.setText(currentInput);
  }

  private void showError(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }
}



