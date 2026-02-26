package com.vibecoding.calculator.graph;

import com.vibecoding.calculator.parser.ExpressionParser;
import com.vibecoding.calculator.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private double xMin = -10, xMax = 10, yMin = -10, yMax = 10;
    private final List<FunctionEntry> functions = new ArrayList<>();
    private boolean showGrid = true;
    private boolean showAxes = true;
    private Point dragStart;
    private boolean useDegrees = false;

    private static final Color[] FUNCTION_COLORS = {
            Theme.ACCENT_BLUE, Theme.ACCENT_GREEN, Theme.ACCENT_PEACH,
            Theme.ACCENT_MAUVE, Theme.ACCENT_YELLOW, Theme.ACCENT_TEAL
    };

    public static class FunctionEntry {
        public String expression;
        public Color color;
        public boolean visible;

        public FunctionEntry(String expression, Color color) {
            this.expression = expression;
            this.color = color;
            this.visible = true;
        }
    }

    public GraphPanel() {
        setBackground(new Color(0x18, 0x18, 0x28));
        setPreferredSize(new Dimension(600, 450));

        addMouseWheelListener(e -> {
            double factor = e.getWheelRotation() > 0 ? 1.2 : 1 / 1.2;
            double cx = xMin + (xMax - xMin) * ((double) e.getX() / getWidth());
            double cy = yMax - (yMax - yMin) * ((double) e.getY() / getHeight());
            double nw = (xMax - xMin) * factor;
            double nh = (yMax - yMin) * factor;
            xMin = cx - nw * ((double) e.getX() / getWidth());
            xMax = cx + nw * (1 - (double) e.getX() / getWidth());
            yMin = cy - nh * (1 - (double) e.getY() / getHeight());
            yMax = cy + nh * ((double) e.getY() / getHeight());
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
            @Override
            public void mouseReleased(MouseEvent e) { dragStart = null; }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    double dx = (e.getX() - dragStart.x) * (xMax - xMin) / getWidth();
                    double dy = (e.getY() - dragStart.y) * (yMax - yMin) / getHeight();
                    xMin -= dx; xMax -= dx;
                    yMin += dy; yMax += dy;
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
    }

    public void setUseDegrees(boolean deg) { this.useDegrees = deg; }

    public int addFunction(String expression) {
        Color color = FUNCTION_COLORS[functions.size() % FUNCTION_COLORS.length];
        functions.add(new FunctionEntry(expression, color));
        repaint();
        return functions.size() - 1;
    }

    public void updateFunction(int index, String expression) {
        if (index >= 0 && index < functions.size()) {
            functions.get(index).expression = expression;
            repaint();
        }
    }

    public void removeFunction(int index) {
        if (index >= 0 && index < functions.size()) {
            functions.remove(index);
            repaint();
        }
    }

    public void clearFunctions() {
        functions.clear();
        repaint();
    }

    public void toggleFunction(int index) {
        if (index >= 0 && index < functions.size()) {
            functions.get(index).visible = !functions.get(index).visible;
            repaint();
        }
    }

    public void resetView() {
        xMin = -10; xMax = 10; yMin = -10; yMax = 10;
        repaint();
    }

    public void zoomIn() {
        double cx = (xMin + xMax) / 2, cy = (yMin + yMax) / 2;
        double w = (xMax - xMin) / 2.5, h = (yMax - yMin) / 2.5;
        xMin = cx - w; xMax = cx + w; yMin = cy - h; yMax = cy + h;
        repaint();
    }

    public void zoomOut() {
        double cx = (xMin + xMax) / 2, cy = (yMin + yMax) / 2;
        double w = (xMax - xMin) * 0.625, h = (yMax - yMin) * 0.625;
        xMin = cx - w; xMax = cx + w; yMin = cy - h; yMax = cy + h;
        repaint();
    }

    public void setShowGrid(boolean show) { this.showGrid = show; repaint(); }
    public void setShowAxes(boolean show) { this.showAxes = show; repaint(); }

    public List<FunctionEntry> getFunctions() { return functions; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        if (showGrid) drawGrid(g2, w, h);
        if (showAxes) drawAxes(g2, w, h);

        for (FunctionEntry fn : functions) {
            if (fn.visible && fn.expression != null && !fn.expression.trim().isEmpty()) {
                drawFunction(g2, fn, w, h);
            }
        }

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0x30, 0x30, 0x50));
        g2.setStroke(new BasicStroke(0.5f));

        double step = calculateGridStep(xMax - xMin, w);
        double startX = Math.floor(xMin / step) * step;
        for (double x = startX; x <= xMax; x += step) {
            int px = toScreenX(x, w);
            g2.drawLine(px, 0, px, h);
        }

        step = calculateGridStep(yMax - yMin, h);
        double startY = Math.floor(yMin / step) * step;
        for (double y = startY; y <= yMax; y += step) {
            int py = toScreenY(y, h);
            g2.drawLine(0, py, w, py);
        }
    }

    private void drawAxes(Graphics2D g2, int w, int h) {
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(0x6C, 0x70, 0x86));

        // X axis
        if (yMin <= 0 && yMax >= 0) {
            int y0 = toScreenY(0, h);
            g2.drawLine(0, y0, w, y0);
        }
        // Y axis
        if (xMin <= 0 && xMax >= 0) {
            int x0 = toScreenX(0, w);
            g2.drawLine(x0, 0, x0, h);
        }

        // Tick labels
        g2.setFont(new Font("Consolas", Font.PLAIN, 10));
        g2.setColor(Theme.TEXT_SUBTLE);

        double step = calculateGridStep(xMax - xMin, w);
        double startX = Math.floor(xMin / step) * step;
        int y0 = toScreenY(0, h);
        for (double x = startX; x <= xMax; x += step) {
            if (Math.abs(x) < step * 0.01) continue;
            int px = toScreenX(x, w);
            String label = formatTickLabel(x);
            g2.drawString(label, px + 2, Math.min(Math.max(y0 + 14, 14), h - 2));
        }

        step = calculateGridStep(yMax - yMin, h);
        double startY = Math.floor(yMin / step) * step;
        int x0 = toScreenX(0, w);
        for (double y = startY; y <= yMax; y += step) {
            if (Math.abs(y) < step * 0.01) continue;
            int py = toScreenY(y, h);
            String label = formatTickLabel(y);
            g2.drawString(label, Math.min(Math.max(x0 + 4, 4), w - 40), py - 2);
        }
    }

    private void drawFunction(Graphics2D g2, FunctionEntry fn, int w, int h) {
        g2.setColor(fn.color);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        GeneralPath path = new GeneralPath();
        boolean started = false;
        double prevY = Double.NaN;

        for (int px = 0; px < w; px++) {
            double x = xMin + (xMax - xMin) * px / w;
            double y;
            try {
                String expr = fn.expression.replace("x", "(" + x + ")").replace("X", "(" + x + ")");
                ExpressionParser parser = new ExpressionParser(expr, useDegrees);
                y = parser.parse();
            } catch (Exception e) {
                y = Double.NaN;
            }

            if (Double.isNaN(y) || Double.isInfinite(y) || y < yMin - (yMax - yMin) * 10 || y > yMax + (yMax - yMin) * 10) {
                started = false;
                prevY = Double.NaN;
                continue;
            }

            // Detect discontinuities (e.g. tan)
            if (!Double.isNaN(prevY) && Math.abs(y - prevY) > (yMax - yMin) * 2) {
                started = false;
            }

            int py = toScreenY(y, h);
            if (!started) {
                path.moveTo(px, py);
                started = true;
            } else {
                path.lineTo(px, py);
            }
            prevY = y;
        }
        g2.draw(path);
    }

    private int toScreenX(double x, int w) { return (int) ((x - xMin) / (xMax - xMin) * w); }
    private int toScreenY(double y, int h) { return (int) ((yMax - y) / (yMax - yMin) * h); }

    private double calculateGridStep(double range, int pixels) {
        double rawStep = range * 80 / pixels;
        double mag = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double normalized = rawStep / mag;
        if (normalized <= 1) return mag;
        if (normalized <= 2) return 2 * mag;
        if (normalized <= 5) return 5 * mag;
        return 10 * mag;
    }

    private String formatTickLabel(double val) {
        if (Math.abs(val) >= 1e6 || (Math.abs(val) < 0.01 && val != 0)) {
            return String.format("%.1e", val);
        }
        if (val == Math.floor(val) && Math.abs(val) < 1e6) {
            return String.valueOf((int) val);
        }
        return String.format("%.2f", val);
    }
}
