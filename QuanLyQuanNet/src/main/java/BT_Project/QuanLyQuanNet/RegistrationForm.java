package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField; // Nhập lại mật khẩu
    private JButton registerButton;

    public RegistrationForm() {
        setTitle("Đăng ký - Quản lý Quán Net");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Tên đăng nhập (Email):"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Nhập lại mật khẩu:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        registerButton = new JButton("Đăng ký");
        panel.add(new JLabel()); // ô trống
        panel.add(registerButton);

        add(panel);

        // Xử lý sự kiện khi bấm nút Đăng ký
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
    }

    private void register() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

        if (!isValidEmail(username)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
            return;
        }

        // Kiểm tra mật khẩu hợp lệ (số, 8 đến 14 ký tự)
        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải là số và từ 8 đến 14 ký tự!");
            return;
        }

        // Kiểm tra mật khẩu nhập lại có khớp không
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
            return;
        }

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra đăng ký vào database (thêm vào DB)
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, "user"); // Mặc định vai trò là user

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                dispose();  // Đóng form đăng ký sau khi đăng ký thành công
                new LoginFrom().setVisible(true);  // Quay lại trang đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kiểm tra email hợp lệ bằng regex
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Kiểm tra mật khẩu hợp lệ (số, 8-14 ký tự)
    private boolean isValidPassword(String password) {
        return password.matches("[0-9]{8,14}");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationForm().setVisible(true);
        });
    }
}
