import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class PatientWindow extends JFrame {

    private Connection connection;
    private JTable table;
    private JTextField txtLastName, txtFirstName, txtDOB, txtPhone, txtAddress, txtSearch;
    private JLabel statusLabel;

    public PatientWindow(Connection connection) {
        this.connection = connection;
        initialize();
        loadPatients();
    }

    private void initialize() {
        setTitle("Patient Management");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(248, 250, 252));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(185, 28, 28),
                        getWidth(), 0, new Color(220, 38, 38));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 900, 70);
        mainPanel.add(header);

        JLabel titleLbl = new JLabel("👤  Patient Management");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBounds(20, 15, 400, 40);
        header.add(titleLbl);

        // ── Left form panel ─────────────────────────────────────────────────
        JPanel formPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBounds(15, 85, 260, 490);
        mainPanel.add(formPanel);

        JLabel formTitle = new JLabel("Patient Details");
        formTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setBounds(15, 15, 200, 25);
        formPanel.add(formTitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 38, 38));
        sep.setBounds(15, 45, 230, 2);
        formPanel.add(sep);

        // Form fields
        txtLastName  = addField(formPanel, "Last Name",    55,  txtLastName);
        txtFirstName = addField(formPanel, "First Name",   115, txtFirstName);
        txtDOB       = addField(formPanel, "Date of Birth (DD/MM/YYYY)", 175, txtDOB);
        txtPhone     = addField(formPanel, "Phone",        235, txtPhone);
        txtAddress   = addField(formPanel, "Address",      295, txtAddress);

        // Action buttons
        JButton btnAdd    = createButton("Add",    new Color(220, 38, 38));
        JButton btnUpdate = createButton("Update", new Color(37, 99, 235));
        JButton btnDelete = createButton("Delete", new Color(107, 114, 128));
        JButton btnClear  = createButton("Clear",  new Color(15, 23, 42));

        btnAdd.setBounds(15, 370, 110, 36);
        btnUpdate.setBounds(135, 370, 110, 36);
        btnDelete.setBounds(15, 415, 110, 36);
        btnClear.setBounds(135, 415, 110, 36);

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnClear);

        // Status
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setBounds(15, 460, 230, 20);
        formPanel.add(statusLabel);

        // ── Right table panel ────────────────────────────────────────────────
        JPanel tablePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        tablePanel.setOpaque(false);
        tablePanel.setBounds(290, 85, 595, 490);
        mainPanel.add(tablePanel);

        // Search bar
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBounds(15, 15, 25, 30);
        tablePanel.add(searchIcon);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtSearch.setBounds(45, 15, 330, 32);
        tablePanel.add(txtSearch);

        JButton btnSearch = createButton("Search", new Color(220, 38, 38));
        btnSearch.setBounds(385, 15, 90, 32);
        tablePanel.add(btnSearch);

        JButton btnRefresh = createButton("All", new Color(15, 23, 42));
        btnRefresh.setBounds(480, 15, 90, 32);
        tablePanel.add(btnRefresh);

        // Table
        table = new JTable();
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.setSelectionBackground(new Color(254, 226, 226));
        table.setSelectionForeground(new Color(15, 23, 42));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBounds(15, 60, 565, 415);
        tablePanel.add(scrollPane);

        // ── Button Actions ──────────────────────────────────────────────────
        btnAdd.addActionListener(e -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDelete.addActionListener(e -> deletePatient());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchPatient());
        btnRefresh.addActionListener(e -> loadPatients());

        // Row click → fill form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtLastName.setText(table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "");
                    txtFirstName.setText(table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "");
                    txtDOB.setText(table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "");
                    txtPhone.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                    txtAddress.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
                    setStatus("Row selected — edit and press Update", new Color(37, 99, 235));
                }
            }
        });
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private JTextField addField(JPanel panel, String label, int y, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setBounds(15, y, 230, 16);
        panel.add(lbl);

        JTextField tf = new JTextField();
        tf.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        tf.setBounds(15, y + 18, 230, 32);
        panel.add(tf);
        return tf;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? color.darker() : color;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Tahoma", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ── Database Operations ─────────────────────────────────────────────────

    private void loadPatients() {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Patient ORDER BY Num_patient");
            table.setModel(DbUtils.resultSetToTableModel(rs));
            setStatus("Loaded " + table.getRowCount() + " patients", new Color(5, 150, 105));
        } catch (SQLException e) {
            setStatus("Error loading data", new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void addPatient() {
        String ln = txtLastName.getText().trim();
        String fn = txtFirstName.getText().trim();
        String dob = txtDOB.getText().trim();
        String ph = txtPhone.getText().trim();
        String addr = txtAddress.getText().trim();

        if (ln.isEmpty() || fn.isEmpty()) {
            setStatus("Last Name and First Name are required!", new Color(220, 38, 38));
            return;
        }

        String sql = "INSERT INTO Patient (LastName, FirstName, Date_of_Birth, Phone, Address) " +
                     "VALUES (?, ?, TO_DATE(?, 'DD/MM/YYYY'), ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, ln);
            ps.setString(2, fn);
            ps.setString(3, dob);
            ps.setString(4, ph);
            ps.setString(5, addr);
            ps.executeUpdate();
            setStatus("Patient added successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadPatients();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void updatePatient() {
        int row = table.getSelectedRow();
        if (row < 0) {
            setStatus("Please select a patient to update", new Color(245, 158, 11));
            return;
        }
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());

        String sql = "UPDATE Patient SET LastName=?, FirstName=?, Date_of_Birth=TO_DATE(?, 'DD/MM/YYYY'), Phone=?, Address=? " +
                     "WHERE Num_patient=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, txtLastName.getText().trim());
            ps.setString(2, txtFirstName.getText().trim());
            ps.setString(3, txtDOB.getText().trim());
            ps.setString(4, txtPhone.getText().trim());
            ps.setString(5, txtAddress.getText().trim());
            ps.setInt(6, id);
            ps.executeUpdate();
            setStatus("Patient updated successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadPatients();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void deletePatient() {
        int row = table.getSelectedRow();
        if (row < 0) {
            setStatus("Please select a patient to delete", new Color(245, 158, 11));
            return;
        }
        String name = table.getValueAt(row, 1) + " " + table.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient: " + name + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Patient WHERE Num_patient=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            setStatus("Patient deleted ✓", new Color(5, 150, 105));
            clearFields();
            loadPatients();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void searchPatient() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadPatients(); return; }

        String sql = "SELECT * FROM Patient WHERE UPPER(LastName) LIKE ? OR UPPER(FirstName) LIKE ? OR Phone LIKE ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            String kw = "%" + keyword.toUpperCase() + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            setStatus("Found " + table.getRowCount() + " result(s)", new Color(37, 99, 235));
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtLastName.setText("");
        txtFirstName.setText("");
        txtDOB.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
        setStatus("Ready", new Color(100, 116, 139));
    }
}