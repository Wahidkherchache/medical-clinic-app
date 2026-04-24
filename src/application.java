import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class application extends JFrame{

    /**
	 * 
	 */
	private Connection connection;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	application window = new application();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public application() {
        connectToDatabase();
        initialize();
    }

    private void connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "Kherchache", "Abdelouahid"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Database connection failed!\n" + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initialize() {
        setTitle("Medical Clinic — Appointment Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with dark background
        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42),
                        getWidth(), getHeight(), new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative red accent line at top
                g2.setColor(new Color(220, 38, 38));
                g2.fillRect(0, 0, getWidth(), 4);

                // Subtle grid pattern
                g2.setColor(new Color(255, 255, 255, 8));
                g2.setStroke(new BasicStroke(1));
                for (int x = 0; x < getWidth(); x += 40) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 40) {
                    g2.drawLine(0, y, getWidth(), y);
                }
            }
        };
        setContentPane(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBounds(30, 20, 640, 120);
        mainPanel.add(headerPanel);

        // Clinic icon label
        JLabel iconLabel = new JLabel("🏥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setBounds(20, 15, 70, 90);
        headerPanel.add(iconLabel);

        // Title
        JLabel titleLabel = new JLabel("Medical Clinic");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setBounds(100, 15, 400, 45);
        headerPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Appointment Management System");
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setBounds(102, 58, 350, 25);
        headerPanel.add(subtitleLabel);

        // Connection status
        JLabel statusLabel = new JLabel("● Connected to Oracle");
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        statusLabel.setForeground(connection != null ? new Color(74, 222, 128) : new Color(248, 113, 113));
        statusLabel.setBounds(102, 85, 250, 20);
        headerPanel.add(statusLabel);

        // Section title
        JLabel sectionLabel = new JLabel("SELECT A MODULE");
        sectionLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        sectionLabel.setForeground(new Color(100, 116, 139));
        sectionLabel.setBounds(30, 160, 200, 20);
        mainPanel.add(sectionLabel);

        // Navigation buttons
        createNavButton(mainPanel, "👤  Patient Management",
                "Add, modify, delete and search patients",
                new Color(220, 38, 38), new Color(185, 28, 28), 30, 190, () -> {
                    new PatientWindow(connection).setVisible(true);
                });

        createNavButton(mainPanel, "👨‍⚕️  Doctor Management",
                "Manage clinic doctors and specialties",
                new Color(37, 99, 235), new Color(29, 78, 216), 240, 190, () -> {
                    new DoctorWindow(connection).setVisible(true);
                });

        createNavButton(mainPanel, "📅  Appointment Management",
                "Schedule, cancel and view appointments",
                new Color(5, 150, 105), new Color(4, 120, 87), 450, 190, () -> {
                    new AppointmentWindow(connection).setVisible(true);
                });
             
        // Footer
        JLabel footerLabel = new JLabel("USTHB — DataBase 1 Project  |  L2.ACAD.B  |  2025/2026");
        footerLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(71, 85, 105));
        footerLabel.setBounds(30, 460, 500, 20);
        mainPanel.add(footerLabel);
    }

    private void createNavButton(JPanel parent, String title, String desc,
                                  Color color, Color hoverColor, int x, int y, Runnable action) {
        JPanel card = new JPanel(null) {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = hovered ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Top accent bar
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);

                // Hover glow
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        action.run();
                    }
                });
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, 195, 260);
        parent.add(card);

        // Title label
        JLabel titleLbl = new JLabel("<html><center>" + title + "</center></html>");
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        titleLbl.setBounds(10, 30, 175, 60);
        card.add(titleLbl);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setBounds(20, 100, 155, 2);
        card.add(sep);

        // Description
        JLabel descLbl = new JLabel("<html><center>" + desc + "</center></html>");
        descLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        descLbl.setForeground(new Color(255, 255, 255, 200));
        descLbl.setHorizontalAlignment(SwingConstants.CENTER);
        descLbl.setBounds(10, 110, 175, 60);
        card.add(descLbl);

        // Open button
        JButton openBtn = new JButton("OPEN →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        openBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
        openBtn.setForeground(Color.WHITE);
        openBtn.setContentAreaFilled(false);
        openBtn.setBorderPainted(false);
        openBtn.setBorder(new LineBorder(new Color(255, 255, 255, 80), 1, true));
        openBtn.setBounds(47, 195, 100, 32);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> action.run());
        card.add(openBtn);
    }
}