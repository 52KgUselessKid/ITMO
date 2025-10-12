import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Mandelbrot extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Mandelbrot app = new Mandelbrot();
            app.setVisible(true);
        });
    }

    private final MandelbrotPanel panel;
    private final JTextField iterField;
    private final JButton zoomInButton, zoomOutButton, resetButton;
    private final JButton upButton, downButton, leftButton, rightButton;
    private final JLabel iterLabel, coordsLabel;

    public Mandelbrot() {
        super("Множество Мандельброта");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 920);
        setLocationRelativeTo(null);

        panel = new MandelbrotPanel();
        iterField = new JTextField("500", 5);
        iterLabel = new JLabel("Макс итераций:");
        coordsLabel = new JLabel();

        zoomInButton = new JButton("+");
        zoomOutButton = new JButton("-");
        resetButton = new JButton("0");

        upButton = new JButton("↓");
        downButton = new JButton("↑");
        leftButton = new JButton("←");
        rightButton = new JButton("→");

        Color bgPanel = new Color(25, 25, 25);
        Color btnColor = new Color(50, 50, 50);
        Color textColor = Color.WHITE;

        iterLabel.setForeground(textColor);
        coordsLabel.setForeground(textColor);

        for (JButton b : new JButton[]{zoomInButton, zoomOutButton, resetButton,
                upButton, downButton, leftButton, rightButton}) {
            b.setBackground(btnColor);
            b.setForeground(textColor);
            b.setFocusPainted(false);
        }

        iterField.setBackground(btnColor);
        iterField.setForeground(textColor);
        iterField.setCaretColor(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(bgPanel);
        topPanel.add(iterLabel);
        topPanel.add(iterField);
        topPanel.add(coordsLabel);

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

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        getContentPane().add(panel, BorderLayout.CENTER);

        iterField.addActionListener(e -> applyIterations());
        iterField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyIterations(); }
        });

        zoomInButton.addActionListener(e -> panel.zoom(0.5));
        zoomOutButton.addActionListener(e -> panel.zoom(2.0));
        resetButton.addActionListener(e -> panel.resetView());

        upButton.addActionListener(e -> panel.move(0, -0.2));
        downButton.addActionListener(e -> panel.move(0, 0.2));
        leftButton.addActionListener(e -> panel.move(-0.2, 0));
        rightButton.addActionListener(e -> panel.move(0.2, 0));

        panel.setViewChangeListener((reMin, reMax, imMin, imMax) ->
                coordsLabel.setText(String.format("  Re:[%.3f, %.3f]  Im:[%.3f, %.3f]",
                        reMin, reMax, imMin, imMax)));
        panel.render();
    }

    private void applyIterations() {
        try {
            int val = Integer.parseInt(iterField.getText().trim());
            if (val < 1) val = 1;
            if (val > 10000) val = 10000;
            iterField.setText(String.valueOf(val));
            panel.setMaxIterations(val);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите целое число для итераций.");
            iterField.setText("500");
            panel.setMaxIterations(500);
        }
    }

    static class MandelbrotPanel extends JPanel {
        private double reMin = -2.5, reMax = 1.0, imMin = -1.5, imMax = 1.5;
        private int maxIterations = 700;
        private BufferedImage image;
        private int imageWidth, imageHeight;
        private int offsetX, offsetY;

        interface ViewChangeListener {
            void onViewChanged(double reMin, double reMax, double imMin, double imMax);
        }
        private ViewChangeListener listener;

        public MandelbrotPanel() {
            setBackground(new Color(30, 30, 30));
            addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    calculateImageSize();
                    render();
                }
            });
        }

        public void setViewChangeListener(ViewChangeListener l) { listener = l; }
        private void notifyViewChanged() {
            if (listener != null) listener.onViewChanged(reMin, reMax, imMin, imMax);
        }

        public void setMaxIterations(int m) { maxIterations = m; render(); }

        public void resetView() {
            reMin = -2.5; reMax = 1.0; imMin = -1.5; imMax = 1.5;
            notifyViewChanged(); render();
        }

        public void zoom(double factor) {
            double rc = (reMin + reMax)/2, ic = (imMin + imMax)/2;
            double rHalf = (reMax - reMin)*factor/2, iHalf = (imMax - imMin)*factor/2;
            reMin = rc - rHalf; reMax = rc + rHalf;
            imMin = ic - iHalf; imMax = ic + iHalf;
            notifyViewChanged(); render();
        }

        public void move(double dx, double dy) {
            double reShift = (reMax - reMin)*dx;
            double imShift = (imMax - imMin)*dy;
            reMin += reShift; reMax += reShift;
            imMin += imShift; imMax += imShift;
            notifyViewChanged(); render();
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
            double reRange = reMax - reMin, imRange = imMax - imMin;

            for (int y = 0; y < imageHeight; y++) {
                double c_im = imMax - y * imRange / (imageHeight - 1);
                for (int x = 0; x < imageWidth; x++) {
                    double c_re = reMin + x * reRange / (imageWidth - 1);
                    double z_re = 0, z_im = 0;
                    int n = 0;
                    while (n < maxIterations && z_re * z_re + z_im * z_im <= 4.0) {
                        double new_re = z_re * z_re - z_im * z_im + c_re;
                        z_im = 2 * z_re * z_im + c_im;
                        z_re = new_re;
                        n++;
                    }
                    int color = (n == maxIterations) ? 0x000000 : getColor(n);
                    image.setRGB(x, y, color);
                }
            }
            notifyViewChanged();
            repaint();
        }

        private int getColor(int n) {
            float t = (float) n / maxIterations;
            Color c = Color.getHSBColor(0.65f - 0.65f * t, 1.0f, 1.0f);
            return c.getRGB();
        }
    }
}