package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;

public class LoginFrom extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JButton registerButton;
    private JLabel headerLabel;
    private JLabel errorLabel;
    private int loginAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private Timer lockoutTimer;

    public LoginFrom() {
        setTitle("GAME CENTER - PLAYER LOGIN");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 40));

        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(40, 40, 50));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        headerLabel = new JLabel("PLAYER LOGIN", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 200, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(headerLabel, gbc);

        // Error label
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(255, 80, 80));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 1;
        mainPanel.add(errorLabel, gbc);

        // Username field
        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField();
        styleTextField(usernameField);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password field
        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Show password checkbox
        JCheckBox showPassCheck = new JCheckBox("Show password");
        showPassCheck.setForeground(Color.WHITE);
        showPassCheck.setOpaque(false);
        showPassCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPassCheck.isSelected() ? '\0' : '•');
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(showPassCheck, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Login button
        loginButton = createGamingButton("LOGIN", new Color(0, 150, 200));
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);

        // Register button
        registerButton = createGamingButton("REGISTER", new Color(100, 100, 100));
        registerButton.addActionListener(e -> {
            new RegistrationForm().setVisible(true);
            dispose();
        });
        buttonPanel.add(registerButton);

        // Exit button
        exitButton = createGamingButton("EXIT", new Color(200, 50, 50));
        exitButton.addActionListener(e -> exitApplication());
        buttonPanel.add(exitButton);

        // Footer
        JLabel footerLabel = new JLabel("Forgot password? Contact admin", SwingConstants.CENTER);
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(footerLabel, BorderLayout.SOUTH);

        // Enter key listener
        passwordField.addActionListener(e -> login());
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 40));
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 200)));

        // Title label
        JLabel titleLabel = new JLabel("  GAME CENTER LOGIN", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 255, 255));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Close button
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(80, 20, 20));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setOpaque(true);
        closeBtn.setPreferredSize(new Dimension(40, 40));
        closeBtn.addActionListener(e -> exitApplication());
        
        titleBar.add(closeBtn, BorderLayout.EAST);

        // Make draggable
        final Point[] initialClick = new Point[1];
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick[0].x;
                int yMoved = e.getY() - initialClick[0].y;

                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        return titleBar;
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

    private JButton createGamingButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button glow effect
                if (getModel().isRollover()) {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                // Button background
                GradientPaint gp = new GradientPaint(0, 0, baseColor, 0, getHeight(), baseColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Button border
                g2.setColor(baseColor.brighter());
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Consolas", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getDefaultCursor());
            }
        });
        
        return button;
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            showError("Please enter both username and password!");
            return;
        }

        if (loginAttempts >= MAX_ATTEMPTS) {
            showError("Account locked! Try again later.");
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT id, password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	int userId = rs.getInt("id");
                String dbPassword = rs.getString("password");
                String role = rs.getString("role");

                if (dbPassword.equals(new String(password))) {
                    // Successful login
                    loginAttempts = 0;
                    JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (role.equalsIgnoreCase("admin")) {
                        new GiaoDienAdmin().setVisible(true);
                    } else {
                        new GiaoDienUser(userId).setVisible(true);
                    }
                    dispose();
                } else {
                    // Wrong password
                    loginAttempts++;
                    int remaining = MAX_ATTEMPTS - loginAttempts;
                    showError("Invalid password! " + remaining + " attempts remaining");
                    
                    if (loginAttempts >= MAX_ATTEMPTS) {
                        lockAccount(username);
                        startLockoutTimer();
                    }
                }
            } else {
                showError("Account not found!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        } finally {
            Arrays.fill(password, '0'); // Clear password from memory
        }
    }

    private void lockAccount(String username) {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "UPDATE users SET is_locked = TRUE WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void startLockoutTimer() {
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        
        // Hủy timer cũ nếu tồn tại
        if (lockoutTimer != null) {
            lockoutTimer.stop();
        }
        
        // Tạo timer mới với Swing Timer
        lockoutTimer = new Timer(300000, e -> { // 300000 ms = 5 phút
            loginAttempts = 0;
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
            errorLabel.setText(" ");
            ((Timer)e.getSource()).stop(); // Dừng timer sau khi chạy
        });
        
        lockoutTimer.setRepeats(false); // Chỉ chạy 1 lần
        lockoutTimer.start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        Toolkit.getDefaultToolkit().beep(); // Error sound
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Exit Game Center?", 
            "Confirm Exit", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            LoginFrom loginForm = new LoginFrom();
            loginForm.setVisible(true);
        });
    }
}
