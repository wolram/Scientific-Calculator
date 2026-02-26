package com.vibecoding.calculator.ui;

import com.vibecoding.calculator.engine.ScientificEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScientificCalculatorUI extends JFrame {
    private final JTextField displayExpr;
    private final JTextField displayResult;
    private final JLabel modeLabel;
    private final JLabel memLabel;
    private final ScientificEngine engine = new ScientificEngine();

    private final JFrame parent;

    private StringBuilder expression = new StringBuilder();
    private String lastResult = "";
    private boolean useDegrees = true;
    private boolean secondFunction = false;
    private double memory = 0;
    private boolean hasMemory = false;

    public ScientificCalculatorUI(JFrame parent) {
        super("Calculadora Científica");
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Theme.styleFrame(this, "Calculadora Científica", 480, 680);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Theme.BG_DARK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (parent != null) parent.setVisible(true);
            }
        });

        // Top bar
        JPanel topBar = Theme.createDarkPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(8, 12, 4, 12));

        JButton backBtn = Theme.functionButton("\u2190 Menu");
        backBtn.setPreferredSize(new Dimension(80, 30));
        backBtn.addActionListener(e -> { dispose(); if (parent != null) parent.setVisible(true); });
        topBar.add(backBtn, BorderLayout.WEST);

        modeLabel = Theme.createLabel("DEG", Theme.LABEL_FONT, Theme.ACCENT_YELLOW);
        memLabel = Theme.createLabel("", Theme.LABEL_FONT, Theme.ACCENT_GREEN);

        JPanel indicators = Theme.createDarkPanel();
        indicators.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        indicators.add(memLabel);
        indicators.add(modeLabel);
        topBar.add(indicators, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Display area
        JPanel displayPanel = Theme.createDarkPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBorder(new EmptyBorder(4, 12, 8, 12));

        displayExpr = new JTextField();
        displayExpr.setEditable(false);
        displayExpr.setHorizontalAlignment(SwingConstants.RIGHT);
        displayExpr.setFont(Theme.DISPLAY_SMALL);
        displayExpr.setForeground(Theme.TEXT_DIM);
        displayExpr.setBackground(Theme.BG_SURFACE);
        displayExpr.setBorder(new EmptyBorder(6, 12, 2, 12));
        displayExpr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        displayResult = Theme.createDisplay();
        displayResult.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JPanel displayContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        displayContainer.setOpaque(false);
        displayContainer.setLayout(new BoxLayout(displayContainer, BoxLayout.Y_AXIS));
        displayContainer.setBorder(new EmptyBorder(4, 0, 4, 0));
        displayContainer.add(displayExpr);
        displayContainer.add(displayResult);

        displayPanel.add(displayContainer);
        add(displayPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsPanel = Theme.createDarkPanel();
        buttonsPanel.setLayout(new GridLayout(10, 5, 4, 4));
        buttonsPanel.setBorder(new EmptyBorder(4, 8, 12, 8));

        // Row 1: 2nd, DEG/RAD, MC, MR, M+
        addBtn(buttonsPanel, "2nd", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_MAUVE, () -> toggle2nd());
        addBtn(buttonsPanel, "DEG", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_YELLOW, () -> toggleAngleMode());
        addBtn(buttonsPanel, "MC", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> { memory = 0; hasMemory = false; memLabel.setText(""); });
        addBtn(buttonsPanel, "MR", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> { if (hasMemory) appendToExpr(engine.format(memory)); });
        addBtn(buttonsPanel, "M+", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> {
            try { memory += Double.parseDouble(getCurrentValue()); hasMemory = true; memLabel.setText("M"); } catch (Exception ignored) {}
        });

        // Row 2: x², x³, xⁿ, eˣ, 10ˣ
        addBtn(buttonsPanel, "x\u00B2", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("square"));
        addBtn(buttonsPanel, "x\u00B3", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("cube"));
        addBtn(buttonsPanel, "x\u02B8", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> appendToExpr("^"));
        addBtn(buttonsPanel, "e\u02E3", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("exp"));
        addBtn(buttonsPanel, "10\u02E3", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("10pow"));

        // Row 3: 1/x, √x, ³√x, ln, log
        addBtn(buttonsPanel, "1/x", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("recip"));
        addBtn(buttonsPanel, "\u221Ax", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("sqrt"));
        addBtn(buttonsPanel, "\u00B3\u221Ax", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("cbrt"));
        addBtn(buttonsPanel, "ln", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("ln"));
        addBtn(buttonsPanel, "log", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("log"));

        // Row 4: sin, cos, tan, n!, |x|
        addBtn(buttonsPanel, "sin", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyTrig("sin"));
        addBtn(buttonsPanel, "cos", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyTrig("cos"));
        addBtn(buttonsPanel, "tan", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyTrig("tan"));
        addBtn(buttonsPanel, "n!", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("fact"));
        addBtn(buttonsPanel, "|x|", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> applyFunc("abs"));

        // Row 5: sinh/asin, cosh/acos, tanh/atan, nPr, nCr
        addBtn(buttonsPanel, "sinh", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyHyp("sinh"));
        addBtn(buttonsPanel, "cosh", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyHyp("cosh"));
        addBtn(buttonsPanel, "tanh", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_TEAL, () -> applyHyp("tanh"));
        addBtn(buttonsPanel, "nPr", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> appendToExpr("P"));
        addBtn(buttonsPanel, "nCr", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_BLUE, () -> appendToExpr("C"));

        // Row 6: (, ), π, e, φ
        addBtn(buttonsPanel, "(", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("("));
        addBtn(buttonsPanel, ")", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr(")"));
        addBtn(buttonsPanel, "\u03C0", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_MAUVE, () -> appendToExpr(String.valueOf(Math.PI)));
        addBtn(buttonsPanel, "e", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_MAUVE, () -> appendToExpr(String.valueOf(Math.E)));
        addBtn(buttonsPanel, "\u03C6", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.ACCENT_MAUVE, () -> appendToExpr(String.valueOf(engine.phi())));

        // Row 7: 7, 8, 9, ÷, ←
        addBtn(buttonsPanel, "7", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("7"));
        addBtn(buttonsPanel, "8", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("8"));
        addBtn(buttonsPanel, "9", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("9"));
        addBtn(buttonsPanel, "\u00F7", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.ACCENT_PEACH, () -> appendToExpr("/"));
        addBtn(buttonsPanel, "\u2190", Theme.BTN_CLEAR, Theme.BTN_CLEAR_HOVER, Theme.BG_DARK, () -> backspace());

        // Row 8: 4, 5, 6, ×, C
        addBtn(buttonsPanel, "4", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("4"));
        addBtn(buttonsPanel, "5", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("5"));
        addBtn(buttonsPanel, "6", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("6"));
        addBtn(buttonsPanel, "\u00D7", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.ACCENT_PEACH, () -> appendToExpr("*"));
        addBtn(buttonsPanel, "C", Theme.BTN_CLEAR, Theme.BTN_CLEAR_HOVER, Theme.BG_DARK, () -> clearAll());

        // Row 9: 1, 2, 3, -, =  |  0, ., ±, +, =
        addBtn(buttonsPanel, "1", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("1"));
        addBtn(buttonsPanel, "2", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("2"));
        addBtn(buttonsPanel, "3", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("3"));
        addBtn(buttonsPanel, "\u2212", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.ACCENT_PEACH, () -> appendToExpr("-"));
        addBtn(buttonsPanel, "+", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.ACCENT_PEACH, () -> appendToExpr("+"));

        // Row 10: 0, ., ±, %, =
        addBtn(buttonsPanel, "0", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("0"));
        addBtn(buttonsPanel, ".", Theme.BTN_NUMBER, Theme.BTN_NUMBER_HOVER, Theme.TEXT_PRIMARY, () -> appendToExpr("."));
        addBtn(buttonsPanel, "\u00B1", Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, Theme.TEXT_PRIMARY, () -> toggleSign());
        addBtn(buttonsPanel, "%", Theme.BTN_OPERATOR, Theme.BTN_OPERATOR_HOVER, Theme.ACCENT_PEACH, () -> applyFunc("percent"));
        addBtn(buttonsPanel, "=", Theme.BTN_ACCENT, Theme.BTN_ACCENT_HOVER, Theme.BG_DARK, () -> evaluate());

        add(buttonsPanel, BorderLayout.SOUTH);

        // Keyboard
        addKeyBindings();
    }

    private void addBtn(JPanel panel, String text, Color bg, Color hover, Color fg, Runnable action) {
        JButton btn = Theme.createButton(text, bg, hover, fg);
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    private void appendToExpr(String s) {
        expression.append(s);
        displayExpr.setText(expression.toString());
        displayResult.setText("");
    }

    private void backspace() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            displayExpr.setText(expression.toString());
        }
    }

    private void clearAll() {
        expression.setLength(0);
        lastResult = "";
        displayExpr.setText("");
        displayResult.setText("0");
    }

    private String getCurrentValue() {
        if (!lastResult.isEmpty()) return lastResult;
        String expr = expression.toString().trim();
        if (expr.isEmpty()) return "0";
        return expr;
    }

    private void applyFunc(String func) {
        try {
            double val = Double.parseDouble(getCurrentValue());
            double result = switch (func) {
                case "square" -> engine.square(val);
                case "cube" -> engine.cube(val);
                case "exp" -> engine.exp(val);
                case "10pow" -> engine.tenPow(val);
                case "recip" -> engine.reciprocal(val);
                case "sqrt" -> engine.sqrt(val);
                case "cbrt" -> engine.cbrt(val);
                case "ln" -> engine.ln(val);
                case "log" -> engine.log10(val);
                case "fact" -> engine.factorial((int) Math.round(val));
                case "abs" -> engine.abs(val);
                case "percent" -> val / 100.0;
                default -> val;
            };
            String formatted = engine.format(result);
            lastResult = formatted;
            displayExpr.setText(func + "(" + engine.format(val) + ")");
            displayResult.setText(formatted);
            expression.setLength(0);
            expression.append(formatted);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void applyTrig(String func) {
        try {
            double val = Double.parseDouble(getCurrentValue());
            double result;
            if (secondFunction) {
                // Inverse trig
                result = switch (func) {
                    case "sin" -> useDegrees ? engine.asinDeg(val) : engine.asin(val);
                    case "cos" -> useDegrees ? engine.acosDeg(val) : engine.acos(val);
                    case "tan" -> useDegrees ? engine.atanDeg(val) : engine.atan(val);
                    default -> val;
                };
                displayExpr.setText("a" + func + (useDegrees ? "\u00B0" : "") + "(" + engine.format(val) + ")");
            } else {
                result = switch (func) {
                    case "sin" -> useDegrees ? engine.sinDeg(val) : engine.sin(val);
                    case "cos" -> useDegrees ? engine.cosDeg(val) : engine.cos(val);
                    case "tan" -> useDegrees ? engine.tanDeg(val) : engine.tan(val);
                    default -> val;
                };
                displayExpr.setText(func + (useDegrees ? "\u00B0" : "") + "(" + engine.format(val) + ")");
            }
            String formatted = engine.format(result);
            lastResult = formatted;
            displayResult.setText(formatted);
            expression.setLength(0);
            expression.append(formatted);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void applyHyp(String func) {
        try {
            double val = Double.parseDouble(getCurrentValue());
            double result;
            if (secondFunction) {
                result = switch (func) {
                    case "sinh" -> engine.asinh(val);
                    case "cosh" -> engine.acosh(val);
                    case "tanh" -> engine.atanh(val);
                    default -> val;
                };
                displayExpr.setText("a" + func + "(" + engine.format(val) + ")");
            } else {
                result = switch (func) {
                    case "sinh" -> engine.sinh(val);
                    case "cosh" -> engine.cosh(val);
                    case "tanh" -> engine.tanh(val);
                    default -> val;
                };
                displayExpr.setText(func + "(" + engine.format(val) + ")");
            }
            String formatted = engine.format(result);
            lastResult = formatted;
            displayResult.setText(formatted);
            expression.setLength(0);
            expression.append(formatted);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void toggleSign() {
        String expr = expression.toString();
        if (expr.startsWith("-")) {
            expression.deleteCharAt(0);
        } else if (!expr.isEmpty()) {
            expression.insert(0, "-");
        }
        displayExpr.setText(expression.toString());
    }

    private void toggle2nd() {
        secondFunction = !secondFunction;
        // Visual feedback would go here
    }

    private void toggleAngleMode() {
        useDegrees = !useDegrees;
        modeLabel.setText(useDegrees ? "DEG" : "RAD");
    }

    private void evaluate() {
        String expr = expression.toString().trim();
        if (expr.isEmpty()) return;

        try {
            // Handle nPr and nCr
            if (expr.contains("P") && !expr.contains("(")) {
                String[] parts = expr.split("P");
                if (parts.length == 2) {
                    int n = (int) Double.parseDouble(parts[0].trim());
                    int r = (int) Double.parseDouble(parts[1].trim());
                    double result = engine.permutation(n, r);
                    showResult(expr, engine.format(result));
                    return;
                }
            }
            if (expr.contains("C") && !expr.contains("(")) {
                String[] parts = expr.split("C");
                if (parts.length == 2) {
                    int n = (int) Double.parseDouble(parts[0].trim());
                    int r = (int) Double.parseDouble(parts[1].trim());
                    double result = engine.combination(n, r);
                    showResult(expr, engine.format(result));
                    return;
                }
            }

            // Use expression parser
            com.vibecoding.calculator.parser.ExpressionParser parser =
                    new com.vibecoding.calculator.parser.ExpressionParser(expr, useDegrees);
            double result = parser.parse();
            showResult(expr, engine.format(result));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showResult(String expr, String result) {
        displayExpr.setText(expr + " =");
        displayResult.setText(result);
        lastResult = result;
        expression.setLength(0);
        expression.append(result);
    }

    private void showError(String msg) {
        displayResult.setText("Erro");
        displayResult.setForeground(Theme.ACCENT_RED);
        Timer timer = new Timer(1500, e -> displayResult.setForeground(Theme.TEXT_PRIMARY));
        timer.setRepeats(false);
        timer.start();
    }

    private void addKeyBindings() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || c == '.') appendToExpr(String.valueOf(c));
                else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') appendToExpr(String.valueOf(c));
                else if (c == '(' || c == ')') appendToExpr(String.valueOf(c));
                else if (c == '=' || c == '\n') evaluate();
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) backspace();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) clearAll();
                if (e.getKeyCode() == KeyEvent.VK_DELETE) clearAll();
            }
        });
        setFocusable(true);
    }
}
