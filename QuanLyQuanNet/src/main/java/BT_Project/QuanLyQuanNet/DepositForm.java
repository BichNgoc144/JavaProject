package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class DepositForm extends JFrame {
    private JTextField amountField;
    private JLabel qrLabel;
    private JButton createQrButton;
    private JButton confirmButton;
    private JButton btnLogout;

    public DepositForm() {
        setTitle("Nạp Tiền - Quản lý Quán Net");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel nhập liệu & nút
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1, 10, 10));

        // Nhập số tiền
        inputPanel.add(new JLabel("Nhập số tiền nạp:"));
        amountField = new JTextField(20);
        inputPanel.add(amountField);

        // Nút tạo mã QR
        createQrButton = new JButton("Tạo Mã QR");
        inputPanel.add(createQrButton);

        // Nút xác nhận
        confirmButton = new JButton("Xác nhận");
        inputPanel.add(confirmButton);

        add(inputPanel, BorderLayout.NORTH);

        // Hiển thị mã QR
        qrLabel = new JLabel("", SwingConstants.CENTER);
        add(qrLabel, BorderLayout.CENTER);

        // Nút Home (thoát về giao diện user)
        btnLogout = new JButton("Home");
        add(btnLogout, BorderLayout.SOUTH);

        // Lắng nghe sự kiện tạo mã QR
        createQrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateQRCode();
            }
        });

        // Lắng nghe sự kiện xác nhận nạp tiền
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processDeposit();
            }
        });

        // Nút Home
        btnLogout.addActionListener(e -> {
            new GiaoDienUser().setVisible(true);
            dispose();  // Đóng form hiện tại
        });
    }

    // Tạo mã QR từ số tiền người dùng nhập
    private void generateQRCode() {
        try {
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

            // Tạo mã QR chứa thông tin về số tiền
            String qrData = "Số tiền nạp: " + amount;
            int size = 200; // Kích thước mã QR
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, size, size, hints);
            ImageIcon qrImage = new ImageIcon(toBufferedImage(matrix));
            qrLabel.setIcon(qrImage);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã QR!");
        }
    }

    // Chuyển BitMatrix thành BufferedImage
    private java.awt.Image toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
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

        int userId = 1; // Giả sử ID user là 1 (cần lấy từ đăng nhập thực tế nếu có)

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO deposit (user_id, amount) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setDouble(2, money);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Nạp tiền thành công: " + money + " VNĐ");
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
        qrLabel.setIcon(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DepositForm().setVisible(true);
        });
    }
}
