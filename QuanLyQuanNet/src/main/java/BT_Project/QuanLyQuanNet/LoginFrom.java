package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrom extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton; // Nút thoát

    public LoginFrom() {
        setTitle("Đăng nhập - Quản lý Quán Net");
        setSize(400, 250);  // Kích thước form
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sử dụng GridLayout để căn chỉnh các ô nhập liệu và các nút ngang nhau
        setLayout(new GridLayout(5, 2, 10, 10));  // 5 hàng, 2 cột, khoảng cách 10px

        add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Đăng nhập");
        add(loginButton);

        exitButton = new JButton("Thoát");
        add(exitButton);

        // Xử lý sự kiện khi bấm nút Đăng nhập
        loginButton.addActionListener(e -> login());

        // Xử lý sự kiện khi bấm nút Thoát
        exitButton.addActionListener(e -> exitApplication());
    }

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);  // Chỉ truyền username vào câu lệnh
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password"); // Lấy mật khẩu từ DB
                String role = rs.getString("role");  // Lấy role từ DB

                // Kiểm tra mật khẩu nhập và mật khẩu trong DB
                if (dbPassword.equals(password)) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công ");
                    if (role.equalsIgnoreCase("admin")) {
                        new GiaoDienAdmin().setVisible(true);
                    } else {
                        new GiaoDienUser().setVisible(true);
                    }
                    dispose(); // Đóng form đăng nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Sai mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();  // In chi tiết lỗi ra console
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void exitApplication() {
        // Đóng cửa sổ đăng nhập và thoát ứng dụng
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn thoát không?", "Thoát", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);  // Đóng ứng dụng
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrom().setVisible(true);
        });
    }
}
