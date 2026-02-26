package com.vibecoding.calculator.ui;

import com.vibecoding.calculator.graph.GraphPanel;
import com.vibecoding.calculator.parser.ExpressionParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GraphingCalculatorUI extends JFrame {
    private final JFrame parent;
    private final GraphPanel graphPanel;
    private final JPanel functionListPanel;
    private final List<FunctionRow> functionRows = new ArrayList<>();
    private final JTextField evalField;
    private final JLabel evalResult;
    private boolean useDegrees = false;

    private static final int MAX_FUNCTIONS = 6;

    public GraphingCalculatorUI(JFrame parent) {
        super("Calculadora Gráfica HP 50G");
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Theme.styleFrame(this, "Calculadora Gráfica HP 50G", 820, 700);
        setMinimumSize(new Dimension(700, 550));
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

        JLabel titleLabel = Theme.createLabel("HP 50G", new Font("Consolas", Font.BOLD, 16), Theme.ACCENT_MAUVE);
        topBar.add(titleLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Main split: graph (left) + controls (right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);
        splitPane.setDividerLocation(520);
        splitPane.setBackground(Theme.BG_DARK);

        // Graph panel
        graphPanel = new GraphPanel();
        JPanel graphWrapper = new JPanel(new BorderLayout());
        graphWrapper.setBackground(Theme.BG_DARK);
        graphWrapper.setBorder(new EmptyBorder(8, 12, 12, 4));
        graphWrapper.add(graphPanel, BorderLayout.CENTER);
        splitPane.setLeftComponent(graphWrapper);

        // Right panel: controls
        JPanel rightPanel = Theme.createDarkPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(8, 4, 12, 12));

        // Functions section
        JLabel funcTitle = Theme.createLabel("Funções f(x)", new Font("Segoe UI", Font.BOLD, 14), Theme.TEXT_PRIMARY);
        funcTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(funcTitle);
        rightPanel.add(Box.createVerticalStrut(8));

        functionListPanel = new JPanel();
        functionListPanel.setOpaque(false);
        functionListPanel.setLayout(new BoxLayout(functionListPanel, BoxLayout.Y_AXIS));
        functionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane funcScroll = new JScrollPane(functionListPanel);
        funcScroll.setBorder(null);
        funcScroll.setOpaque(false);
        funcScroll.getViewport().setOpaque(false);
        funcScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        funcScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        funcScroll.setPreferredSize(new Dimension(260, 250));
        rightPanel.add(funcScroll);
        rightPanel.add(Box.createVerticalStrut(8));

        // Add function button
        JButton addFuncBtn = Theme.accentButton("+ Adicionar Função");
        addFuncBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addFuncBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFuncBtn.addActionListener(e -> addFunctionRow());
        rightPanel.add(addFuncBtn);
        rightPanel.add(Box.createVerticalStrut(16));

        // Controls
        JLabel ctrlTitle = Theme.createLabel("Controles", new Font("Segoe UI", Font.BOLD, 14), Theme.TEXT_PRIMARY);
        ctrlTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(ctrlTitle);
        rightPanel.add(Box.createVerticalStrut(8));

        // Zoom + Reset buttons
        JPanel zoomPanel = Theme.createDarkPanel();
        zoomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        zoomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        zoomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton zoomIn = Theme.functionButton("Zoom +");
        zoomIn.addActionListener(e -> graphPanel.zoomIn());
        JButton zoomOut = Theme.functionButton("Zoom -");
        zoomOut.addActionListener(e -> graphPanel.zoomOut());
        JButton resetView = Theme.functionButton("Reset");
        resetView.addActionListener(e -> graphPanel.resetView());
        JButton clearAll = Theme.clearButton("Limpar");
        clearAll.addActionListener(e -> clearAllFunctions());

        zoomPanel.add(zoomIn);
        zoomPanel.add(zoomOut);
        zoomPanel.add(resetView);
        zoomPanel.add(clearAll);
        rightPanel.add(zoomPanel);
        rightPanel.add(Box.createVerticalStrut(8));

        // Toggles
        JPanel togglePanel = Theme.createDarkPanel();
        togglePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        togglePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        togglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JCheckBox gridCheck = new JCheckBox("Grade", true);
        styleCheck(gridCheck);
        gridCheck.addActionListener(e -> graphPanel.setShowGrid(gridCheck.isSelected()));

        JCheckBox axesCheck = new JCheckBox("Eixos", true);
        styleCheck(axesCheck);
        axesCheck.addActionListener(e -> graphPanel.setShowAxes(axesCheck.isSelected()));

        JCheckBox degCheck = new JCheckBox("Graus", false);
        styleCheck(degCheck);
        degCheck.addActionListener(e -> {
            useDegrees = degCheck.isSelected();
            graphPanel.setUseDegrees(useDegrees);
            graphPanel.repaint();
        });

        togglePanel.add(gridCheck);
        togglePanel.add(axesCheck);
        togglePanel.add(degCheck);
        rightPanel.add(togglePanel);
        rightPanel.add(Box.createVerticalStrut(16));

        // Quick evaluate
        JLabel evalTitle = Theme.createLabel("Avaliar Expressão", new Font("Segoe UI", Font.BOLD, 14), Theme.TEXT_PRIMARY);
        evalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(evalTitle);
        rightPanel.add(Box.createVerticalStrut(6));

        evalField = new JTextField();
        evalField.setFont(new Font("Consolas", Font.PLAIN, 13));
        evalField.setForeground(Theme.TEXT_PRIMARY);
        evalField.setBackground(Theme.BG_SURFACE);
        evalField.setCaretColor(Theme.TEXT_PRIMARY);
        evalField.setBorder(new EmptyBorder(6, 8, 6, 8));
        evalField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        evalField.setAlignmentX(Component.LEFT_ALIGNMENT);
        evalField.addActionListener(e -> evaluateExpression());
        rightPanel.add(evalField);
        rightPanel.add(Box.createVerticalStrut(4));

        evalResult = Theme.createLabel("", new Font("Consolas", Font.BOLD, 14), Theme.ACCENT_GREEN);
        evalResult.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(evalResult);
        rightPanel.add(Box.createVerticalGlue());

        // Preset functions
        JLabel presetTitle = Theme.createLabel("Presets", new Font("Segoe UI", Font.BOLD, 14), Theme.TEXT_PRIMARY);
        presetTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(presetTitle);
        rightPanel.add(Box.createVerticalStrut(6));

        JPanel presetPanel = Theme.createDarkPanel();
        presetPanel.setLayout(new GridLayout(2, 3, 4, 4));
        presetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        presetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        addPresetBtn(presetPanel, "sin(x)");
        addPresetBtn(presetPanel, "cos(x)");
        addPresetBtn(presetPanel, "tan(x)");
        addPresetBtn(presetPanel, "x^2");
        addPresetBtn(presetPanel, "sqrt(x)");
        addPresetBtn(presetPanel, "ln(x)");

        rightPanel.add(presetPanel);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Add a default function
        addFunctionRow();
    }

    private void addFunctionRow() {
        if (functionRows.size() >= MAX_FUNCTIONS) return;

        int idx = functionRows.size();
        FunctionRow row = new FunctionRow(idx);
        functionRows.add(row);
        functionListPanel.add(row.panel);
        functionListPanel.revalidate();
        functionListPanel.repaint();

        graphPanel.addFunction("");
    }

    private void removeFunctionRow(int index) {
        if (index < 0 || index >= functionRows.size()) return;
        functionListPanel.remove(functionRows.get(index).panel);
        functionRows.remove(index);
        graphPanel.removeFunction(index);

        // Reindex
        for (int i = 0; i < functionRows.size(); i++) {
            functionRows.get(i).index = i;
            functionRows.get(i).updateLabel();
        }
        functionListPanel.revalidate();
        functionListPanel.repaint();
    }

    private void clearAllFunctions() {
        functionRows.clear();
        functionListPanel.removeAll();
        graphPanel.clearFunctions();
        functionListPanel.revalidate();
        functionListPanel.repaint();
        addFunctionRow();
    }

    private void evaluateExpression() {
        String expr = evalField.getText().trim();
        if (expr.isEmpty()) return;
        try {
            ExpressionParser parser = new ExpressionParser(expr, useDegrees);
            double result = parser.parse();
            evalResult.setText("= " + formatResult(result));
            evalResult.setForeground(Theme.ACCENT_GREEN);
        } catch (Exception ex) {
            evalResult.setText("Erro: " + ex.getMessage());
            evalResult.setForeground(Theme.ACCENT_RED);
        }
    }

    private String formatResult(double value) {
        if (Double.isNaN(value)) return "NaN";
        if (Double.isInfinite(value)) return value > 0 ? "\u221E" : "-\u221E";
        if (value == Math.floor(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.format("%.10g", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private void addPresetBtn(JPanel panel, String expr) {
        JButton btn = Theme.functionButton(expr);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.addActionListener(e -> {
            if (functionRows.isEmpty()) addFunctionRow();
            FunctionRow lastRow = functionRows.get(functionRows.size() - 1);
            if (!lastRow.input.getText().trim().isEmpty()) {
                addFunctionRow();
                lastRow = functionRows.get(functionRows.size() - 1);
            }
            lastRow.input.setText(expr);
            lastRow.onUpdate();
        });
        panel.add(btn);
    }

    private void styleCheck(JCheckBox cb) {
        cb.setFont(Theme.LABEL_FONT);
        cb.setForeground(Theme.TEXT_DIM);
        cb.setBackground(Theme.BG_DARK);
        cb.setFocusPainted(false);
    }

    private static final Color[] FUNC_COLORS = {
            Theme.ACCENT_BLUE, Theme.ACCENT_GREEN, Theme.ACCENT_PEACH,
            Theme.ACCENT_MAUVE, Theme.ACCENT_YELLOW, Theme.ACCENT_TEAL
    };

    private class FunctionRow {
        int index;
        JPanel panel;
        JTextField input;
        JLabel colorDot;

        FunctionRow(int index) {
            this.index = index;
            panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BorderLayout(4, 0));
            panel.setBorder(new EmptyBorder(2, 0, 2, 0));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

            Color color = FUNC_COLORS[index % FUNC_COLORS.length];

            colorDot = new JLabel("\u25CF");
            colorDot.setFont(new Font("Segoe UI", Font.BOLD, 16));
            colorDot.setForeground(color);
            colorDot.setPreferredSize(new Dimension(20, 28));
            panel.add(colorDot, BorderLayout.WEST);

            input = new JTextField();
            input.setFont(new Font("Consolas", Font.PLAIN, 13));
            input.setForeground(Theme.TEXT_PRIMARY);
            input.setBackground(Theme.BG_SURFACE);
            input.setCaretColor(Theme.TEXT_PRIMARY);
            input.setBorder(new EmptyBorder(4, 8, 4, 8));
            input.setToolTipText("f" + (index + 1) + "(x) = ...");

            // Placeholder
            input.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (input.getText().equals("f(x) = ...")) {
                        input.setText("");
                        input.setForeground(Theme.TEXT_PRIMARY);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (input.getText().isEmpty()) {
                        input.setText("f(x) = ...");
                        input.setForeground(Theme.TEXT_SUBTLE);
                    }
                }
            });
            input.setText("f(x) = ...");
            input.setForeground(Theme.TEXT_SUBTLE);

            input.addActionListener(e -> onUpdate());
            input.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    onUpdate();
                }
            });
            panel.add(input, BorderLayout.CENTER);

            JButton removeBtn = Theme.createButton("\u00D7", Theme.BG_DARK, Theme.BTN_CLEAR, Theme.ACCENT_RED);
            removeBtn.setPreferredSize(new Dimension(28, 28));
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            removeBtn.addActionListener(e -> removeFunctionRow(this.index));
            panel.add(removeBtn, BorderLayout.EAST);
        }

        void onUpdate() {
            String expr = input.getText().trim();
            if (expr.equals("f(x) = ...")) expr = "";
            graphPanel.updateFunction(index, expr);
        }

        void updateLabel() {
            Color color = FUNC_COLORS[index % FUNC_COLORS.length];
            colorDot.setForeground(color);
        }
    }
}
