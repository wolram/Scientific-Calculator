package com.vibecoding.calculator.ui;

import com.vibecoding.calculator.engine.FinancialEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FinancialCalculatorUI extends JFrame {
    private final FinancialEngine engine = new FinancialEngine();
    private final JFrame parent;

    // TVM registers
    private final JTextField displayMain;
    private final JTextField fieldN, fieldI, fieldPV, fieldPMT, fieldFV;
    private final JCheckBox beginMode;
    private final JTextArea outputArea;

    // Current input
    private StringBuilder currentInput = new StringBuilder();
    private String activeRegister = null;

    // Cash flows
    private final java.util.List<Double> cashFlows = new java.util.ArrayList<>();

    public FinancialCalculatorUI(JFrame parent) {
        super("Calculadora Financeira HP 12C");
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Theme.styleFrame(this, "Calculadora Financeira HP 12C", 560, 780);
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

        JLabel titleLabel = Theme.createLabel("HP 12C", new Font("Consolas", Font.BOLD, 16), Theme.ACCENT_GREEN);
        topBar.add(titleLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = Theme.createDarkPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(4, 12, 8, 12));

        // Display
        displayMain = Theme.createDisplay();
        displayMain.setText("0.00");
        displayMain.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JPanel displayWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        displayWrapper.setOpaque(false);
        displayWrapper.setLayout(new BorderLayout());
        displayWrapper.setBorder(new EmptyBorder(4, 0, 4, 0));
        displayWrapper.add(displayMain, BorderLayout.CENTER);
        displayWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        mainPanel.add(displayWrapper);
        mainPanel.add(Box.createVerticalStrut(8));

        // TVM Register Fields
        JPanel tvmPanel = new JPanel(new GridLayout(1, 5, 6, 0));
        tvmPanel.setOpaque(false);
        tvmPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        fieldN = createRegField("N", "Períodos");
        fieldI = createRegField("i%", "Taxa (%)");
        fieldPV = createRegField("PV", "Valor Presente");
        fieldPMT = createRegField("PMT", "Pagamento");
        fieldFV = createRegField("FV", "Valor Futuro");

        tvmPanel.add(wrapRegField("N", fieldN));
        tvmPanel.add(wrapRegField("i%", fieldI));
        tvmPanel.add(wrapRegField("PV", fieldPV));
        tvmPanel.add(wrapRegField("PMT", fieldPMT));
        tvmPanel.add(wrapRegField("FV", fieldFV));
        mainPanel.add(tvmPanel);
        mainPanel.add(Box.createVerticalStrut(4));

        // Begin/End mode
        beginMode = new JCheckBox("BEGIN (início do período)");
        beginMode.setFont(Theme.LABEL_FONT);
        beginMode.setForeground(Theme.TEXT_DIM);
        beginMode.setBackground(Theme.BG_DARK);
        beginMode.setFocusPainted(false);
        beginMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        mainPanel.add(beginMode);
        mainPanel.add(Box.createVerticalStrut(8));

        // Buttons
        JPanel btnPanel = Theme.createDarkPanel();
        btnPanel.setLayout(new GridLayout(7, 5, 4, 4));
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Row 1: TVM keys
        addCalcBtn(btnPanel, "N", Theme.ACCENT_GREEN, () -> solveTVM("N"));
        addCalcBtn(btnPanel, "i%", Theme.ACCENT_GREEN, () -> solveTVM("I"));
        addCalcBtn(btnPanel, "PV", Theme.ACCENT_GREEN, () -> solveTVM("PV"));
        addCalcBtn(btnPanel, "PMT", Theme.ACCENT_GREEN, () -> solveTVM("PMT"));
        addCalcBtn(btnPanel, "FV", Theme.ACCENT_GREEN, () -> solveTVM("FV"));

        // Row 2: Cash flow / NPV / IRR
        addCalcBtn(btnPanel, "CF\u2080", Theme.ACCENT_PEACH, () -> addCashFlow());
        addCalcBtn(btnPanel, "CFj", Theme.ACCENT_PEACH, () -> addCashFlow());
        addCalcBtn(btnPanel, "NPV", Theme.ACCENT_PEACH, () -> calcNPV());
        addCalcBtn(btnPanel, "IRR", Theme.ACCENT_PEACH, () -> calcIRR());
        addCalcBtn(btnPanel, "CLR CF", Theme.ACCENT_RED, () -> { cashFlows.clear(); appendOutput("Cash flows limpos"); });

        // Row 3: Amort / Depreciation
        addCalcBtn(btnPanel, "AMORT", Theme.ACCENT_BLUE, () -> calcAmort());
        addCalcBtn(btnPanel, "SL", Theme.ACCENT_BLUE, () -> calcDeprSL());
        addCalcBtn(btnPanel, "DB", Theme.ACCENT_BLUE, () -> calcDeprDB());
        addCalcBtn(btnPanel, "SYD", Theme.ACCENT_BLUE, () -> calcDeprSYD());
        addCalcBtn(btnPanel, "BOND", Theme.ACCENT_BLUE, () -> calcBond());

        // Row 4: Percentage
        addCalcBtn(btnPanel, "%", Theme.ACCENT_MAUVE, () -> calcPercent());
        addCalcBtn(btnPanel, "\u0394%", Theme.ACCENT_MAUVE, () -> calcPercentChange());
        addCalcBtn(btnPanel, "%T", Theme.ACCENT_MAUVE, () -> calcPercentTotal());
        addCalcBtn(btnPanel, "MRKUP", Theme.ACCENT_MAUVE, () -> calcMarkup());
        addCalcBtn(btnPanel, "MARGN", Theme.ACCENT_MAUVE, () -> calcMargin());

        // Row 5: Number row 7-9
        addNumBtn(btnPanel, "7");
        addNumBtn(btnPanel, "8");
        addNumBtn(btnPanel, "9");
        addOpBtn(btnPanel, "\u00F7", "/");
        addCalcBtn(btnPanel, "CLR", Theme.ACCENT_RED, () -> clearAll());

        // Row 6: 4-6
        addNumBtn(btnPanel, "4");
        addNumBtn(btnPanel, "5");
        addNumBtn(btnPanel, "6");
        addOpBtn(btnPanel, "\u00D7", "*");
        addCalcBtn(btnPanel, "\u2190", Theme.ACCENT_RED, () -> backspace());

        // Row 7: 1-3
        addNumBtn(btnPanel, "1");
        addNumBtn(btnPanel, "2");
        addNumBtn(btnPanel, "3");
        addOpBtn(btnPanel, "\u2212", "-");
        addOpBtn(btnPanel, "+", "+");

        mainPanel.add(btnPanel);
        mainPanel.add(Box.createVerticalStrut(4));

        // Bottom row: 0, ., ±, CHS, ENTER
        JPanel bottomRow = Theme.createDarkPanel();
        bottomRow.setLayout(new GridLayout(1, 5, 4, 4));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        addNumBtn(bottomRow, "0");
        addNumBtn(bottomRow, ".");
        addCalcBtn(bottomRow, "\u00B1", Theme.TEXT_PRIMARY, () -> toggleSign());
        addCalcBtn(bottomRow, "CHS", Theme.ACCENT_YELLOW, () -> toggleSign());
        addCalcBtn(bottomRow, "ENTER", Theme.ACCENT_GREEN, () -> storeToRegister());
        mainPanel.add(bottomRow);
        mainPanel.add(Box.createVerticalStrut(8));

        // Output area
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        outputArea.setForeground(Theme.TEXT_DIM);
        outputArea.setBackground(Theme.BG_SURFACE);
        outputArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        mainPanel.add(scroll);

        add(mainPanel, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || c == '.') appendDigit(String.valueOf(c));
                else if (c == '\n') storeToRegister();
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) backspace();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) clearAll();
            }
        });
    }

    private JTextField createRegField(String name, String tooltip) {
        JTextField f = new JTextField("0");
        f.setHorizontalAlignment(SwingConstants.RIGHT);
        f.setFont(new Font("Consolas", Font.BOLD, 13));
        f.setForeground(Theme.TEXT_PRIMARY);
        f.setBackground(Theme.BG_SURFACE);
        f.setBorder(new EmptyBorder(4, 4, 4, 6));
        f.setToolTipText(tooltip);
        f.setEditable(false);
        f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeRegister = name;
                highlightRegister(name);
            }
        });
        return f;
    }

    private JPanel wrapRegField(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BorderLayout(0, 2));
        JLabel l = Theme.createLabel(label, new Font("Consolas", Font.BOLD, 11), Theme.ACCENT_GREEN);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void highlightRegister(String reg) {
        Color normal = Theme.BG_SURFACE;
        Color active = Theme.BG_OVERLAY;
        fieldN.setBackground(reg.equals("N") ? active : normal);
        fieldI.setBackground(reg.equals("i%") ? active : normal);
        fieldPV.setBackground(reg.equals("PV") ? active : normal);
        fieldPMT.setBackground(reg.equals("PMT") ? active : normal);
        fieldFV.setBackground(reg.equals("FV") ? active : normal);
    }

    private void addCalcBtn(JPanel panel, String text, Color accent, Runnable action) {
        JButton btn = Theme.createButton(text, Theme.BTN_FUNCTION, Theme.BTN_FUNCTION_HOVER, accent);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    private void addNumBtn(JPanel panel, String digit) {
        JButton btn = Theme.numberButton(digit);
        btn.addActionListener(e -> appendDigit(digit));
        panel.add(btn);
    }

    private void addOpBtn(JPanel panel, String label, String op) {
        JButton btn = Theme.operatorButton(label);
        btn.addActionListener(e -> {
            try {
                // Simple inline calc: display = display op next
                double val = getDisplayValue();
                String expr = displayMain.getText() + op;
                currentInput.setLength(0);
                displayMain.setText(String.format("%.2f %s ", val, label));
            } catch (Exception ignored) {}
        });
        panel.add(btn);
    }

    private void appendDigit(String d) {
        currentInput.append(d);
        displayMain.setText(currentInput.toString());
    }

    private void backspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            displayMain.setText(currentInput.length() > 0 ? currentInput.toString() : "0.00");
        }
    }

    private void clearAll() {
        currentInput.setLength(0);
        displayMain.setText("0.00");
        fieldN.setText("0");
        fieldI.setText("0");
        fieldPV.setText("0");
        fieldPMT.setText("0");
        fieldFV.setText("0");
        activeRegister = null;
        highlightRegister("");
        outputArea.setText("");
    }

    private void toggleSign() {
        if (currentInput.length() > 0 && currentInput.charAt(0) == '-') {
            currentInput.deleteCharAt(0);
        } else {
            currentInput.insert(0, '-');
        }
        displayMain.setText(currentInput.toString());
    }

    private double getDisplayValue() {
        try {
            return Double.parseDouble(currentInput.length() > 0 ? currentInput.toString() : displayMain.getText().replaceAll("[^\\d.\\-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private double getRegValue(JTextField field) {
        try {
            return Double.parseDouble(field.getText().replaceAll("[^\\d.\\-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void storeToRegister() {
        double val = getDisplayValue();
        if (activeRegister != null) {
            switch (activeRegister) {
                case "N" -> fieldN.setText(String.valueOf(val));
                case "i%" -> fieldI.setText(String.valueOf(val));
                case "PV" -> fieldPV.setText(String.valueOf(val));
                case "PMT" -> fieldPMT.setText(String.valueOf(val));
                case "FV" -> fieldFV.setText(String.valueOf(val));
            }
            currentInput.setLength(0);
            displayMain.setText(engine.format(val));
        }
    }

    private void solveTVM(String solve) {
        try {
            double n = getRegValue(fieldN);
            double i = getRegValue(fieldI);
            double pv = getRegValue(fieldPV);
            double pmt = getRegValue(fieldPMT);
            double fv = getRegValue(fieldFV);
            boolean begin = beginMode.isSelected();

            double result = switch (solve) {
                case "N" -> engine.periods(i, pv, pmt, fv, begin);
                case "I" -> engine.interestRate(n, pv, pmt, fv, begin);
                case "PV" -> engine.presentValue(n, i, pmt, fv, begin);
                case "PMT" -> engine.payment(n, i, pv, fv, begin);
                case "FV" -> engine.futureValue(n, i, pv, pmt, begin);
                default -> 0;
            };

            String formatted = engine.format(result);
            displayMain.setText(formatted);
            appendOutput(solve + " = " + formatted);

            switch (solve) {
                case "N" -> fieldN.setText(formatted);
                case "I" -> fieldI.setText(formatted);
                case "PV" -> fieldPV.setText(formatted);
                case "PMT" -> fieldPMT.setText(formatted);
                case "FV" -> fieldFV.setText(formatted);
            }
        } catch (Exception ex) {
            appendOutput("Erro TVM: " + ex.getMessage());
        }
    }

    private void addCashFlow() {
        double val = getDisplayValue();
        cashFlows.add(val);
        currentInput.setLength(0);
        appendOutput("CF[" + (cashFlows.size() - 1) + "] = " + engine.format(val));
        displayMain.setText("0.00");
    }

    private void calcNPV() {
        if (cashFlows.isEmpty()) { appendOutput("Sem cash flows"); return; }
        double rate = getRegValue(fieldI);
        double[] cfs = cashFlows.stream().mapToDouble(Double::doubleValue).toArray();
        double result = engine.npv(rate, cfs);
        displayMain.setText(engine.format(result));
        appendOutput("NPV (i=" + engine.formatPercent(rate) + ") = " + engine.format(result));
    }

    private void calcIRR() {
        if (cashFlows.size() < 2) { appendOutput("Pelo menos 2 cash flows"); return; }
        double[] cfs = cashFlows.stream().mapToDouble(Double::doubleValue).toArray();
        double result = engine.irr(cfs);
        displayMain.setText(engine.formatPercent(result));
        fieldI.setText(String.format("%.4f", result));
        appendOutput("IRR = " + engine.formatPercent(result));
    }

    private void calcAmort() {
        try {
            double principal = Math.abs(getRegValue(fieldPV));
            double rate = getRegValue(fieldI);
            int periods = (int) getRegValue(fieldN);
            if (principal == 0 || periods == 0) { appendOutput("PV e N necessários"); return; }

            List<FinancialEngine.AmortRow> schedule = engine.amortizationSchedule(principal, rate, periods);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s %12s %12s %12s %12s%n", "Per", "Pagamento", "Juros", "Principal", "Saldo"));
            sb.append("-".repeat(55)).append("\n");
            double totalInterest = 0, totalPrincipal = 0;
            for (FinancialEngine.AmortRow row : schedule) {
                sb.append(String.format("%-5d %12s %12s %12s %12s%n",
                        row.period, engine.format(row.payment), engine.format(row.interest),
                        engine.format(row.principal), engine.format(row.balance)));
                totalInterest += row.interest;
                totalPrincipal += row.principal;
            }
            sb.append("-".repeat(55)).append("\n");
            sb.append(String.format("TOTAL %12s %12s %12s%n", "", engine.format(totalInterest), engine.format(totalPrincipal)));
            outputArea.setText(sb.toString());
            outputArea.setCaretPosition(0);
        } catch (Exception ex) {
            appendOutput("Erro: " + ex.getMessage());
        }
    }

    private void calcDeprSL() {
        try {
            double cost = Math.abs(getRegValue(fieldPV));
            double salvage = Math.abs(getRegValue(fieldFV));
            double life = getRegValue(fieldN);
            double dep = engine.depreciationSL(cost, salvage, life);
            displayMain.setText(engine.format(dep));
            appendOutput("Depreciação Linear = " + engine.format(dep) + " /período");
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcDeprDB() {
        try {
            double cost = Math.abs(getRegValue(fieldPV));
            double salvage = Math.abs(getRegValue(fieldFV));
            double life = getRegValue(fieldN);
            int year = (int) getDisplayValue();
            if (year <= 0) year = 1;
            double dep = engine.depreciationDB(cost, salvage, life, year, 2);
            displayMain.setText(engine.format(dep));
            appendOutput("Depreciação DB (ano " + year + ") = " + engine.format(dep));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcDeprSYD() {
        try {
            double cost = Math.abs(getRegValue(fieldPV));
            double salvage = Math.abs(getRegValue(fieldFV));
            double life = getRegValue(fieldN);
            int year = (int) getDisplayValue();
            if (year <= 0) year = 1;
            double dep = engine.depreciationSYD(cost, salvage, life, year);
            displayMain.setText(engine.format(dep));
            appendOutput("Depreciação SYD (ano " + year + ") = " + engine.format(dep));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcBond() {
        try {
            double face = Math.abs(getRegValue(fieldFV));
            if (face == 0) face = 1000;
            double coupon = getRegValue(fieldI);
            double yield = getDisplayValue();
            int periods = (int) getRegValue(fieldN);
            double price = engine.bondPrice(face, coupon, yield, periods);
            displayMain.setText(engine.format(price));
            appendOutput("Preço Bond = " + engine.format(price));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcPercent() {
        try {
            double base = getRegValue(fieldPV);
            double pct = getDisplayValue();
            double result = engine.percentOf(base, pct);
            displayMain.setText(engine.format(result));
            appendOutput(engine.format(pct) + "% de " + engine.format(base) + " = " + engine.format(result));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcPercentChange() {
        try {
            double oldVal = getRegValue(fieldPV);
            double newVal = getDisplayValue();
            double result = engine.percentChange(oldVal, newVal);
            displayMain.setText(engine.formatPercent(result));
            appendOutput("\u0394% = " + engine.formatPercent(result));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcPercentTotal() {
        try {
            double part = getDisplayValue();
            double total = getRegValue(fieldPV);
            double result = engine.percentTotal(part, total);
            displayMain.setText(engine.formatPercent(result));
            appendOutput("%T = " + engine.formatPercent(result));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcMarkup() {
        try {
            double cost = getRegValue(fieldPV);
            double price = getDisplayValue();
            double result = engine.markup(cost, price);
            displayMain.setText(engine.formatPercent(result));
            appendOutput("Markup = " + engine.formatPercent(result));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void calcMargin() {
        try {
            double cost = getRegValue(fieldPV);
            double price = getDisplayValue();
            double result = engine.margin(cost, price);
            displayMain.setText(engine.formatPercent(result));
            appendOutput("Margem = " + engine.formatPercent(result));
        } catch (Exception ex) { appendOutput("Erro: " + ex.getMessage()); }
    }

    private void appendOutput(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}
