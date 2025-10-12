import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Julia extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Julia app = new Julia();
            app.setVisible(true);
        });
    }

    private final JuliaPanel panel;
    private final JTextField iterField;
    private final JTextField cReField, cImField;
    private final JButton applyCButton;
    private final JButton zoomInButton, zoomOutButton, resetButton;
    private final JButton upButton, downButton, leftButton, rightButton;
    private final JLabel iterLabel, coordsLabel;

    public Julia() {
        super("Множество Жюлиа");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 970);
        setLocationRelativeTo(null);

        Color bgPanel = new Color(25, 25, 25);
        Color btnColor = new Color(60, 60, 60);
        Color textColor = Color.WHITE;

        panel = new JuliaPanel();
        iterField = new JTextField("500", 5);
        iterLabel = new JLabel("Макс итераций:");
        coordsLabel = new JLabel();

        iterLabel.setForeground(textColor);
        coordsLabel.setForeground(textColor);

        cReField = new JTextField("-0.5251993", 9);
        cImField = new JTextField("0.5251993", 9);
        applyCButton = new JButton("Применить");

        zoomInButton = new JButton("+");
        zoomOutButton = new JButton("-");
        resetButton = new JButton("0");

        upButton = new JButton("↓");
        downButton = new JButton("↑");
        leftButton = new JButton("←");
        rightButton = new JButton("→");

        JButton[] allButtons = {applyCButton, zoomInButton, zoomOutButton, resetButton,
                upButton, downButton, leftButton, rightButton};
        for (JButton b : allButtons) {
            b.setBackground(btnColor);
            b.setForeground(textColor);
            b.setFocusPainted(false);
        }

        JTextField[] textFields = {iterField, cReField, cImField};
        for (JTextField tf : textFields) {
            tf.setBackground(new Color(40, 40, 40));
            tf.setForeground(Color.WHITE);
            tf.setCaretColor(Color.WHITE);
            tf.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBackground(bgPanel);
        topPanel.add(new JLabel("c =") {{ setForeground(textColor); }});
        topPanel.add(cReField);
        topPanel.add(new JLabel("+ i") {{ setForeground(textColor); }});
        topPanel.add(cImField);
        topPanel.add(applyCButton);
        topPanel.add(iterLabel);
        topPanel.add(iterField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        bottomPanel.setBackground(bgPanel);

        Insets btnInsets = new Insets(2, 8, 2, 8);
        for (JButton b : new JButton[]{zoomInButton, zoomOutButton, resetButton,
                upButton, downButton, leftButton, rightButton}) {
            b.setMargin(btnInsets);
        }

        JPanel movePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        movePanel.setBackground(bgPanel);
        movePanel.add(upButton);
        movePanel.add(downButton);
        movePanel.add(leftButton);
        movePanel.add(rightButton);

        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        zoomPanel.setBackground(bgPanel);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(resetButton);

        bottomPanel.add(movePanel);
        bottomPanel.add(zoomPanel);
        bottomPanel.add(coordsLabel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        iterField.addActionListener(e -> applyIterations());
        iterField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyIterations();
            }
        });

        applyCButton.addActionListener(e -> {
            try {
                double re = Double.parseDouble(cReField.getText().trim());
                double im = Double.parseDouble(cImField.getText().trim());
                panel.setC(re, im);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа для c (Re и Im).");
            }
        });

        zoomInButton.addActionListener(e -> panel.zoom(0.5));
        zoomOutButton.addActionListener(e -> panel.zoom(2.0));
        resetButton.addActionListener(e -> panel.resetView());
        upButton.addActionListener(e -> panel.move(0, -0.2));
        downButton.addActionListener(e -> panel.move(0, 0.2));
        leftButton.addActionListener(e -> panel.move(-0.2, 0));
        rightButton.addActionListener(e -> panel.move(0.2, 0));

        panel.setViewChangeListener((reMin, reMax, imMin, imMax) -> {
            coordsLabel.setText(String.format("Re:[%.4f, %.4f]  Im:[%.4f, %.4f]",
                    reMin, reMax, imMin, imMax));
        });

        panel.render();
        coordsLabel.setText(String.format("Re:[%.4f, %.4f]  Im:[%.4f, %.4f]",
                panel.reMin, panel.reMax, panel.imMin, panel.imMax));
    }

    private void applyIterations() {
        try {
            int val = Integer.parseInt(iterField.getText().trim());
            if (val < 1) val = 1;
            if (val > 10000) val = 10000;
            iterField.setText(String.valueOf(val));
            panel.setMaxIterations(val);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите целое число для максимального количества итераций.");
            iterField.setText("500");
            panel.setMaxIterations(500);
        }
    }

    static class JuliaPanel extends JPanel {
        public double reMin = -1.5, reMax = 1.5, imMin = -1.5, imMax = 1.5;
        private double cRe = -0.5251993, cIm = 0.5251993;
        private int maxIterations = 500;
        private BufferedImage image;
        private int imageWidth, imageHeight;
        private int offsetX, offsetY;

        interface ViewChangeListener {
            void onViewChanged(double reMin, double reMax, double imMin, double imMax);
        }
        private ViewChangeListener viewChangeListener;

        public JuliaPanel() {
            setBackground(new Color(20, 20, 20));
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    calculateImageSize();
                    render();
                }
            });
        }

        public void setC(double re, double im) {
            this.cRe = re;
            this.cIm = im;
            render();
        }

        public void setMaxIterations(int m) {
            this.maxIterations = m;
            render();
        }

        public void setViewChangeListener(ViewChangeListener l) {
            this.viewChangeListener = l;
        }

        public void resetView() {
            reMin = -1.5; reMax = 1.5;
            imMin = -1.5; imMax = 1.5;
            notifyViewChanged();
            render();
        }

        public void zoom(double factor) {
            double reCenter = (reMin + reMax) / 2.0;
            double imCenter = (imMin + imMax) / 2.0;
            double reHalf = (reMax - reMin) * factor / 2.0;
            double imHalf = (imMax - imMin) * factor / 2.0;
            reMin = reCenter - reHalf;
            reMax = reCenter + reHalf;
            imMin = imCenter - imHalf;
            imMax = imCenter + imHalf;
            notifyViewChanged();
            render();
        }

        public void move(double dxFraction, double dyFraction) {
            double reShift = (reMax - reMin) * dxFraction;
            double imShift = (imMax - imMin) * dyFraction;
            reMin += reShift;
            reMax += reShift;
            imMin += imShift;
            imMax += imShift;
            notifyViewChanged();
            render();
        }

        private void notifyViewChanged() {
            if (viewChangeListener != null)
                viewChangeListener.onViewChanged(reMin, reMax, imMin, imMax);
        }

        private void calculateImageSize() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (panelWidth <= 0 || panelHeight <= 0) {
                imageWidth = 0;
                imageHeight = 0;
                return;
            }

            double graphAspect = (reMax - reMin) / (imMax - imMin);
            double panelAspect = (double) panelWidth / panelHeight;

            if (panelAspect > graphAspect) {
                imageHeight = panelHeight;
                imageWidth = (int) (panelHeight * graphAspect);
                offsetX = (panelWidth - imageWidth) / 2;
                offsetY = 0;
            } else {
                imageWidth = panelWidth;
                imageHeight = (int) (panelWidth / graphAspect);
                offsetX = 0;
                offsetY = (panelHeight - imageHeight) / 2;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.drawImage(image, offsetX, offsetY, imageWidth, imageHeight, null);
            }
        }

        public void render() {
            calculateImageSize();

            if (imageWidth <= 0 || imageHeight <= 0) return;

            image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            double reRange = reMax - reMin;
            double imRange = imMax - imMin;

            for (int y = 0; y < imageHeight; y++) {
                double z_im0 = imMax - (double)y * imRange / (imageHeight - 1);
                for (int x = 0; x < imageWidth; x++) {
                    double z_re0 = reMin + (double)x * reRange / (imageWidth - 1);
                    double z_re = z_re0;
                    double z_im = z_im0;
                    int n = 0;
                    double modulus2 = 0.0;

                    while (n < maxIterations) {
                        double z_re2 = z_re * z_re;
                        double z_im2 = z_im * z_im;
                        modulus2 = z_re2 + z_im2;
                        if (modulus2 > 4.0) break;
                        double new_re = z_re2 - z_im2 + cRe;
                        double new_im = 2 * z_re * z_im + cIm;
                        z_re = new_re;
                        z_im = new_im;
                        n++;
                    }

                    int color;
                    if (n >= maxIterations) color = 0x000000;
                    else {
                        double log_zn = Math.log(modulus2) / 2.0;
                        double nu = Math.log(log_zn / Math.log(2)) / Math.log(2);
                        double iteration = n + 1 - nu;
                        color = fractalColor(iteration);
                    }
                    image.setRGB(x, y, color);
                }
            }
            notifyViewChanged();
            repaint();
        }

        private int fractalColor(double iteration) {
            double t = iteration / Math.max(1, maxIterations);
            t = Math.max(0.0, Math.min(1.0, t));

            double r, g, b;
            if (t < 0.5) {
                double tt = t * 2.0;
                r = tt;
                g = tt * 0.5;
                b = 1.0 - tt;
            } else {
                double tt = (t - 0.5) * 2.0;
                r = 1.0;
                g = 0.5 + tt * 0.5;
                b = tt;
            }

            r = Math.pow(r, 0.9);
            g = Math.pow(g, 0.9);
            b = Math.pow(b, 0.9);

            int ir = (int)Math.round(r * 255);
            int ig = (int)Math.round(g * 255);
            int ib = (int)Math.round(b * 255);
            return (ir << 16) | (ig << 8) | ib;
        }
    }
}