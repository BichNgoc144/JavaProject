package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegistrationForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    public RegistrationForm() {
        setTitle("GAME CENTER - ĐĂNG KÝ THÀNH VIÊN");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 40));
        setLayout(new BorderLayout());

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(20, 20, 50);
                Color color2 = new Color(40, 10, 70);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username/Email field
        JLabel userLabel = new JLabel("Email đăng nhập:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField();
        styleTextField(usernameField);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Mật khẩu (8-14 số):");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm password field
        JLabel confirmPassLabel = new JLabel("Nhập lại mật khẩu:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmPassLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(confirmPassLabel, gbc);

        confirmPasswordField = new JPasswordField();
        styleTextField(confirmPasswordField);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Back button
        backButton = new JButton("QUAY LẠI");
        styleButton(backButton, new Color(100, 100, 100));
        backButton.addActionListener(e -> {
            new LoginFrom().setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);

        // Register button
        registerButton = new JButton("ĐĂNG KÝ");
        styleButton(registerButton, new Color(0, 150, 200));
        registerButton.addActionListener(this::register);
        buttonPanel.add(registerButton);

        // Footer
        JLabel footerLabel = new JLabel("Mật khẩu phải gồm 8-14 chữ số", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(150, 150, 150));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(60, 60, 70));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setCaretColor(new Color(0, 200, 200));
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setFont(new Font("Consolas", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.brighter(), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    private void register(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

        if (!isValidEmail(username)) {
            JOptionPane.showMessageDialog(this, 
                "Email không hợp lệ! Vui lòng nhập email đúng định dạng.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, 
                "Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ cái và 1 ký tự đặc biệt!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Mật khẩu nhập lại không khớp!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Check if username already exists
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Email đã được sử dụng! Vui lòng chọn email khác.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user
            String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, "user");

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrom().setVisible(true);
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-zA-Z])(?=.*[\\W_]).{8,}$");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RegistrationForm().setVisible(true);
        });
    }
}
