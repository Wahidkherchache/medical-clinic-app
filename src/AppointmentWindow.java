import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class AppointmentWindow extends JFrame {

    private Connection connection;
    private JTable table;
    private JComboBox<String> cmbPatient, cmbDoctor, cmbTime, cmbStatus, cmbFilterDoctor;
    private JTextField txtDate, txtFilterDate;
    private JLabel statusLabel;

    public AppointmentWindow(Connection connection) {
        this.connection = connection;
        initialize();
        loadCombos();
        loadAppointments();
    }

    private void initialize() {
        setTitle("Appointment Management");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(248, 250, 252));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(4, 120, 87),
                        getWidth(), 0, new Color(5, 150, 105));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 1000, 70);
        mainPanel.add(header);

        JLabel titleLbl = new JLabel("📅  Appointment Management");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBounds(20, 15, 450, 40);
        header.add(titleLbl);

        // ── Left form panel ──────────────────────────────────────────────────
        JPanel formPanel = createCard(15, 85, 280, 530);
        mainPanel.add(formPanel);

        JLabel formTitle = new JLabel("New Appointment");
        formTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setBounds(15, 15, 220, 25);
        formPanel.add(formTitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(5, 150, 105));
        sep.setBounds(15, 45, 250, 2);
        formPanel.add(sep);

        // Patient combo
        addLabel(formPanel, "Patient", 55);
        cmbPatient = createCombo();
        cmbPatient.setBounds(15, 73, 250, 32);
        formPanel.add(cmbPatient);

        // Doctor combo
        addLabel(formPanel, "Doctor", 115);
        cmbDoctor = createCombo();
        cmbDoctor.setBounds(15, 133, 250, 32);
        formPanel.add(cmbDoctor);

        // Date
        addLabel(formPanel, "Date (DD/MM/YYYY)", 175);
        txtDate = new JTextField();
        txtDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtDate.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtDate.setBounds(15, 193, 250, 32);
        formPanel.add(txtDate);

        // Time combo
        addLabel(formPanel, "Time Slot", 235);
        cmbTime = new JComboBox<>(new String[]{
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "13:00", "13:30", "14:00",
            "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
        });
        cmbTime.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmbTime.setBounds(15, 253, 250, 32);
        cmbTime.setBackground(Color.WHITE);
        formPanel.add(cmbTime);

        // Status combo
        addLabel(formPanel, "Status", 295);
        cmbStatus = new JComboBox<>(new String[]{"Planned", "Completed", "Cancelled"});
        cmbStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmbStatus.setBounds(15, 313, 250, 32);
        cmbStatus.setBackground(Color.WHITE);
        formPanel.add(cmbStatus);

        // Buttons
        JButton btnBook   = createButton("Book",   new Color(5, 150, 105));
        JButton btnUpdate = createButton("Update", new Color(37, 99, 235));
        JButton btnCancel = createButton("Cancel", new Color(220, 38, 38));
        JButton btnClear  = createButton("Clear",  new Color(15, 23, 42));

        btnBook.setBounds(15, 365, 120, 36);
        btnUpdate.setBounds(145, 365, 120, 36);
        btnCancel.setBounds(15, 410, 120, 36);
        btnClear.setBounds(145, 410, 120, 36);

        formPanel.add(btnBook);
        formPanel.add(btnUpdate);
        formPanel.add(btnCancel);
        formPanel.add(btnClear);

        // Check availability button
        JButton btnCheck = createButton("Check Availability", new Color(124, 58, 237));
        btnCheck.setBounds(15, 458, 250, 36);
        formPanel.add(btnCheck);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setBounds(15, 505, 250, 20);
        formPanel.add(statusLabel);

        // ── Right table panel ────────────────────────────────────────────────
        JPanel tablePanel = createCard(310, 85, 675, 530);
        mainPanel.add(tablePanel);

        // Filter bar
        JLabel filterLbl = new JLabel("Filter by Doctor:");
        filterLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        filterLbl.setForeground(new Color(71, 85, 105));
        filterLbl.setBounds(15, 12, 110, 20);
        tablePanel.add(filterLbl);

        cmbFilterDoctor = createCombo();
        cmbFilterDoctor.setBounds(125, 10, 200, 30);
        tablePanel.add(cmbFilterDoctor);

        JLabel dateLbl = new JLabel("Date:");
        dateLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        dateLbl.setForeground(new Color(71, 85, 105));
        dateLbl.setBounds(335, 12, 40, 20);
        tablePanel.add(dateLbl);

        txtFilterDate = new JTextField();
        txtFilterDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtFilterDate.setToolTipText("DD/MM/YYYY");
        txtFilterDate.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtFilterDate.setBounds(380, 10, 110, 30);
        tablePanel.add(txtFilterDate);

        JButton btnFilter  = createButton("Filter",   new Color(5, 150, 105));
        JButton btnShowAll = createButton("Show All", new Color(15, 23, 42));
        btnFilter.setBounds(500, 10, 80, 30);
        btnShowAll.setBounds(585, 10, 80, 30);
        tablePanel.add(btnFilter);
        tablePanel.add(btnShowAll);

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
        table.setSelectionBackground(new Color(209, 250, 229));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    // Color rows by status
                    String status = t.getValueAt(row, t.getColumnCount() - 1) != null
                            ? t.getValueAt(row, t.getColumnCount() - 1).toString() : "";
                    if (status.equalsIgnoreCase("Cancelled"))
                        c.setBackground(new Color(254, 242, 242));
                    else if (status.equalsIgnoreCase("Completed"))
                        c.setBackground(new Color(240, 253, 244));
                    else
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBounds(15, 55, 645, 465);
        tablePanel.add(scrollPane);

        // ── Actions ───────────────────────────────────────────────────────────
        btnBook.addActionListener(e -> bookAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnCancel.addActionListener(e -> cancelAppointment());
        btnClear.addActionListener(e -> clearFields());
        btnCheck.addActionListener(e -> checkAvailability());
        btnFilter.addActionListener(e -> filterAppointments());
        btnShowAll.addActionListener(e -> loadAppointments());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) fillFormFromRow(row);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private void addLabel(JPanel panel, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setBounds(15, y, 250, 16);
        panel.add(lbl);
    }

    private JComboBox<String> createCombo() {
        JComboBox<String> cmb = new JComboBox<>();
        cmb.setFont(new Font("Tahoma", Font.PLAIN, 12));
        cmb.setBackground(Color.WHITE);
        return cmb;
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

    // ── Load combos from DB ────────────────────────────────────────────────────

    private void loadCombos() {
        try {
            // Patients
            cmbPatient.removeAllItems();
            cmbFilterDoctor.removeAllItems();
            cmbFilterDoctor.addItem("All Doctors");

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT Num_patient, LastName, FirstName FROM Patient ORDER BY LastName");
            while (rs.next())
                cmbPatient.addItem(rs.getInt(1) + " — " + rs.getString(2) + " " + rs.getString(3));

            // Doctors
            cmbDoctor.removeAllItems();
            rs = st.executeQuery("SELECT Num_doctor, LastName, FirstName, Specialty FROM Doctor ORDER BY LastName");
            while (rs.next()) {
                String entry = rs.getInt(1) + " — Dr. " + rs.getString(2) + " " + rs.getString(3) + " (" + rs.getString(4) + ")";
                cmbDoctor.addItem(entry);
                cmbFilterDoctor.addItem(entry);
            }
        } catch (SQLException e) {
            setStatus("Error loading combos", new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    // ── Database Operations ────────────────────────────────────────────────────

    private void loadAppointments() {
        try {
            String sql = "SELECT A.Num_Appointment, " +
                         "P.LastName || ' ' || P.FirstName AS PATIENT, " +
                         "D.LastName || ' ' || D.FirstName AS DOCTOR, " +
                         "D.Specialty, A.DateApp, A.TimeApp, A.status " +
                         "FROM Appointment A " +
                         "JOIN Patient P ON A.Num_Patient = P.Num_patient " +
                         "JOIN Doctor D ON A.Num_Doctor = D.Num_doctor " +
                         "ORDER BY A.DateApp DESC, A.TimeApp";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            table.setModel(DbUtils.resultSetToTableModel(rs));
            setStatus("Loaded " + table.getRowCount() + " appointments", new Color(5, 150, 105));
        } catch (SQLException e) {
            setStatus("Error loading appointments", new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void bookAppointment() {
        if (cmbPatient.getItemCount() == 0 || cmbDoctor.getItemCount() == 0) {
            setStatus("No patients or doctors available!", new Color(220, 38, 38));
            return;
        }
        String date = txtDate.getText().trim();
        String time = cmbTime.getSelectedItem().toString();
        if (date.isEmpty()) { setStatus("Please enter a date!", new Color(220, 38, 38)); return; }

        int patientId = extractId(cmbPatient.getSelectedItem().toString());
        int doctorId  = extractId(cmbDoctor.getSelectedItem().toString());
        String status = cmbStatus.getSelectedItem().toString();

        // Check availability first
        if (!isDoctorAvailable(doctorId, date, time, -1)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This doctor already has an appointment at " + time + " on " + date + ".\nBook anyway?",
                    "Slot Taken", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        String sql = "INSERT INTO Appointment (Num_Patient, Num_Doctor, DateApp, TimeApp, status) VALUES (?, ?, TO_DATE(?, 'DD/MM/YYYY'), ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, status);
            ps.executeUpdate();
            setStatus("Appointment booked successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadAppointments();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void updateAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Please select an appointment to update", new Color(245, 158, 11)); return; }
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());

        int patientId = extractId(cmbPatient.getSelectedItem().toString());
        int doctorId  = extractId(cmbDoctor.getSelectedItem().toString());
        String date   = txtDate.getText().trim();
        String time   = cmbTime.getSelectedItem().toString();
        String status = cmbStatus.getSelectedItem().toString();

        if (!isDoctorAvailable(doctorId, date, time, id)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This doctor already has an appointment at " + time + " on " + date + ".\nUpdate anyway?",
                    "Slot Taken", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        String sql = "UPDATE Appointment SET Num_Patient=?, Num_Doctor=?, DateApp=TO_DATE(?, 'DD/MM/YYYY'), TimeApp=?, status=? WHERE Num_Appointment=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, status);
            ps.setInt(6, id);
            ps.executeUpdate();
            setStatus("Appointment updated successfully ✓", new Color(5, 150, 105));
            clearFields();
            loadAppointments();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void cancelAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Please select an appointment to cancel", new Color(245, 158, 11)); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel this appointment?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE Appointment SET status='Cancelled' WHERE Num_Appointment=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            setStatus("Appointment cancelled ✓", new Color(5, 150, 105));
            loadAppointments();
        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void checkAvailability() {
        if (cmbDoctor.getItemCount() == 0) return;
        String date = txtDate.getText().trim();
        String time = cmbTime.getSelectedItem().toString();
        if (date.isEmpty()) { setStatus("Enter a date to check!", new Color(245, 158, 11)); return; }

        int doctorId = extractId(cmbDoctor.getSelectedItem().toString());
        if (isDoctorAvailable(doctorId, date, time, -1)) {
            setStatus("✓ Doctor is available at " + time, new Color(5, 150, 105));
        } else {
            setStatus("✗ Doctor is NOT available at " + time, new Color(220, 38, 38));
        }
    }

    private boolean isDoctorAvailable(int doctorId, String date, String time, int excludeId) {
        try {
            String sql = "SELECT COUNT(*) FROM Appointment WHERE Num_Doctor=? AND DateApp=TO_DATE(?, 'DD/MM/YYYY') AND TimeApp=? AND status != 'Cancelled'";
            if (excludeId > 0) sql += " AND NUM_APPOINTMENT != " + excludeId;
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void filterAppointments() {
        String filterDoc  = cmbFilterDoctor.getSelectedItem() != null ? cmbFilterDoctor.getSelectedItem().toString() : "";
        String filterDate = txtFilterDate.getText().trim();

        StringBuilder sql = new StringBuilder(
            "SELECT A.Num_Appointment, " +
            "P.LastName || ' ' || P.FirstName AS PATIENT, " +
            "D.LastName || ' ' || D.FirstName AS DOCTOR, " +
            "D.Specialty, A.DateApp, A.TimeApp, A.status " +
            "FROM Appointment A " +
            "JOIN Patient P ON A.Num_Patient = P.Num_patient " +
            "JOIN Doctor D ON A.Num_Doctor = D.Num_doctor WHERE 1=1 ");

        if (!filterDoc.equals("All Doctors") && !filterDoc.isEmpty()) {
            int docId = extractId(filterDoc);
            sql.append("AND A.NUM_DOCTOR = ").append(docId).append(" ");
        }
        if (!filterDate.isEmpty()) {
            sql.append("AND A.DateApp = TO_DATE('").append(filterDate).append("', 'DD/MM/YYYY') ");
        }
        sql.append("ORDER BY A.DateApp DESC, A.TimeApp");

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());
            table.setModel(DbUtils.resultSetToTableModel(rs));
            setStatus("Found " + table.getRowCount() + " result(s)", new Color(37, 99, 235));
        } catch (SQLException e) {
            setStatus("Error filtering: " + e.getMessage(), new Color(220, 38, 38));
            e.printStackTrace();
        }
    }

    private void fillFormFromRow(int row) {
        txtDate.setText(safeGet(row, 4));
        String time = safeGet(row, 5);
        for (int i = 0; i < cmbTime.getItemCount(); i++) {
            if (cmbTime.getItemAt(i).equals(time)) { cmbTime.setSelectedIndex(i); break; }
        }
        String status = safeGet(row, 6);
        for (int i = 0; i < cmbStatus.getItemCount(); i++) {
            if (cmbStatus.getItemAt(i).equalsIgnoreCase(status)) { cmbStatus.setSelectedIndex(i); break; }
        }
        setStatus("Row selected — edit and press Update", new Color(37, 99, 235));
    }

    private void clearFields() {
        txtDate.setText("");
        txtFilterDate.setText("");
        if (cmbPatient.getItemCount() > 0) cmbPatient.setSelectedIndex(0);
        if (cmbDoctor.getItemCount() > 0) cmbDoctor.setSelectedIndex(0);
        cmbTime.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
        setStatus("Ready", new Color(100, 116, 139));
    }

    private int extractId(String comboItem) {
        try {
            return Integer.parseInt(comboItem.split(" — ")[0].trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private String safeGet(int row, int col) {
        Object val = table.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }
}