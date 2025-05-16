package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DepositForm extends JFrame {
	private int userId;
	
    private JTextField amountField;
    private JButton confirmButton;
    private JButton btnLogout;

    public DepositForm() {
        this(1);
    }
    public DepositForm(int userid) {
    	this.userId=userid;
        setTitle("Nạp Tiền - Quản lý Quán Net");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel nhập liệu & nút
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // Nhập số tiền
        inputPanel.add(new JLabel("Nhập số tiền nạp:"));
        amountField = new JTextField(20);
        inputPanel.add(amountField);

        // Nút xác nhận
        confirmButton = new JButton("Xác nhận nạp tiền");
        inputPanel.add(confirmButton);

        add(inputPanel, BorderLayout.NORTH);

        // Panel thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        
        // Tạo nhãn thông tin
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h3>Hướng dẫn nạp tiền</h3>"
                + "<p>1. Nhập số tiền cần nạp</p>"
                + "<p>2. Nhấn nút 'Xác nhận nạp tiền'</p>"
                + "<p>3. Số dư tối đa cho phép là 1,000,000 VNĐ</p>"
                + "</div></html>", SwingConstants.CENTER);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        add(infoPanel, BorderLayout.CENTER);

        // Nút Home (thoát về giao diện user)
        btnLogout = new JButton("Home");
        add(btnLogout, BorderLayout.SOUTH);

        // Lắng nghe sự kiện xác nhận nạp tiền
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processDeposit();
            }
        });

        // Nút Home
        btnLogout.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true);
            dispose();  // Đóng form hiện tại
        });
    }

    // Kiểm tra số dư hiện tại
    private double getCurrentBalance() {
        double currentBalance = 0;
        
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Tính tổng số tiền đã nạp
            String sqlDeposit = "SELECT COALESCE(SUM(amount), 0) AS total_deposit FROM deposit WHERE user_id = ?";
            try (PreparedStatement stmtDeposit = conn.prepareStatement(sqlDeposit)) {
                stmtDeposit.setInt(1, userId);
                try (ResultSet rs = stmtDeposit.executeQuery()) {
                    if (rs.next()) {
                        currentBalance = rs.getDouble("total_deposit");
                    }
                }
            }
            
            // Trừ tổng số tiền đã sử dụng
            String sqlUsage = "SELECT COALESCE(SUM(total_amount), 0) AS total_usage FROM machine_usage WHERE user_id = ?";
            try (PreparedStatement stmtUsageSum = conn.prepareStatement(sqlUsage)) {
                stmtUsageSum.setInt(1, userId);
                try (ResultSet rs = stmtUsageSum.executeQuery()) {
                    if (rs.next()) {
                        currentBalance -= rs.getDouble("total_usage");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return currentBalance;
    }

    // Xử lý nạp tiền khi người dùng xác nhận
    private void processDeposit() {
        String amount = amountField.getText().trim();
        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!");
            return;
        }
        double money = 0;
        try {
            money = Double.parseDouble(amount);
            if (money <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!");
            return;
        }

        // Kiểm tra số dư sau khi nạp không vượt quá 1 triệu
        double currentBalance = getCurrentBalance();
        if (currentBalance + money > 1000000) {
            JOptionPane.showMessageDialog(
                this, 
                String.format("Không thể nạp %,.0f VNĐ. Số dư hiện tại: %,.0f VNĐ.\nSố dư sau khi nạp không được vượt quá 1,000,000 VNĐ.", 
                money, currentBalance), 
                "Vượt quá hạn mức", 
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO deposit (user_id, amount) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setDouble(2, money);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    this, 
                    String.format("Nạp tiền thành công: %,.0f VNĐ\nSố dư hiện tại: %,.0f VNĐ", 
                    money, currentBalance + money)
                );
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi nạp tiền!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Làm mới trường nhập liệu
    private void clearFields() {
        amountField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DepositForm().setVisible(true);
        });
    }
}
