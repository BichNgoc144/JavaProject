package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;

public class UseMachineForm extends JFrame {
    private JTable machineTable;
    private JLabel totalLabel;
    private JButton startButton;
    private JButton stopButton;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double pricePerHour;
    private JButton btnLogout;
    private int userId;
    
    // Usage information components
    private JPanel infoPanel;
    private JPanel startTimePanel;
    private JPanel usageDurationPanel;
    private JPanel costPanel;
    private JPanel balancePanel;
    private JLabel startTimeLabel;
    private JLabel usageDurationLabel;
    private JLabel costLabel;
    private JLabel balanceLabel;
    private Timer usageTimer;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    
    private final Color DARK_BG = new Color(20, 20, 40);
    private final Color NEON_BLUE = new Color(0, 200, 255);
    private final Color NEON_GREEN = new Color(0, 255, 100);
    private final Color NEON_PURPLE = new Color(170, 0, 255);
    private final Color PANEL_BG = new Color(30, 30, 50);
    private final Color TEXT_COLOR = new Color(220, 220, 220);
    private final Color TABLE_GRID_COLOR = new Color(60, 60, 100);
    private final Color ROW_EVEN = new Color(30, 30, 50);
    private final Color ROW_ODD = new Color(40, 40, 60);
    private final Color ROW_SELECTED = new Color(70, 50, 100);
    
    public UseMachineForm() {
        this(1); // Default constructor với userId = 1
    }

    public UseMachineForm(int userid) {
        this.userId = userid;
        
        // Thiết lập frame
        setTitle("GAME STATION - NET CAFE");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel với background gaming
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tạo bảng với style gaming - Đã xóa cột "Chọn"
        String[] columnNames = {"Mã máy", "Tên máy", "Giá theo giờ", "Mô tả"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        machineTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Màu nền cho hàng chẵn và lẻ khác nhau để phân biệt
                c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                c.setForeground(TEXT_COLOR);
                
                // Thêm đường viền cho mỗi ô để rõ ràng hơn
                JComponent jc = (JComponent) c;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR));
                
                // Định dạng cột theo loại dữ liệu
                if (column == 0) { // Mã máy
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                    c.setFont(new Font("Consolas", Font.BOLD, 14));
                } else if (column == 2) { // Cột giá tiền (đã thay đổi index do xóa cột đầu)
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT);
                    c.setForeground(NEON_GREEN);
                    // Định dạng hiển thị giá tiền
                    if (c instanceof JLabel && getValueAt(row, column) != null) {
                        double value = Double.parseDouble(getValueAt(row, column).toString());
                        ((JLabel)c).setText(String.format("%,.1f", value));
                    }
                }
                
                // Highlight hàng được chọn
                if (isRowSelected(row)) {
                    jc.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, NEON_PURPLE));
                    c.setBackground(ROW_SELECTED);
                }
                
                return c;
            }
        };
        
        // Style cho bảng
        machineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        machineTable.setRowSelectionAllowed(true);  // Cho phép chọn theo dòng
        machineTable.setColumnSelectionAllowed(false); // Không cho phép chọn theo cột
        machineTable.setCellSelectionEnabled(false); // Không cho phép chọn theo ô
        machineTable.setSelectionBackground(NEON_PURPLE);
        machineTable.setSelectionForeground(Color.BLACK);
        machineTable.setFont(new Font("Consolas", Font.PLAIN, 14));
        machineTable.setRowHeight(40); // Tăng chiều cao của hàng để hiển thị tốt hơn
        
        // Điều chỉnh độ rộng cột
        TableColumnModel columnModel = machineTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);     // Mã máy
        columnModel.getColumn(1).setPreferredWidth(150);    // Tên máy
        columnModel.getColumn(2).setPreferredWidth(100);    // Giá theo giờ
        columnModel.getColumn(3).setPreferredWidth(280);    // Mô tả
        
        // Sự kiện click vào dòng
        machineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = machineTable.getSelectedRow();
                if (selectedRow >= 0) {
                    startButton.setEnabled(true);
                }
            }
        });
        
        // Style header bảng
        JTableHeader header = machineTable.getTableHeader();
        header.setBackground(new Color(20, 20, 40));
        header.setForeground(NEON_BLUE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, NEON_BLUE));
        header.setReorderingAllowed(false); // Không cho phép kéo thả cột
        
        // Đặt renderer cho header để tạo style nhất quán
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(new Color(20, 20, 40));
                label.setForeground(NEON_BLUE);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(60, 60, 100)));
                return label;
            }
        });

        // Tạo custom grid line với màu rõ ràng
        machineTable.setShowGrid(true);
        machineTable.setGridColor(new Color(60, 60, 100));
        machineTable.setIntercellSpacing(new Dimension(1, 1)); // Tăng khoảng cách giữa các ô
        
        JScrollPane scrollPane = new JScrollPane(machineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Label tổng tiền với style gaming
        totalLabel = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(NEON_BLUE);
        totalLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(totalLabel, BorderLayout.NORTH);

        // Tạo panel thông tin với style gaming
        createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        startButton = createGamingButton("BẮT ĐẦU", NEON_GREEN);
        startButton.addActionListener(e -> startUsingMachine());

        stopButton = createGamingButton("KẾT THÚC", NEON_PURPLE);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopUsingMachine());

        btnLogout = createGamingButton("TRỞ VỀ", new Color(60, 60, 80));
        btnLogout.setForeground(TEXT_COLOR);
        btnLogout.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true);
            dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(btnLogout);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadMachines();
    }

    private JButton createGamingButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(bgColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
 // Tải danh sách máy từ cơ sở dữ liệu vào bảng
    private void loadMachines() {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT machine_code, machine_name, hourly_rate, description FROM machines";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            DefaultTableModel model = (DefaultTableModel) machineTable.getModel();
            model.setRowCount(0);  // Xóa dữ liệu cũ

            while (rs.next()) {
                String machineCode = rs.getString("machine_code");
                String machineName = rs.getString("machine_name");
                double hourlyRate = rs.getDouble("hourly_rate");
                String description = rs.getString("description");

                // Đã xóa giá trị "false" của cột "Chọn"
                model.addRow(new Object[]{machineCode, machineName, hourlyRate, description});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách máy!");
        }
    }

    // Bắt đầu sử dụng máy
    private void startUsingMachine() {
        int selectedRow = machineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một máy để sử dụng!");
            return;
        }

        // Check current balance
        double currentBalance = getCurrentBalance();
        if (currentBalance <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Số dư tài khoản của bạn không đủ để sử dụng máy!\nVui lòng nạp tiền trước khi sử dụng.",
                    "Số dư không đủ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Đã thay đổi index do xóa cột đầu
        String selectedMachineName = (String) machineTable.getValueAt(selectedRow, 1); // Tên máy là cột thứ 2 (index 1)
        Object priceObj = machineTable.getValueAt(selectedRow, 2); // Giá theo giờ là cột thứ 3 (index 2)
        if (priceObj instanceof Number) {
            pricePerHour = ((Number) priceObj).doubleValue();
        } else {
            pricePerHour = Double.parseDouble(priceObj.toString());
        }
        startTime = LocalDateTime.now();

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO machine_usage (user_id, machine_name, start_time, total_amount) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);  // Sử dụng ID người dùng hiện tại
            stmt.setString(2, selectedMachineName);
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            stmt.setBigDecimal(4, new java.math.BigDecimal(0));  // Ban đầu là 0

            stmt.executeUpdate();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            
            // Update UI with usage information
            startTimeLabel.setText(startTime.format(timeFormatter));
            
            // Start the usage timer to update information every second
            if (usageTimer != null && usageTimer.isRunning()) {
                usageTimer.stop();
            }
            
            usageTimer = new Timer(1000, e -> updateUsageInfo());
            usageTimer.start();
            
            JOptionPane.showMessageDialog(this, "Đã bắt đầu sử dụng máy: " + selectedMachineName);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi bắt đầu sử dụng máy!");
        }
    }

    // Kết thúc sử dụng máy
    private void stopUsingMachine() {
        int selectedRow = machineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một máy!");
            return;
        }

        if (usageTimer != null && usageTimer.isRunning()) {
            usageTimer.stop();
        }

        String selectedMachineName = (String) machineTable.getValueAt(selectedRow, 1);
        endTime = LocalDateTime.now();

        Duration duration = Duration.between(startTime, endTime);
        long secondsUsed = duration.getSeconds();
        double hoursUsed = secondsUsed / 3600.0; 
        double totalAmount = hoursUsed * pricePerHour;
        System.out.println("secon " + secondsUsed);
        System.out.println("hour: " + hoursUsed);
        System.out.println("total: " + totalAmount);

        try (Connection conn = KetNoiCSDL.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật machine_usage
            String sqlUpdateUsage = "UPDATE machine_usage SET end_time = ?, total_amount = ? " +
                    "WHERE user_id = ? AND machine_name = ? AND end_time IS NULL";
            try (PreparedStatement stmtUsage = conn.prepareStatement(sqlUpdateUsage)) {
                stmtUsage.setTimestamp(1, Timestamp.valueOf(endTime));
                stmtUsage.setBigDecimal(2, new java.math.BigDecimal(totalAmount));
                stmtUsage.setInt(3, userId);  // Sử dụng ID người dùng hiện tại
                stmtUsage.setString(4, selectedMachineName);
                stmtUsage.executeUpdate();
            }

            // Get updated balance
            double totalDeposit = 0;
            String sqlDeposit = "SELECT COALESCE(SUM(amount), 0) AS total_deposit FROM deposit WHERE user_id = ?";
            try (PreparedStatement stmtDeposit = conn.prepareStatement(sqlDeposit)) {
                stmtDeposit.setInt(1, userId);
                try (ResultSet rs = stmtDeposit.executeQuery()) {
                    if (rs.next()) {
                        totalDeposit = rs.getDouble("total_deposit");
                    }
                }
            }

            double totalUsage = 0;
            String sqlUsage = "SELECT COALESCE(SUM(total_amount), 0) AS total_usage FROM machine_usage WHERE user_id = ?";
            try (PreparedStatement stmtUsageSum = conn.prepareStatement(sqlUsage)) {
                stmtUsageSum.setInt(1, userId); // Fixed: Use userId instead of hardcoded 1
                try (ResultSet rs = stmtUsageSum.executeQuery()) {
                    if (rs.next()) {
                        totalUsage = rs.getDouble("total_usage");
                    }
                }
            }

            double remaining = totalDeposit - totalUsage;
            if (remaining < 0) remaining = 0;

            conn.commit();

            // Format usage duration nicely
            String formattedDuration = String.format("%02d:%02d:%02d", 
                    duration.toHours(), 
                    duration.toMinutesPart(), 
                    duration.toSecondsPart());
            
            // Reset UI components
            startTimeLabel.setText("Chưa bắt đầu");
            usageDurationLabel.setText("00:00:00");
            costLabel.setText("0 VNĐ");
            balanceLabel.setText(String.format("%.0f VNĐ", remaining));
            totalLabel.setText(String.format("Tổng tiền: %.0f VNĐ", totalAmount));
            
            // Show detailed invoice
            String message = String.format(
                    "HÓA ĐƠN SỬ DỤNG MÁY\n" +
                    "================================\n" +
                    "Máy: %s\n" +
                    "Thời gian bắt đầu: %s\n" +
                    "Thời gian kết thúc: %s\n" +
                    "Thời gian sử dụng: %s\n" +
                    "Giá theo giờ: %.0f VNĐ\n" +
                    "================================\n" +
                    "Tổng tiền: %.0f VNĐ\n" +
                    "Số dư còn lại: %.0f VNĐ",
                    selectedMachineName,
                    startTime.format(timeFormatter),
                    endTime.format(timeFormatter),
                    formattedDuration,
                    pricePerHour,
                    totalAmount,
                    remaining
            );
            JOptionPane.showMessageDialog(this, message, "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);

            startButton.setEnabled(true);
            stopButton.setEnabled(false);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kết thúc sử dụng máy!");
        }
    }


 
 // Get current balance from database
    private double getCurrentBalance() {
        double totalDeposit = 0;
        double totalUsage = 0;
        
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Get total deposits
            String sqlDeposit = "SELECT COALESCE(SUM(amount), 0) AS total_deposit FROM deposit WHERE user_id = ?";
            try (PreparedStatement stmtDeposit = conn.prepareStatement(sqlDeposit)) {
                stmtDeposit.setInt(1, userId);
                try (ResultSet rs = stmtDeposit.executeQuery()) {
                    if (rs.next()) {
                        totalDeposit = rs.getDouble("total_deposit");
                    }
                }
            }
            
            // Get total usage
            String sqlUsage = "SELECT COALESCE(SUM(total_amount), 0) AS total_usage FROM machine_usage WHERE user_id = ?";
            try (PreparedStatement stmtUsageSum = conn.prepareStatement(sqlUsage)) {
                stmtUsageSum.setInt(1, userId);
                try (ResultSet rs = stmtUsageSum.executeQuery()) {
                    if (rs.next()) {
                        totalUsage = rs.getDouble("total_usage");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin số dư!");
        }
        
        return totalDeposit - totalUsage;
    }
    
 // Create the information panel with gaming style
    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new CompoundBorder(
            new TitledBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 2),
                "THÔNG TIN SỬ DỤNG",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                NEON_BLUE
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        infoPanel.setBackground(new Color(30, 30, 50));
        
        // Tạo các panel thông tin theo phong cách gaming
        startTimePanel = createInfoPanel("Thời gian bắt đầu:", "Chưa bắt đầu");
        infoPanel.add(startTimePanel);
        startTimeLabel = getValueLabelFromPanel(startTimePanel);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        usageDurationPanel = createInfoPanel("Thời lượng sử dụng:", "00:00:00");
        infoPanel.add(usageDurationPanel);
        usageDurationLabel = getValueLabelFromPanel(usageDurationPanel);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        costPanel = createInfoPanel("Chi phí hiện tại:", "0 VNĐ");
        infoPanel.add(costPanel);
        costLabel = getValueLabelFromPanel(costPanel);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        balancePanel = createInfoPanel("Số dư hiện tại:", "10000 VNĐ");
        infoPanel.add(balancePanel);
        balanceLabel = getValueLabelFromPanel(balancePanel);
        
        updateBalance();
        add(infoPanel, BorderLayout.EAST);
    }

    private JPanel createInfoPanel(String labelText, String initialValue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(NEON_BLUE);
        panel.add(label, BorderLayout.WEST);
     
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(new Color(40, 40, 60));
        valuePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 90), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel valueLabel = new JLabel(initialValue, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        valueLabel.setForeground(Color.WHITE);
        valuePanel.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(valuePanel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel getValueLabelFromPanel(JPanel panel) {
        JPanel valuePanel = (JPanel) panel.getComponent(1);
        return (JLabel) valuePanel.getComponent(0);
    }
    
    // Update balance display
    private void updateBalance() {
        double balance = getCurrentBalance();
        balanceLabel.setText(String.format("%.0f VNĐ", balance));
    }
    
    // Update usage information in real-time
    private void updateUsageInfo() {
        if (startTime == null) return;
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(startTime, now);
        
        // Format duration as HH:MM:SS
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        usageDurationLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        
        // Calculate current cost
        double hoursUsed = duration.toMinutes() / 60.0;
        double currentCost = hoursUsed * pricePerHour;
      
        costLabel.setText(String.format("%.0f VNĐ", currentCost));
        
        // Update balance (current balance - current cost)
        double balance = getCurrentBalance() - currentCost;
        if (balance < 0) balance = 0;
        balanceLabel.setText(String.format("%.0f VNĐ", balance));
    }

      
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UseMachineForm(1).setVisible(true));
    }
}