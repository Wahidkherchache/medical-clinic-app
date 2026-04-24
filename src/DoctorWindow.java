import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class DoctorWindow extends JFrame {

    private Connection connection;
    private JTable table;
    private JTextField txtLastName, txtFirstName, txtPhone;
    private JComboBox<String> cmbSpecialty;
    private JLabel statusLabel;

    public DoctorWindow(Connection connection) {
        this.connection = connection;
        initialize();
        loadDoctors();
    }

    private void initialize() {
        setTitle("Doctor Management");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(248, 250, 252));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(29, 78, 216),
                        getWidth(), 0, new Color(37, 99, 235));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 900, 70);
        mainPanel.add(header);

        JLabel titleLbl = new JLabel("👨‍⚕️  Doctor Management");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBounds(20, 15, 400, 40);
        header.add(titleLbl);

        // ── Left form panel ──────────────────────────────────────────────────
        JPanel formPanel = createCard(15, 85, 260, 490);
        mainPanel.add(formPanel);

        JLabel formTitle = new JLabel("Doctor Details");
        formTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setBounds(15, 15, 200, 25);
        formPanel.add(formTitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(37, 99, 235));
        sep.setBounds(15, 45, 230, 2);
        formPanel.add(sep);

        txtLastName  = addField(formPanel, "Last Name",  55);
        txtFirstName = addField(formPanel, "First Name", 115);
        txtPhone     = addField(formPanel, "Phone",      175);

        // Specialty combo
        JLabel specLbl = new JLabel("Specialty");
        specLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        specLbl.setForeground(new Color(71, 85, 105));
        specLbl.setBounds(15, 235, 230, 16);
        formPanel.add(specLbl);

        cmbSpecialty = new JComboBox<>(new String[]{
            "General Practitioner", "Cardiologist", "Dermatologist",
            "Neurologist", "Pediatrician", "Orthopedist",
            "Ophthalmologist", "Gynecologist", "Radiologist", "Other"
        });
        cmbSpecialty.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmbSpecialty.setBounds(15, 253, 230, 32);
        cmbSpecialty.setBackground(Color.WHITE);
        formPanel.add(cmbSpecialty);

        // Buttons
        JButton btnAdd    = createButton("Add",    new Color(37, 99, 235));
        JButton btnUpdate = createButton("Update", new Color(5, 150, 105));
        JButton btnDelete = createButton("Delete", new Color(107, 114, 128));
        JButton btnClear  = createButton("Clear",  new Color(15, 23, 42));

        btnAdd.setBounds(15, 310, 110, 36);
        btnUpdate.setBounds(135, 310, 110, 36);
        btnDelete.setBounds(15, 355, 110, 36);
        btnClear.setBounds(135, 355, 110, 36);

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnClear);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setBounds(15, 460, 230, 20);
        formPanel.add(statusLabel);

        // ── Right table panel ────────────────────────────────────────────────
        JPanel tablePanel = createCard(290, 85, 595, 490);
        mainPanel.add(tablePanel);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBounds(15, 15, 25, 30);
        tablePanel.add(searchIcon);

        JTextField txtSearch = new JTextField();
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtSearch.setBounds(45, 15, 280, 32);
        tablePanel.add(txtSearch);

        JButton btnSearch  = createButton("Search", new Color(37, 99, 235));
        JButton btnRefresh = createButton("All",    new Color(15, 23, 42));
        btnSearch.setBounds(335, 15, 90, 32);
        btnRefresh.setBounds(432, 15, 90, 32);
        tablePanel.add(btnSearch);
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
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBounds(15, 60, 565, 415);
        tablePanel.add(scrollPane);

        // ── Actions ──────────────────────────────────────────────────────────
        btnAdd.addActionListener(e -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchDoctor(txtSearch.getText().trim()));
        btnRefresh.addActionListener(e -> loadDoctors());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtLastName.setText(safeGet(row, 1));
                    txtFirstName.setText(safeGet(row, 2));
                    String spec = safeGet(row, 3);
                    cmbSpecialty.setSelectedItem(spec.isEmpty() ? "General Practitioner" : spec);
                    txtPhone.setText(safeGet(row, 4));
                    setStatus("Row selected — edit and press Update", new Color(37, 99, 235));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String safeGet(int row, int col) {
        Object val = table.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    private JPanel createCard(int x, int y, int w, int h) {
        JPanel p = new JPanel(null) {
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
        p.setOpaque(false);
        p.setBounds(x, y, w, h);
        return p;
    }

    private JTextField addField(JPanel panel, String label, int y) {
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
                g2.setColor(hovered ? color.darker() : color);
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

    // ── Database Operations ───────────────────────────────────────────────────

    private void loadDoctors() {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Doctor ORDER BY Num_doctor");
            table.setModel(DbUtils.resultSetToTableModel(rs));
            setStatus("Loaded " + table.getRowCount() + " doctors", new Color(5, 150, 105));
        } catch (SQLException e) {
            setStatus("Error loading data", new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void addDoctor() {
        String ln   = txtLastName.getText().trim();
        String fn   = txtFirstName.getText().trim();
        String spec = cmbSpecialty.getSelectedItem().toString();
        String ph   = txtPhone.getText().trim();

        if (ln.isEmpty() || fn.isEmpty()) {
            setStatus("Last Name and First Name are required!", new Color(220, 38, 38));
            return;
        }
        String sql = "INSERT INTO Doctor (LastName, FirstName, Specialty, Phone) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, ln);
            ps.setString(2, fn);
            ps.setString(3, spec);
            ps.setString(4, ph);
            ps.executeUpdate();
            setStatus("Doctor added successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadDoctors();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void updateDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Please select a doctor to update", new Color(245, 158, 11)); return; }
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());

        String sql = "UPDATE Doctor SET LastName=?, FirstName=?, Specialty=?, Phone=? WHERE Num_doctor=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, txtLastName.getText().trim());
            ps.setString(2, txtFirstName.getText().trim());
            ps.setString(3, cmbSpecialty.getSelectedItem().toString());
            ps.setString(4, txtPhone.getText().trim());
            ps.setInt(5, id);
            ps.executeUpdate();
            setStatus("Doctor updated successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadDoctors();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void deleteDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Please select a doctor to delete", new Color(245, 158, 11)); return; }
        String name = safeGet(row, 1) + " " + safeGet(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Dr. " + name + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Doctor WHERE Num_doctor=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            setStatus("Doctor deleted ✓", new Color(5, 150, 105));
            clearFields();
            loadDoctors();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void searchDoctor(String keyword) {
        if (keyword.isEmpty()) { loadDoctors(); return; }
        String sql = "SELECT * FROM Doctor WHERE UPPER(LastName) LIKE ? OR UPPER(FirstName) LIKE ? OR UPPER(Specialty) LIKE ?";
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
        txtPhone.setText("");
        cmbSpecialty.setSelectedIndex(0);
        table.clearSelection();
        setStatus("Ready", new Color(100, 116, 139));
    }
}