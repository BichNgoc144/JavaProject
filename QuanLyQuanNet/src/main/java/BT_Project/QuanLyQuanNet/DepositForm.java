package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class DepositForm extends JFrame {
    private int userId;
    private JTextField amountField;
    private JLabel currentBalanceLabel;
    
    private final Color DARK_BG = new Color(15, 15, 25);
    private final Color NEON_BLUE = new Color(0, 200, 255);
    private final Color NEON_GREEN = new Color(0, 255, 100);
    private final Color NEON_PURPLE = new Color(170, 0, 255);
    private final Color NEON_RED = new Color(255, 50, 50);

    public DepositForm() {
        this(1);
    }

    public DepositForm(int userId) {
        this.userId = userId;
        
        // Frame setup
        setTitle("ADD FUNDS - GAME NET SYSTEM");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(DARK_BG);
        
        JLabel titleLabel = new JLabel("NAP TIEN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 28));
        titleLabel.setForeground(NEON_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);
        
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balancePanel.setOpaque(false);
        balancePanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(50, 50, 80), 2),
            new EmptyBorder(15, 30, 15, 30)
        ));
        
        JLabel balanceTitle = new JLabel("TỔNG TIỀN", SwingConstants.CENTER);
        balanceTitle.setFont(new Font("Arial", Font.BOLD, 16));
        balanceTitle.setForeground(new Color(200, 200, 200));
        balanceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        currentBalanceLabel = new JLabel("", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentBalanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBalanceDisplay();
        
        balancePanel.add(balanceTitle);
        balancePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        balancePanel.add(currentBalanceLabel);
        mainPanel.add(balancePanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Amount input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel amountLabel = new JLabel("NHẬP SỐ TIỀN (VND):", SwingConstants.CENTER);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(amountLabel);
        
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.BOLD, 18));
        amountField.setHorizontalAlignment(JTextField.CENTER);
        amountField.setMaximumSize(new Dimension(300, 40));
        amountField.setBorder(new CompoundBorder(
            new LineBorder(NEON_BLUE, 2),
            new EmptyBorder(5, 10, 5, 10)
        ));
        amountField.setBackground(new Color(30, 30, 50));
        amountField.setForeground(Color.WHITE);
        amountField.setCaretColor(NEON_BLUE);
        inputPanel.add(amountField);
        
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(400, 60));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton confirmButton = createGamingButton("XÁC NHẬN", NEON_GREEN);
        confirmButton.addActionListener(e -> processDeposit());
        
        JButton homeButton = createGamingButton("QUAY LẠI", NEON_PURPLE);
        homeButton.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true);
            dispose();
        });
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(homeButton);
        mainPanel.add(buttonPanel);
        
        // Instructions panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        JLabel infoTitle = new JLabel("HƯỚNG DẪN", SwingConstants.CENTER);
        infoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        infoTitle.setForeground(NEON_BLUE);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoTitle);
        
        JTextArea infoText = new JTextArea(
            "1. Nhập số tiền bạn muốn nạp\n" +
            "2. Nhấn nút xác nhận để nạp tiền\n" +
            "3. Số tiền nạp tối đa: 1,000,000 VND\n" +
            "4. Số tiền nạp tối thiểu: 10,000 VND"
        );
        infoText.setFont(new Font("Courier New", Font.PLAIN, 12));
        infoText.setForeground(new Color(180, 180, 180));
        infoText.setOpaque(false);
        infoText.setEditable(false);
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoText.setBorder(new EmptyBorder(10, 0, 0, 0));
        infoPanel.add(infoText);
        
        mainPanel.add(infoPanel);
        
        add(mainPanel);
    }

    private JButton createGamingButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Button background with gradient
                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, baseColor.brighter(), 
                                                       0, getHeight(), baseColor);
                    g2.setPaint(gp);
                } else {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), 
                                     baseColor.getBlue(), 150));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Button border with glow effect
                g2.setColor(baseColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Button text
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
        button.setPreferredSize(new Dimension(150, 50));
        
        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }

    private void updateBalanceDisplay() {
        double currentBalance = getCurrentBalance();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        currentBalanceLabel.setText(currencyFormatter.format(currentBalance));
        currentBalanceLabel.setForeground(currentBalance > 0 ? NEON_GREEN : Color.WHITE);
    }

    private double getCurrentBalance() {
        double currentBalance = 0;
        
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Calculate total deposits
            String sqlDeposit = "SELECT COALESCE(SUM(amount), 0) AS total_deposit FROM deposit WHERE user_id = ?";
            try (PreparedStatement stmtDeposit = conn.prepareStatement(sqlDeposit)) {
                stmtDeposit.setInt(1, userId);
                try (ResultSet rs = stmtDeposit.executeQuery()) {
                    if (rs.next()) {
                        currentBalance = rs.getDouble("total_deposit");
                    }
                }
            }
            
            // Subtract total usage
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

    private void processDeposit() {
        String amountText = amountField.getText().trim();
        
        if (amountText.isEmpty()) {
            showError("Please enter an amount!");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            showError("Invalid amount format!");
            return;
        }
        
        // Validate amount
        if (amount <= 0) {
            showError("Amount must be greater than 0!");
            return;
        }
        
        if (amount < 10000) {
            showError("Minimum deposit is 10,000 VND!");
            return;
        }
        
        // Check balance limit
        double currentBalance = getCurrentBalance();
        if (currentBalance + amount > 1000000) {
            showError(String.format(
                "Cannot deposit %,.0f VND. Current balance: %,.0f VND.\nMaximum balance limit is 1,000,000 VND.",
                amount, currentBalance
            ));
            return;
        }
        
        // Process deposit
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO deposit (user_id, amount) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showSuccess(String.format(
                    "Successfully deposited %,.0f VND\nNew balance: %,.0f VND",
                    amount, currentBalance + amount
                ));
                amountField.setText("");
                updateBalanceDisplay();
            } else {
                showError("Deposit failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database connection error!");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DepositForm().setVisible(true);
        });
    }
}