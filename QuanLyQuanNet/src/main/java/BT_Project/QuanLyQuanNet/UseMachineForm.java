package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class UseMachineForm extends JFrame {
    private JTable machineTable;
    private JLabel totalLabel;
    private JButton startButton;
    private JButton stopButton;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double pricePerHour;
    private JButton btnLogout;
    private int userId ; 
    
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

    public UseMachineForm() {
        this(1); // Default constructor với userId = 1
    }
    
    public UseMachineForm(int userid) {
        this.userId = userid; // Fix lỗi: Phải gán userid (tham số) chứ không phải userId (thuộc tính mặc định)
        System.out.println("sudung may userID " + userid);
        setTitle("Sử Dụng Máy - Quản lý Quán Net");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị thông tin máy
        String[] columnNames = {"Chọn", "Mã máy", "Tên máy", "Giá theo giờ", "Mô tả"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        machineTable = new JTable(model);
        machineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(machineTable);
        add(scrollPane, BorderLayout.CENTER);

        // Label hiển thị tổng tiền
        totalLabel = new JLabel("Tổng tiền: 0 VNĐ");
        add(totalLabel, BorderLayout.NORTH);
        
        // Create information panel to display usage details on the right side
        createInfoPanel();

        // Nút "Bắt đầu sử dụng"
        startButton = new JButton("Bắt đầu");
        startButton.setPreferredSize(new Dimension(100, 30));

        // Nút "Kết thúc sử dụng"
        stopButton = new JButton("Kết thúc");
        stopButton.setPreferredSize(new Dimension(100, 30));
        stopButton.setEnabled(false);

        // Nút Home (thoát về giao diện user)
        btnLogout = new JButton("Home");
        btnLogout.setPreferredSize(new Dimension(100, 30));

        // Panel chứa nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(btnLogout);  // ✅ Thêm nút Home vào giao diện
        add(buttonPanel, BorderLayout.SOUTH);

        // Lấy danh sách máy từ cơ sở dữ liệu
        loadMachines();

        // Lắng nghe sự kiện bắt đầu sử dụng
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startUsingMachine();
            }
        });

        // Lắng nghe sự kiện kết thúc sử dụng
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopUsingMachine();
            }
        });

        // Nút Home
        btnLogout.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true); // Sử dụng userId đã lưu
            dispose();
        });
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

                model.addRow(new Object[]{false, machineCode, machineName, hourlyRate, description});
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

        String selectedMachineName = (String) machineTable.getValueAt(selectedRow, 2);
        Object priceObj = machineTable.getValueAt(selectedRow, 3);
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

        String selectedMachineName = (String) machineTable.getValueAt(selectedRow, 2);
        endTime = LocalDateTime.now();

        Duration duration = Duration.between(startTime, endTime);
        // Tính thời gian sử dụng bằng giây để tính tiền chính xác hơn
        long secondsUsed = duration.getSeconds();
        double hoursUsed = secondsUsed / 3600.0; // Chuyển đổi giây sang giờ để tính tiền chính xác
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

    // Create the information panel with usage details
    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setPreferredSize(new Dimension(250, getHeight()));
        infoPanel.setBackground(new Color(240, 240, 240));
        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel headerLabel = new JLabel("Thông tin sử dụng", JLabel.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        infoPanel.add(headerPanel);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        startTimePanel = createInfoLabel("Thời gian bắt đầu:", "Chưa bắt đầu");
        infoPanel.add(startTimePanel);
        startTimeLabel = (JLabel) ((JPanel) startTimePanel.getComponent(1)).getComponent(0);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        usageDurationPanel = createInfoLabel("Thời lượng sử dụng:", "00:00:00");
        infoPanel.add(usageDurationPanel);
        usageDurationLabel = (JLabel) ((JPanel) usageDurationPanel.getComponent(1)).getComponent(0);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        costPanel = createInfoLabel("Chi phí hiện tại:", "0 VNĐ");
        infoPanel.add(costPanel);
        costLabel = (JLabel) ((JPanel) costPanel.getComponent(1)).getComponent(0);
        
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        balancePanel = createInfoLabel("Số dư hiện tại:", "0 VNĐ");
        infoPanel.add(balancePanel);
        balanceLabel = (JLabel) ((JPanel) balancePanel.getComponent(1)).getComponent(0);
        
        // Update current balance
        updateBalance();
        
        infoPanel.add(Box.createVerticalGlue());
        
        add(infoPanel, BorderLayout.EAST);
    }
    
    // Helper method to create standardized info label panels
    private JPanel createInfoLabel(String labelText, String initialValue) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, BorderLayout.NORTH);
        
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(new Color(255, 255, 255));
        valuePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
        
        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valuePanel.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(valuePanel, BorderLayout.CENTER);
        return panel;
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
        SwingUtilities.invokeLater(() -> {
            new UseMachineForm().setVisible(true);
        });
    }
}
