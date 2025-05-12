package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import javax.swing.table.DefaultTableModel;

public class UseMachineForm extends JFrame {
    private JTable machineTable;
    private JLabel totalLabel;
    private JButton startButton;
    private JButton stopButton;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double pricePerHour;
    private JButton btnLogout;

    public UseMachineForm() {
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
            new GiaoDienUser().setVisible(true);
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
            stmt.setInt(1, 1);  // Giả sử ID người dùng là 1
            stmt.setString(2, selectedMachineName);
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            stmt.setBigDecimal(4, new java.math.BigDecimal(0));  // Ban đầu là 0

            stmt.executeUpdate();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
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

        String selectedMachineName = (String) machineTable.getValueAt(selectedRow, 2);
        endTime = LocalDateTime.now();

        long minutesUsed = java.time.Duration.between(startTime, endTime).toMinutes();
        double hoursUsed = minutesUsed / 60.0;
        double totalAmount = hoursUsed * pricePerHour;
        if (totalAmount < pricePerHour / 2) {
            totalAmount = pricePerHour / 2;  // Tính nửa giá nếu dùng dưới 30 phút
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật machine_usage
            String sqlUpdateUsage = "UPDATE machine_usage SET end_time = ?, total_amount = ? " +
                    "WHERE user_id = ? AND machine_name = ? AND end_time IS NULL";
            try (PreparedStatement stmtUsage = conn.prepareStatement(sqlUpdateUsage)) {
                stmtUsage.setTimestamp(1, Timestamp.valueOf(endTime));
                stmtUsage.setBigDecimal(2, new java.math.BigDecimal(totalAmount));
                stmtUsage.setInt(3, 1);  // Giả sử user_id = 1
                stmtUsage.setString(4, selectedMachineName);
                stmtUsage.executeUpdate();
            }

            // Tính số dư
            double totalDeposit = 0;
            String sqlDeposit = "SELECT COALESCE(SUM(amount), 0) AS total_deposit FROM deposit WHERE user_id = ?";
            try (PreparedStatement stmtDeposit = conn.prepareStatement(sqlDeposit)) {
                stmtDeposit.setInt(1, 1);
                try (ResultSet rs = stmtDeposit.executeQuery()) {
                    if (rs.next()) {
                        totalDeposit = rs.getDouble("total_deposit");
                    }
                }
            }

            double totalUsage = 0;
            String sqlUsage = "SELECT COALESCE(SUM(total_amount), 0) AS total_usage FROM machine_usage WHERE user_id = ?";
            try (PreparedStatement stmtUsageSum = conn.prepareStatement(sqlUsage)) {
                stmtUsageSum.setInt(1, 1);
                try (ResultSet rs = stmtUsageSum.executeQuery()) {
                    if (rs.next()) {
                        totalUsage = rs.getDouble("total_usage");
                    }
                }
            }

            double remaining = totalDeposit - totalUsage;
            if (remaining < 0) remaining = 0;

            conn.commit();

            totalLabel.setText(String.format("Tổng tiền: %.0f VNĐ", totalAmount));
            String message = String.format(
                    "Hóa đơn sử dụng máy: %s\n" +
                            "-----------------------------\n" +
                            "Tiền đã nạp: %.0f VNĐ\n" +
                            "Tiền sử dụng: %.0f VNĐ\n" +
                            "Số dư còn lại: %.0f VNĐ",
                    selectedMachineName,
                    totalDeposit,
                    totalAmount,
                    remaining
            );
            JOptionPane.showMessageDialog(this, message);

            startButton.setEnabled(true);
            stopButton.setEnabled(false);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kết thúc sử dụng máy!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UseMachineForm().setVisible(true);
        });
    }
}
