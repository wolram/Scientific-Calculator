package com.vibecoding.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {
        super("Multi Calculator Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel header = Theme.createDarkPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(40, 30, 20, 30));

        JLabel titleIcon = Theme.createLabel("\u2211 \u222B \u221A", new Font("Segoe UI", Font.PLAIN, 32), Theme.ACCENT_BLUE);
        titleIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = Theme.createLabel("Multi Calculator Pro", Theme.TITLE_FONT, Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = Theme.createLabel("Científica  \u2022  Financeira  \u2022  Gráfica", Theme.SUBTITLE_FONT, Theme.TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleIcon);
        header.add(Box.createVerticalStrut(12));
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // Calculator cards
        JPanel cardsPanel = Theme.createDarkPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

        cardsPanel.add(createCalcCard(
                "Científica",
                "Funções trigonométricas, logaritmos, potências,\nfatorial, combinatória e mais. Estilo Casio/HP.",
                "\u222B",
                Theme.ACCENT_BLUE,
                this::openScientific
        ));
        cardsPanel.add(Box.createVerticalStrut(16));

        cardsPanel.add(createCalcCard(
                "Financeira HP 12C",
                "TVM, NPV, IRR, amortização, depreciação,\nbonds, percentuais e estatística.",
                "$",
                Theme.ACCENT_GREEN,
                this::openFinancial
        ));
        cardsPanel.add(Box.createVerticalStrut(16));

        cardsPanel.add(createCalcCard(
                "Gráfica HP 50G",
                "Plotagem de funções, zoom, pan, múltiplas\ncurvas, parser de expressões e análise.",
                "\u03C0",
                Theme.ACCENT_MAUVE,
                this::openGraphing
        ));

        add(cardsPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = Theme.createDarkPanel();
        footer.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel version = Theme.createLabel("v2.0.0  \u2022  VibeCoding", Theme.LABEL_FONT, Theme.TEXT_SUBTLE);
        version.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setLayout(new BorderLayout());
        footer.add(version, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCalcCard(String title, String description, String icon, Color accent, Runnable action) {
        JPanel card = new JPanel() {
            Color bg = Theme.BG_SURFACE;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                // Left accent stripe
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Double(0, 0, 6, getHeight(), 6, 6));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 20));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(460, 130));

        // Icon
        JLabel iconLabel = Theme.createLabel(icon, new Font("Segoe UI", Font.BOLD, 36), accent);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(60, 60));
        card.add(iconLabel, BorderLayout.WEST);

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = Theme.createLabel(title, new Font("Segoe UI", Font.BOLD, 18), Theme.TEXT_PRIMARY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));

        for (String line : description.split("\n")) {
            JLabel descLabel = Theme.createLabel(line, Theme.LABEL_FONT, Theme.TEXT_DIM);
            textPanel.add(descLabel);
        }

        card.add(textPanel, BorderLayout.CENTER);

        // Arrow
        JLabel arrow = Theme.createLabel("\u279C", new Font("Segoe UI", Font.BOLD, 20), Theme.TEXT_SUBTLE);
        arrow.setHorizontalAlignment(SwingConstants.CENTER);
        arrow.setPreferredSize(new Dimension(30, 30));
        card.add(arrow, BorderLayout.EAST);

        // Hover + click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty("bg", Theme.BG_OVERLAY);
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty("bg", Theme.BG_SURFACE);
                card.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private void openScientific() {
        ScientificCalculatorUI calc = new ScientificCalculatorUI(this);
        calc.setVisible(true);
        setVisible(false);
    }

    private void openFinancial() {
        FinancialCalculatorUI calc = new FinancialCalculatorUI(this);
        calc.setVisible(true);
        setVisible(false);
    }

    private void openGraphing() {
        GraphingCalculatorUI calc = new GraphingCalculatorUI(this);
        calc.setVisible(true);
        setVisible(false);
    }
}
